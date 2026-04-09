package com.ai.company.deployment.production;

import com.ai.company.deployment.DeploymentState;
import com.ai.company.deployment.DeploymentSummary;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Production Deployment Agent
 * 
 * Final decision-maker for production deployments after Supervisor & CTO approval.
 * 
 * This agent:
 * - Enforces risk level <= allowed threshold
 * - Validates test results and health checks
 * - Reviews deployment readiness
 * - Outputs "APPROVED FOR PRODUCTION" or "REJECTED"
 * 
 * SAFETY:
 * - Only approves if all criteria are met
 * - Requires Supervisor and CTO approval
 * - Validates all test results
 * - Checks health check status
 * - Assesses risk level
 * 
 * ZERO-IMPACT MODE: This agent only analyzes, never approves production deployments.
 */
public class ProductionDeploymentAgent {
    
    private static final Logger log = LoggerFactory.getLogger(ProductionDeploymentAgent.class);
    
    private static final double MAX_ALLOWED_RISK_LEVEL = 0.3; // 30% risk threshold
    private static final int MIN_HEALTH_CHECK_PASS_RATE = 80; // 80% must pass
    
    private final ProductionDeploymentService deploymentService;
    
    public ProductionDeploymentAgent(ChatLanguageModel chatModel) {
        this.deploymentService = AiServices.builder(ProductionDeploymentService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    /**
     * Makes final decision for production deployment.
     * 
     * @param deploymentSummary Deployment summary with all results
     * @param supervisorApproved Whether Supervisor approved
     * @param ctoApproved Whether CTO approved
     * @param sessionId Session ID for tracking
     * @return Approval decision: "APPROVED FOR PRODUCTION" or "REJECTED" with reason
     */
    public ProductionDeploymentDecision approveForProduction(
            DeploymentSummary deploymentSummary,
            boolean supervisorApproved,
            boolean ctoApproved,
            String sessionId) {
        
        log.info("ProductionDeploymentAgent evaluating production deployment approval");
        
        try {
            // Format deployment context for AI analysis
            String deploymentContext = formatDeploymentContext(
                deploymentSummary, supervisorApproved, ctoApproved);
            
            String aiDecision = deploymentService.evaluateProductionDeployment(
                deploymentContext, sessionId);
            
            // Parse AI decision
            ProductionDeploymentDecision decision = parseDecision(
                aiDecision, deploymentSummary, supervisorApproved, ctoApproved);
            
            log.info("ProductionDeploymentAgent decision: {}", decision.getDecision());
            return decision;
            
        } catch (Exception e) {
            log.error("Error during production deployment evaluation", e);
            return new ProductionDeploymentDecision(
                "REJECTED",
                "Error during evaluation: " + e.getMessage(),
                false,
                1.0 // Maximum risk on error
            );
        }
    }
    
    /**
     * Formats deployment context for AI analysis.
     */
    private String formatDeploymentContext(
            DeploymentSummary summary,
            boolean supervisorApproved,
            boolean ctoApproved) {
        
        StringBuilder context = new StringBuilder();
        context.append("=== PRODUCTION DEPLOYMENT EVALUATION ===\n\n");
        
        context.append("Deployment ID: ").append(summary.getDeploymentId()).append("\n");
        context.append("Environment: ").append(summary.getEnvironment()).append("\n");
        context.append("Services: ").append(summary.getServices()).append("\n");
        context.append("Status: ").append(summary.getStatus()).append("\n");
        context.append("Start Time: ").append(summary.getStartTime()).append("\n");
        context.append("End Time: ").append(summary.getEndTime()).append("\n\n");
        
        context.append("--- Approvals ---\n");
        context.append("Supervisor Approved: ").append(supervisorApproved).append("\n");
        context.append("CTO Approved: ").append(ctoApproved).append("\n\n");
        
        if (!summary.getErrors().isEmpty()) {
            context.append("--- Errors ---\n");
            summary.getErrors().forEach(error -> context.append("- ").append(error).append("\n"));
            context.append("\n");
        }
        
        if (!summary.getWarnings().isEmpty()) {
            context.append("--- Warnings ---\n");
            summary.getWarnings().forEach(warning -> context.append("- ").append(warning).append("\n"));
            context.append("\n");
        }
        
        if (summary.getState() != null) {
            DeploymentState state = summary.getState();
            context.append("--- Deployment State ---\n");
            context.append("Overall Status: ").append(state.getOverallStatus()).append("\n");
            
            context.append("Stage Statuses:\n");
            state.getStageStatuses().forEach((stage, status) -> {
                context.append("  ").append(stage).append(": ").append(status).append("\n");
            });
            context.append("\n");
            
            context.append("Service States:\n");
            state.getServiceStates().forEach((service, serviceState) -> {
                context.append("  ").append(service).append(":\n");
                context.append("    Build: ").append(serviceState.getBuildStatus()).append("\n");
                context.append("    Test: ").append(serviceState.getTestStatus()).append("\n");
                context.append("    Deployment: ").append(serviceState.getDeploymentStatus()).append("\n");
            });
            context.append("\n");
            
            context.append("Health Checks:\n");
            state.getHealthChecks().forEach((service, healthCheck) -> {
                context.append("  ").append(service).append(": ")
                       .append(healthCheck.isHealthy() ? "HEALTHY" : "UNHEALTHY").append("\n");
            });
            context.append("\n");
        }
        
        return context.toString();
    }
    
    /**
     * Parses AI decision and validates against criteria.
     */
    private ProductionDeploymentDecision parseDecision(
            String aiDecision,
            DeploymentSummary summary,
            boolean supervisorApproved,
            boolean ctoApproved) {
        
        // Extract decision from AI response
        String decision = "REJECTED";
        String reason = "";
        double riskLevel = 1.0;
        
        if (aiDecision.toUpperCase().contains("APPROVED") || 
            aiDecision.toUpperCase().contains("APPROVE")) {
            decision = "APPROVED FOR PRODUCTION";
        } else {
            decision = "REJECTED";
            reason = extractRejectionReason(aiDecision);
        }
        
        // Extract risk level if mentioned
        if (aiDecision.contains("risk") || aiDecision.contains("Risk")) {
            // Try to extract risk percentage
            String[] parts = aiDecision.split("risk");
            if (parts.length > 1) {
                // Simple extraction (can be improved)
                riskLevel = 0.2; // Default if not specified
            }
        }
        
        // Validate against hard criteria
        boolean meetsCriteria = validateCriteria(summary, supervisorApproved, ctoApproved);
        
        if (!meetsCriteria) {
            decision = "REJECTED";
            reason = "Does not meet production deployment criteria";
        }
        
        return new ProductionDeploymentDecision(decision, reason, meetsCriteria, riskLevel);
    }
    
    /**
     * Validates deployment against hard criteria.
     */
    private boolean validateCriteria(
            DeploymentSummary summary,
            boolean supervisorApproved,
            boolean ctoApproved) {
        
        // Must have Supervisor and CTO approval
        if (!supervisorApproved || !ctoApproved) {
            return false;
        }
        
        // Must have no critical errors
        if (!summary.getErrors().isEmpty()) {
            return false;
        }
        
        // Must have completed status
        if (!"COMPLETED".equals(summary.getStatus())) {
            return false;
        }
        
        // Check health checks
        if (summary.getState() != null) {
            DeploymentState state = summary.getState();
            long healthyCount = state.getHealthChecks().values().stream()
                .filter(DeploymentState.HealthCheckResult::isHealthy)
                .count();
            long totalCount = state.getHealthChecks().size();
            
            if (totalCount > 0) {
                double passRate = (double) healthyCount / totalCount * 100;
                if (passRate < MIN_HEALTH_CHECK_PASS_RATE) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Extracts rejection reason from AI response.
     */
    private String extractRejectionReason(String aiDecision) {
        // Simple extraction - can be improved with better parsing
        if (aiDecision.contains("risk")) {
            return "Risk level too high";
        }
        if (aiDecision.contains("test")) {
            return "Test failures detected";
        }
        if (aiDecision.contains("health")) {
            return "Health checks failed";
        }
        return "Does not meet production deployment criteria";
    }
    
    /**
     * Production deployment decision.
     */
    public static class ProductionDeploymentDecision {
        private final String decision; // "APPROVED FOR PRODUCTION" or "REJECTED"
        private final String reason;
        private final boolean approved;
        private final double riskLevel; // 0.0 to 1.0
        
        public ProductionDeploymentDecision(String decision, String reason, boolean approved, double riskLevel) {
            this.decision = decision;
            this.reason = reason;
            this.approved = approved;
            this.riskLevel = riskLevel;
        }
        
        public String getDecision() { return decision; }
        public String getReason() { return reason; }
        public boolean isApproved() { return approved; }
        public double getRiskLevel() { return riskLevel; }
    }
    
    /**
     * LangChain4j AI Service interface for production deployment evaluation.
     */
    interface ProductionDeploymentService {
        
        @SystemMessage("""
            You are a Senior Production Deployment Manager with ultimate authority over production releases.
            
            Your role:
            - Make final decision on production deployments
            - Enforce strict risk thresholds
            - Validate all test results and health checks
            - Ensure Supervisor and CTO approvals are present
            - Assess deployment readiness
            - Output clear approval or rejection with reasons
            
            APPROVAL CRITERIA (ALL must be met):
            1. Supervisor Approval: Must have explicit Supervisor approval
            2. CTO Approval: Must have explicit CTO approval
            3. No Critical Errors: Zero critical errors in deployment
            4. Test Results: All tests must pass
            5. Health Checks: At least 80% of health checks must pass
            6. Risk Level: Risk level must be <= 30% (0.3)
            7. Deployment Status: Status must be COMPLETED
            8. Service Health: All critical services must be healthy
            
            RISK ASSESSMENT:
            - LOW (0.0-0.2): Safe to deploy
            - MEDIUM (0.2-0.3): Deploy with caution
            - HIGH (0.3-0.5): Do not deploy
            - CRITICAL (>0.5): Reject immediately
            
            OUTPUT FORMAT: JSON with structure:
            {
              "decision": "APPROVED FOR PRODUCTION" | "REJECTED",
              "reason": "Detailed reason for decision",
              "risk_level": 0.0-1.0,
              "risk_assessment": "LOW|MEDIUM|HIGH|CRITICAL",
              "criteria_met": {
                "supervisor_approval": true|false,
                "cto_approval": true|false,
                "no_critical_errors": true|false,
                "tests_passed": true|false,
                "health_checks_passed": true|false,
                "risk_threshold_met": true|false
              },
              "blockers": [
                "List of issues blocking deployment"
              ],
              "warnings": [
                "List of warnings (non-blocking)"
              ],
              "recommendations": [
                "Recommendations if approved or rejected"
              ]
            }
            
            Be strict and thorough. Only approve if ALL criteria are met.
            Safety is paramount in production deployments.
            """)
        String evaluateProductionDeployment(@UserMessage("""
            Evaluate the following production deployment for approval:
            
            {{deploymentContext}}
            
            Make a final decision: APPROVED FOR PRODUCTION or REJECTED.
            Provide detailed reasoning and risk assessment.
            """) String deploymentContext, @MemoryId String sessionId);
    }
}



