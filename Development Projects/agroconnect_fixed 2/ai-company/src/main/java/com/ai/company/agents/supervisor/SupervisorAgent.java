package com.ai.company.agents.supervisor;

import com.ai.company.impact.ImpactMode;
import com.ai.company.impact.ImpactModeManager;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supervisor Agent - Ultimate authority for safety and compliance.
 * 
 * The Supervisor Agent:
 * - Approves or rejects decisions from all other agents
 * - Enforces zero-impact mode and company constraints
 * - Stops invalid or harmful suggestions
 * - Overrides unsafe workflows
 * - Ensures consistency across all agent outputs
 * 
 * This agent operates at the highest level of authority and has the power
 * to reject any agent output that violates safety rules or company policies.
 */
public class SupervisorAgent {
    
    private static final Logger log = LoggerFactory.getLogger(SupervisorAgent.class);
    
    private final SupervisorService supervisorService;
    
    /**
     * Creates a new Supervisor Agent.
     * 
     * @param chatModel The chat language model to use
     */
    public SupervisorAgent(ChatLanguageModel chatModel) {
        this.supervisorService = AiServices.builder(SupervisorService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
        
        log.info("SupervisorAgent initialized");
    }
    
    /**
     * Reviews and approves/rejects an agent decision.
     * 
     * @param agentName The name of the agent making the decision
     * @param decision The decision to review
     * @param context Additional context
     * @param sessionId Session ID for context
     * @return Approval decision (APPROVED, REJECTED, or MODIFICATION_REQUIRED)
     */
    public SupervisorDecision reviewDecision(String agentName, String decision, String context, String sessionId) {
        log.info("Supervisor reviewing decision from agent: {}", agentName);
        
        String review = supervisorService.reviewDecision(agentName, decision, context, sessionId);
        
        // Parse review to extract decision
        SupervisorDecision result = parseDecision(review, agentName, sessionId);
        
        log.info("Supervisor decision: {} for agent: {}", result.getStatus(), agentName);
        
        return result;
    }
    
    /**
     * Enforces company constraints on a proposal.
     * 
     * @param proposal The proposal to check
     * @param sessionId Session ID
     * @return Constraint enforcement result
     */
    public ConstraintEnforcement enforceConstraints(String proposal, String sessionId) {
        log.info("Supervisor enforcing constraints on proposal");
        
        String enforcement = supervisorService.enforceConstraints(proposal, sessionId);
        
        ConstraintEnforcement result = parseConstraintEnforcement(enforcement, sessionId);
        
        log.info("Constraint enforcement: {} violations found", result.getViolations().size());
        
        return result;
    }
    
    /**
     * Checks if a workflow is safe to execute.
     * 
     * @param workflowName The workflow name
     * @param workflowPlan The workflow plan
     * @param sessionId Session ID
     * @return Safety assessment
     */
    public SafetyAssessment assessWorkflowSafety(String workflowName, String workflowPlan, String sessionId) {
        log.info("Supervisor assessing workflow safety: {}", workflowName);
        
        String assessment = supervisorService.assessWorkflowSafety(workflowName, workflowPlan, sessionId);
        
        SafetyAssessment result = parseSafetyAssessment(assessment, workflowName, sessionId);
        
        log.info("Workflow safety assessment: {} - {}", workflowName, result.getSafetyLevel());
        
        return result;
    }
    
    /**
     * Performs consistency check across multiple agent outputs.
     * 
     * @param outputs Map of agent names to their outputs
     * @param sessionId Session ID
     * @return Consistency check result
     */
    public ConsistencyCheck checkConsistency(java.util.Map<String, String> outputs, String sessionId) {
        log.info("Supervisor checking consistency across {} agent outputs", outputs.size());
        
        String consistencyReport = supervisorService.checkConsistency(outputs, sessionId);
        
        ConsistencyCheck result = parseConsistencyCheck(consistencyReport, sessionId);
        
        log.info("Consistency check: {} inconsistencies found", result.getInconsistencies().size());
        
        return result;
    }
    
    /**
     * Overrides an unsafe agent decision.
     * 
     * @param agentName The agent name
     * @param originalDecision The original decision
     * @param reason The reason for override
     * @param sessionId Session ID
     * @return Override decision
     */
    public OverrideDecision overrideDecision(String agentName, String originalDecision, 
                                            String reason, String sessionId) {
        log.warn("Supervisor overriding decision from agent: {} - Reason: {}", agentName, reason);
        
        String override = supervisorService.overrideDecision(agentName, originalDecision, reason, sessionId);
        
        OverrideDecision result = parseOverrideDecision(override, agentName, sessionId);
        
        log.warn("Override decision: {} for agent: {}", result.getStatus(), agentName);
        
        return result;
    }
    
    /**
     * Changes the impact mode (only SupervisorAgent can do this).
     * 
     * @param newMode The new impact mode
     * @param reason Reason for the change
     * @return true if mode was changed successfully
     */
    public boolean changeImpactMode(ImpactMode newMode, String reason) {
        log.info("SupervisorAgent changing impact mode to: {} - Reason: {}", newMode, reason);
        
        ImpactModeManager modeManager = ImpactModeManager.getInstance();
        boolean changed = modeManager.setMode(newMode, "SupervisorAgent");
        
        if (changed) {
            log.info("Impact mode changed successfully to: {}", newMode);
        } else {
            log.warn("Failed to change impact mode to: {}", newMode);
        }
        
        return changed;
    }
    
    /**
     * Validates deployment prerequisites before allowing deployment.
     * 
     * @param deploymentContext Deployment context with all prerequisites
     * @param sessionId Session ID
     * @return Deployment validation result
     */
    public DeploymentValidation validateDeploymentPrerequisites(
            DeploymentPrerequisites prerequisites, String sessionId) {
        log.info("Supervisor validating deployment prerequisites");
        
        String validation = supervisorService.validateDeploymentPrerequisites(
            formatPrerequisites(prerequisites), sessionId);
        
        DeploymentValidation result = parseDeploymentValidation(validation, prerequisites, sessionId);
        
        log.info("Deployment validation: {}", result.isApproved() ? "APPROVED" : "REJECTED");
        
        return result;
    }
    
    /**
     * Approves or rejects staging deployment.
     * 
     * @param stagingContext Staging deployment context
     * @param sessionId Session ID
     * @return Staging deployment approval decision
     */
    public DeploymentApproval approveStagingDeployment(String stagingContext, String sessionId) {
        log.info("Supervisor evaluating staging deployment approval");
        
        String approval = supervisorService.approveStagingDeployment(stagingContext, sessionId);
        
        DeploymentApproval result = parseDeploymentApproval(approval, "staging", sessionId);
        
        log.info("Staging deployment: {}", result.isApproved() ? "APPROVED" : "REJECTED");
        
        return result;
    }
    
    /**
     * Approves or rejects production deployment.
     * 
     * @param productionContext Production deployment context
     * @param sessionId Session ID
     * @return Production deployment approval decision
     */
    public DeploymentApproval approveProductionDeployment(String productionContext, String sessionId) {
        log.info("Supervisor evaluating production deployment approval");
        
        String approval = supervisorService.approveProductionDeployment(productionContext, sessionId);
        
        DeploymentApproval result = parseDeploymentApproval(approval, "production", sessionId);
        
        log.info("Production deployment: {}", result.isApproved() ? "APPROVED" : "REJECTED");
        
        return result;
    }
    
    /**
     * Rejects unsafe changes.
     * 
     * @param changeDescription Description of the change
     * @param riskAssessment Risk assessment
     * @param sessionId Session ID
     * @return Rejection decision with reasons
     */
    public UnsafeChangeRejection rejectUnsafeChanges(
            String changeDescription, String riskAssessment, String sessionId) {
        log.warn("Supervisor evaluating unsafe changes");
        
        String rejection = supervisorService.rejectUnsafeChanges(changeDescription, riskAssessment, sessionId);
        
        UnsafeChangeRejection result = parseUnsafeChangeRejection(rejection, sessionId);
        
        log.warn("Unsafe changes: {}", result.isRejected() ? "REJECTED" : "ALLOWED");
        
        return result;
    }
    
    /**
     * Rejects deployment due to insufficient test coverage.
     * 
     * @param testCoverageReport Test coverage report
     * @param minimumThreshold Minimum test coverage threshold
     * @param sessionId Session ID
     * @return Test coverage rejection decision
     */
    public TestCoverageRejection rejectInsufficientTestCoverage(
            String testCoverageReport, double minimumThreshold, String sessionId) {
        log.warn("Supervisor evaluating test coverage");
        
        String rejection = supervisorService.rejectInsufficientTestCoverage(
            testCoverageReport, minimumThreshold, sessionId);
        
        TestCoverageRejection result = parseTestCoverageRejection(rejection, minimumThreshold, sessionId);
        
        log.warn("Test coverage: {}", result.isRejected() ? "REJECTED" : "APPROVED");
        
        return result;
    }
    
    /**
     * Formats deployment prerequisites for AI analysis.
     */
    private String formatPrerequisites(DeploymentPrerequisites prerequisites) {
        StringBuilder context = new StringBuilder();
        context.append("=== DEPLOYMENT PREREQUISITES ===\n\n");
        context.append("CTO Approval: ").append(prerequisites.isCtoApproved()).append("\n");
        context.append("QA Test Pass: ").append(prerequisites.isQaTestPass()).append("\n");
        context.append("DevOps Risk Analysis: ").append(prerequisites.isDevOpsRiskAnalysis()).append("\n");
        context.append("Staging Validation Pass: ").append(prerequisites.isStagingValidationPass()).append("\n");
        context.append("Code Health Score: ").append(prerequisites.getCodeHealthScore()).append("\n");
        context.append("Minimum Health Threshold: ").append(prerequisites.getMinimumHealthThreshold()).append("\n");
        context.append("Rollback Configured: ").append(prerequisites.isRollbackConfigured()).append("\n");
        
        if (prerequisites.getCtoApprovalDetails() != null) {
            context.append("CTO Approval Details: ").append(prerequisites.getCtoApprovalDetails()).append("\n");
        }
        if (prerequisites.getQaTestResults() != null) {
            context.append("QA Test Results: ").append(prerequisites.getQaTestResults()).append("\n");
        }
        if (prerequisites.getDevOpsRiskAnalysisReport() != null) {
            context.append("DevOps Risk Analysis Report: ").append(prerequisites.getDevOpsRiskAnalysisReport()).append("\n");
        }
        if (prerequisites.getStagingValidationResults() != null) {
            context.append("Staging Validation Results: ").append(prerequisites.getStagingValidationResults()).append("\n");
        }
        if (prerequisites.getRollbackConfiguration() != null) {
            context.append("Rollback Configuration: ").append(prerequisites.getRollbackConfiguration()).append("\n");
        }
        
        return context.toString();
    }
    
    /**
     * Parses deployment validation result.
     */
    private DeploymentValidation parseDeploymentValidation(
            String validation, DeploymentPrerequisites prerequisites, String sessionId) {
        DeploymentValidation result = new DeploymentValidation();
        result.setSessionId(sessionId);
        result.setValidationReport(validation);
        
        // Check prerequisites
        boolean allPrerequisitesMet = 
            prerequisites.isCtoApproved() &&
            prerequisites.isQaTestPass() &&
            prerequisites.isDevOpsRiskAnalysis() &&
            prerequisites.isStagingValidationPass() &&
            prerequisites.getCodeHealthScore() >= prerequisites.getMinimumHealthThreshold() &&
            prerequisites.isRollbackConfigured();
        
        // Parse AI response
        String lowerValidation = validation.toLowerCase();
        boolean aiApproved = lowerValidation.contains("approved") || 
                            (lowerValidation.contains("pass") && !lowerValidation.contains("fail"));
        
        result.setApproved(allPrerequisitesMet && aiApproved);
        
        // Extract missing prerequisites
        java.util.List<String> missingPrerequisites = new java.util.ArrayList<>();
        if (!prerequisites.isCtoApproved()) {
            missingPrerequisites.add("CTO approval missing");
        }
        if (!prerequisites.isQaTestPass()) {
            missingPrerequisites.add("QA tests not passed");
        }
        if (!prerequisites.isDevOpsRiskAnalysis()) {
            missingPrerequisites.add("DevOps risk analysis missing");
        }
        if (!prerequisites.isStagingValidationPass()) {
            missingPrerequisites.add("Staging validation not passed");
        }
        if (prerequisites.getCodeHealthScore() < prerequisites.getMinimumHealthThreshold()) {
            missingPrerequisites.add(String.format(
                "Code health score %.1f below threshold %.1f",
                prerequisites.getCodeHealthScore(), prerequisites.getMinimumHealthThreshold()));
        }
        if (!prerequisites.isRollbackConfigured()) {
            missingPrerequisites.add("Rollback configuration missing");
        }
        
        result.setMissingPrerequisites(missingPrerequisites);
        
        return result;
    }
    
    /**
     * Parses deployment approval result.
     */
    private DeploymentApproval parseDeploymentApproval(String approval, String environment, String sessionId) {
        DeploymentApproval result = new DeploymentApproval();
        result.setEnvironment(environment);
        result.setSessionId(sessionId);
        result.setApprovalReport(approval);
        
        String lowerApproval = approval.toLowerCase();
        boolean approved = lowerApproval.contains("approved") || 
                          (lowerApproval.contains("approve") && !lowerApproval.contains("reject"));
        
        result.setApproved(approved);
        
        if (!approved) {
            // Extract rejection reasons
            java.util.List<String> reasons = new java.util.ArrayList<>();
            if (lowerApproval.contains("cto")) {
                reasons.add("CTO approval missing");
            }
            if (lowerApproval.contains("qa") || lowerApproval.contains("test")) {
                reasons.add("QA tests not passed");
            }
            if (lowerApproval.contains("risk")) {
                reasons.add("Risk analysis issues");
            }
            if (lowerApproval.contains("staging")) {
                reasons.add("Staging validation failed");
            }
            if (lowerApproval.contains("health") || lowerApproval.contains("code")) {
                reasons.add("Code health below threshold");
            }
            if (lowerApproval.contains("rollback")) {
                reasons.add("Rollback not configured");
            }
            result.setRejectionReasons(reasons);
        }
        
        return result;
    }
    
    /**
     * Parses unsafe change rejection result.
     */
    private UnsafeChangeRejection parseUnsafeChangeRejection(String rejection, String sessionId) {
        UnsafeChangeRejection result = new UnsafeChangeRejection();
        result.setSessionId(sessionId);
        result.setRejectionReport(rejection);
        
        String lowerRejection = rejection.toLowerCase();
        boolean rejected = lowerRejection.contains("reject") || 
                          lowerRejection.contains("unsafe") ||
                          lowerRejection.contains("dangerous");
        
        result.setRejected(rejected);
        
        // Extract safety concerns
        java.util.List<String> safetyConcerns = new java.util.ArrayList<>();
        if (lowerRejection.contains("security")) {
            safetyConcerns.add("Security risk");
        }
        if (lowerRejection.contains("data")) {
            safetyConcerns.add("Data integrity risk");
        }
        if (lowerRejection.contains("performance")) {
            safetyConcerns.add("Performance risk");
        }
        if (lowerRejection.contains("stability")) {
            safetyConcerns.add("Stability risk");
        }
        
        result.setSafetyConcerns(safetyConcerns);
        
        return result;
    }
    
    /**
     * Parses test coverage rejection result.
     */
    private TestCoverageRejection parseTestCoverageRejection(
            String rejection, double minimumThreshold, String sessionId) {
        TestCoverageRejection result = new TestCoverageRejection();
        result.setSessionId(sessionId);
        result.setMinimumThreshold(minimumThreshold);
        result.setRejectionReport(rejection);
        
        String lowerRejection = rejection.toLowerCase();
        boolean rejected = lowerRejection.contains("reject") || 
                          lowerRejection.contains("insufficient") ||
                          lowerRejection.contains("below threshold");
        
        result.setRejected(rejected);
        
        // Extract coverage issues
        java.util.List<String> coverageIssues = new java.util.ArrayList<>();
        if (lowerRejection.contains("unit")) {
            coverageIssues.add("Unit test coverage insufficient");
        }
        if (lowerRejection.contains("integration")) {
            coverageIssues.add("Integration test coverage insufficient");
        }
        if (lowerRejection.contains("e2e")) {
            coverageIssues.add("E2E test coverage insufficient");
        }
        
        result.setCoverageIssues(coverageIssues);
        
        return result;
    }
    
    /**
     * Parses supervisor review into structured decision.
     */
    private SupervisorDecision parseDecision(String review, String agentName, String sessionId) {
        SupervisorDecision decision = new SupervisorDecision();
        decision.setAgentName(agentName);
        decision.setSessionId(sessionId);
        decision.setReview(review);
        
        // Parse status from review
        String lowerReview = review.toLowerCase();
        if (lowerReview.contains("approved") || lowerReview.contains("approve")) {
            decision.setStatus(SupervisorDecision.DecisionStatus.APPROVED);
        } else if (lowerReview.contains("rejected") || lowerReview.contains("reject")) {
            decision.setStatus(SupervisorDecision.DecisionStatus.REJECTED);
        } else if (lowerReview.contains("modification") || lowerReview.contains("modify")) {
            decision.setStatus(SupervisorDecision.DecisionStatus.MODIFICATION_REQUIRED);
        } else {
            decision.setStatus(SupervisorDecision.DecisionStatus.UNDER_REVIEW);
        }
        
        return decision;
    }
    
    /**
     * Parses constraint enforcement result.
     */
    private ConstraintEnforcement parseConstraintEnforcement(String enforcement, String sessionId) {
        ConstraintEnforcement result = new ConstraintEnforcement();
        result.setSessionId(sessionId);
        result.setReport(enforcement);
        
        // Extract violations
        java.util.List<String> violations = new java.util.ArrayList<>();
        String[] lines = enforcement.split("\n");
        for (String line : lines) {
            if (line.toLowerCase().contains("violation") || line.toLowerCase().contains("violates")) {
                violations.add(line.trim());
            }
        }
        result.setViolations(violations);
        
        result.setCompliant(violations.isEmpty());
        
        return result;
    }
    
    /**
     * Parses safety assessment.
     */
    private SafetyAssessment parseSafetyAssessment(String assessment, String workflowName, String sessionId) {
        SafetyAssessment result = new SafetyAssessment();
        result.setWorkflowName(workflowName);
        result.setSessionId(sessionId);
        result.setAssessment(assessment);
        
        // Parse safety level
        String lowerAssessment = assessment.toLowerCase();
        if (lowerAssessment.contains("safe") && !lowerAssessment.contains("unsafe")) {
            result.setSafetyLevel(SafetyAssessment.SafetyLevel.SAFE);
        } else if (lowerAssessment.contains("unsafe") || lowerAssessment.contains("dangerous")) {
            result.setSafetyLevel(SafetyAssessment.SafetyLevel.UNSAFE);
        } else if (lowerAssessment.contains("risk") || lowerAssessment.contains("warning")) {
            result.setSafetyLevel(SafetyAssessment.SafetyLevel.RISKY);
        } else {
            result.setSafetyLevel(SafetyAssessment.SafetyLevel.UNKNOWN);
        }
        
        return result;
    }
    
    /**
     * Parses consistency check result.
     */
    private ConsistencyCheck parseConsistencyCheck(String report, String sessionId) {
        ConsistencyCheck result = new ConsistencyCheck();
        result.setSessionId(sessionId);
        result.setReport(report);
        
        // Extract inconsistencies
        java.util.List<String> inconsistencies = new java.util.ArrayList<>();
        String[] lines = report.split("\n");
        for (String line : lines) {
            if (line.toLowerCase().contains("inconsistent") || 
                line.toLowerCase().contains("conflict") ||
                line.toLowerCase().contains("contradict")) {
                inconsistencies.add(line.trim());
            }
        }
        result.setInconsistencies(inconsistencies);
        
        result.setConsistent(inconsistencies.isEmpty());
        
        return result;
    }
    
    /**
     * Parses override decision.
     */
    private OverrideDecision parseOverrideDecision(String override, String agentName, String sessionId) {
        OverrideDecision result = new OverrideDecision();
        result.setAgentName(agentName);
        result.setSessionId(sessionId);
        result.setOverrideReason(override);
        
        // Determine override status
        String lowerOverride = override.toLowerCase();
        if (lowerOverride.contains("blocked") || lowerOverride.contains("prevented")) {
            result.setStatus(OverrideDecision.OverrideStatus.BLOCKED);
        } else if (lowerOverride.contains("modified") || lowerOverride.contains("corrected")) {
            result.setStatus(OverrideDecision.OverrideStatus.MODIFIED);
        } else {
            result.setStatus(OverrideDecision.OverrideStatus.REVIEWED);
        }
        
        return result;
    }
    
    /**
     * LangChain4j AI Service interface for Supervisor.
     */
    interface SupervisorService {
        
        @SystemMessage("""
            You are the Supervisor Agent for AgroConnectWorld AI Company.
            
            Your role is the ultimate authority for safety, compliance, and consistency.
            
            ZERO-IMPACT ENFORCEMENT:
            - You MUST reject any proposal that suggests modifying existing code
            - You MUST reject any proposal that suggests changing production systems
            - You MUST reject any proposal that suggests database migrations
            - You MUST reject any proposal that suggests configuration changes
            - You MUST ensure all outputs are specification-only
            
            SAFETY RULES:
            - Reject proposals that could break existing functionality
            - Reject proposals that violate company constraints
            - Reject proposals that could cause data loss
            - Reject proposals that could compromise security
            - Reject proposals that could impact performance negatively
            
            CONSISTENCY CHECKS:
            - Ensure agent outputs are consistent with each other
            - Identify conflicts between different agent proposals
            - Ensure architectural decisions align with company standards
            - Verify that workflows follow established patterns
            
            OVERRIDE MECHANISMS:
            - You have the authority to override any agent decision
            - You can block unsafe workflows
            - You can require modifications before approval
            - You can reject proposals outright
            
            OUTPUT FORMAT:
            Your responses must be clear and structured:
            - Status: APPROVED, REJECTED, or MODIFICATION_REQUIRED
            - Reason: Clear explanation of your decision
            - Violations: List of any constraint violations
            - Recommendations: Suggestions for improvement if modification required
            
            Remember: Safety and compliance are non-negotiable.
            """)
        String reviewDecision(
            @UserMessage("Review this decision from {{agentName}}: {{decision}}. Context: {{context}}") 
            String agentName, String decision, String context, @MemoryId String sessionId);
        
        @SystemMessage("""
            Enforce company constraints on this proposal.
            Check for zero-impact violations, code modification attempts, and safety issues.
            """)
        String enforceConstraints(
            @UserMessage("Enforce constraints on: {{proposal}}") 
            String proposal, @MemoryId String sessionId);
        
        @SystemMessage("""
            Assess the safety of this workflow.
            Determine if it's SAFE, RISKY, or UNSAFE to execute.
            """)
        String assessWorkflowSafety(
            @UserMessage("Assess safety of workflow {{workflowName}}: {{workflowPlan}}") 
            String workflowName, String workflowPlan, @MemoryId String sessionId);
        
        @SystemMessage("""
            Check consistency across multiple agent outputs.
            Identify conflicts, contradictions, or inconsistencies.
            """)
        String checkConsistency(
            @UserMessage("Check consistency of these outputs: {{outputs}}") 
            java.util.Map<String, String> outputs, @MemoryId String sessionId);
        
        @SystemMessage("""
            Override an unsafe agent decision.
            Provide clear reason for override and alternative if applicable.
            """)
        String overrideDecision(
            @UserMessage("Override decision from {{agentName}}: {{originalDecision}}. Reason: {{reason}}") 
            String agentName, String originalDecision, String reason, @MemoryId String sessionId);
        
        @SystemMessage("""
            Validate deployment prerequisites.
            Deployment MUST be blocked if ANY of these are missing:
            - CTO approval
            - QA test pass
            - DevOps risk analysis
            - Staging validation pass
            - Code health score >= minimum threshold
            - Proper rollback configuration
            
            Be strict and thorough. Safety is paramount.
            """)
        String validateDeploymentPrerequisites(
            @UserMessage("Validate deployment prerequisites: {{prerequisites}}") 
            String prerequisites, @MemoryId String sessionId);
        
        @SystemMessage("""
            Approve or reject staging deployment.
            Staging deployments require:
            - CTO approval
            - QA test pass
            - Staging validation pass
            - Code health score >= threshold
            - Rollback configuration
            
            Output: APPROVED or REJECTED with clear reasons.
            """)
        String approveStagingDeployment(
            @UserMessage("Evaluate staging deployment: {{stagingContext}}") 
            String stagingContext, @MemoryId String sessionId);
        
        @SystemMessage("""
            Approve or reject production deployment.
            Production deployments require ALL prerequisites:
            - CTO approval
            - QA test pass
            - DevOps risk analysis
            - Staging validation pass
            - Code health score >= threshold
            - Proper rollback configuration
            
            Be extremely strict. Production deployments are critical.
            Output: APPROVED or REJECTED with detailed reasons.
            """)
        String approveProductionDeployment(
            @UserMessage("Evaluate production deployment: {{productionContext}}") 
            String productionContext, @MemoryId String sessionId);
        
        @SystemMessage("""
            Reject unsafe changes.
            Identify safety concerns including:
            - Security risks
            - Data integrity risks
            - Performance risks
            - Stability risks
            
            Output: REJECTED with safety concerns listed.
            """)
        String rejectUnsafeChanges(
            @UserMessage("Evaluate unsafe changes: {{changeDescription}}. Risk assessment: {{riskAssessment}}") 
            String changeDescription, String riskAssessment, @MemoryId String sessionId);
        
        @SystemMessage("""
            Reject deployment due to insufficient test coverage.
            Check test coverage against minimum threshold.
            Identify missing coverage areas:
            - Unit tests
            - Integration tests
            - E2E tests
            
            Output: REJECTED if coverage below threshold, with specific issues.
            """)
        String rejectInsufficientTestCoverage(
            @UserMessage("Evaluate test coverage: {{testCoverageReport}}. Minimum threshold: {{minimumThreshold}}") 
            String testCoverageReport, double minimumThreshold, @MemoryId String sessionId);
    }
    
    // Result classes
    
    public static class SupervisorDecision {
        private String agentName;
        private DecisionStatus status;
        private String review;
        private String sessionId;
        
        public enum DecisionStatus {
            APPROVED,
            REJECTED,
            MODIFICATION_REQUIRED,
            UNDER_REVIEW
        }
        
        // Getters and Setters
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
        public DecisionStatus getStatus() { return status; }
        public void setStatus(DecisionStatus status) { this.status = status; }
        public String getReview() { return review; }
        public void setReview(String review) { this.review = review; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
    
    public static class ConstraintEnforcement {
        private boolean compliant;
        private java.util.List<String> violations;
        private String report;
        private String sessionId;
        
        public ConstraintEnforcement() {
            this.violations = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isCompliant() { return compliant; }
        public void setCompliant(boolean compliant) { this.compliant = compliant; }
        public java.util.List<String> getViolations() { return violations; }
        public void setViolations(java.util.List<String> violations) { this.violations = violations; }
        public String getReport() { return report; }
        public void setReport(String report) { this.report = report; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
    
    public static class SafetyAssessment {
        private String workflowName;
        private SafetyLevel safetyLevel;
        private String assessment;
        private String sessionId;
        
        public enum SafetyLevel {
            SAFE,
            RISKY,
            UNSAFE,
            UNKNOWN
        }
        
        // Getters and Setters
        public String getWorkflowName() { return workflowName; }
        public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }
        public SafetyLevel getSafetyLevel() { return safetyLevel; }
        public void setSafetyLevel(SafetyLevel safetyLevel) { this.safetyLevel = safetyLevel; }
        public String getAssessment() { return assessment; }
        public void setAssessment(String assessment) { this.assessment = assessment; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
    
    public static class ConsistencyCheck {
        private boolean consistent;
        private java.util.List<String> inconsistencies;
        private String report;
        private String sessionId;
        
        public ConsistencyCheck() {
            this.inconsistencies = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isConsistent() { return consistent; }
        public void setConsistent(boolean consistent) { this.consistent = consistent; }
        public java.util.List<String> getInconsistencies() { return inconsistencies; }
        public void setInconsistencies(java.util.List<String> inconsistencies) { this.inconsistencies = inconsistencies; }
        public String getReport() { return report; }
        public void setReport(String report) { this.report = report; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
    
    public static class OverrideDecision {
        private String agentName;
        private OverrideStatus status;
        private String overrideReason;
        private String sessionId;
        
        public enum OverrideStatus {
            BLOCKED,
            MODIFIED,
            REVIEWED
        }
        
        // Getters and Setters
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
        public OverrideStatus getStatus() { return status; }
        public void setStatus(OverrideStatus status) { this.status = status; }
        public String getOverrideReason() { return overrideReason; }
        public void setOverrideReason(String overrideReason) { this.overrideReason = overrideReason; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
    
    /**
     * Deployment prerequisites data structure.
     */
    public static class DeploymentPrerequisites {
        private boolean ctoApproved;
        private boolean qaTestPass;
        private boolean devOpsRiskAnalysisDone;
        private boolean stagingValidationPass;
        private double codeHealthScore;
        private double minimumHealthThreshold;
        private boolean rollbackConfigured;
        private String ctoApprovalDetails;
        private String qaTestResults;
        private String devOpsRiskAnalysisReport;
        private String stagingValidationResults;
        private String rollbackConfiguration;
        
        // Getters and Setters
        public boolean isCtoApproved() { return ctoApproved; }
        public void setCtoApproved(boolean ctoApproved) { this.ctoApproved = ctoApproved; }
        public boolean isQaTestPass() { return qaTestPass; }
        public void setQaTestPass(boolean qaTestPass) { this.qaTestPass = qaTestPass; }
        public boolean isDevOpsRiskAnalysis() { return devOpsRiskAnalysisDone; }
        public void setDevOpsRiskAnalysis(boolean devOpsRiskAnalysisDone) { this.devOpsRiskAnalysisDone = devOpsRiskAnalysisDone; }
        public String getDevOpsRiskAnalysisReport() { return devOpsRiskAnalysisReport; }
        public void setDevOpsRiskAnalysisReport(String devOpsRiskAnalysisReport) { this.devOpsRiskAnalysisReport = devOpsRiskAnalysisReport; }
        public boolean isStagingValidationPass() { return stagingValidationPass; }
        public void setStagingValidationPass(boolean stagingValidationPass) { this.stagingValidationPass = stagingValidationPass; }
        public double getCodeHealthScore() { return codeHealthScore; }
        public void setCodeHealthScore(double codeHealthScore) { this.codeHealthScore = codeHealthScore; }
        public double getMinimumHealthThreshold() { return minimumHealthThreshold; }
        public void setMinimumHealthThreshold(double minimumHealthThreshold) { this.minimumHealthThreshold = minimumHealthThreshold; }
        public boolean isRollbackConfigured() { return rollbackConfigured; }
        public void setRollbackConfigured(boolean rollbackConfigured) { this.rollbackConfigured = rollbackConfigured; }
        public String getCtoApprovalDetails() { return ctoApprovalDetails; }
        public void setCtoApprovalDetails(String ctoApprovalDetails) { this.ctoApprovalDetails = ctoApprovalDetails; }
        public String getQaTestResults() { return qaTestResults; }
        public void setQaTestResults(String qaTestResults) { this.qaTestResults = qaTestResults; }
        public String getStagingValidationResults() { return stagingValidationResults; }
        public void setStagingValidationResults(String stagingValidationResults) { this.stagingValidationResults = stagingValidationResults; }
        public String getRollbackConfiguration() { return rollbackConfiguration; }
        public void setRollbackConfiguration(String rollbackConfiguration) { this.rollbackConfiguration = rollbackConfiguration; }
    }
    
    /**
     * Deployment validation result.
     */
    public static class DeploymentValidation {
        private boolean approved;
        private String validationReport;
        private java.util.List<String> missingPrerequisites;
        private String sessionId;
        
        public DeploymentValidation() {
            this.missingPrerequisites = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isApproved() { return approved; }
        public void setApproved(boolean approved) { this.approved = approved; }
        public String getValidationReport() { return validationReport; }
        public void setValidationReport(String validationReport) { this.validationReport = validationReport; }
        public java.util.List<String> getMissingPrerequisites() { return missingPrerequisites; }
        public void setMissingPrerequisites(java.util.List<String> missingPrerequisites) { this.missingPrerequisites = missingPrerequisites; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
    
    /**
     * Deployment approval result.
     */
    public static class DeploymentApproval {
        private boolean approved;
        private String environment; // staging or production
        private String approvalReport;
        private java.util.List<String> rejectionReasons;
        private String sessionId;
        
        public DeploymentApproval() {
            this.rejectionReasons = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isApproved() { return approved; }
        public void setApproved(boolean approved) { this.approved = approved; }
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        public String getApprovalReport() { return approvalReport; }
        public void setApprovalReport(String approvalReport) { this.approvalReport = approvalReport; }
        public java.util.List<String> getRejectionReasons() { return rejectionReasons; }
        public void setRejectionReasons(java.util.List<String> rejectionReasons) { this.rejectionReasons = rejectionReasons; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
    
    /**
     * Unsafe change rejection result.
     */
    public static class UnsafeChangeRejection {
        private boolean rejected;
        private String rejectionReport;
        private java.util.List<String> safetyConcerns;
        private String sessionId;
        
        public UnsafeChangeRejection() {
            this.safetyConcerns = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isRejected() { return rejected; }
        public void setRejected(boolean rejected) { this.rejected = rejected; }
        public String getRejectionReport() { return rejectionReport; }
        public void setRejectionReport(String rejectionReport) { this.rejectionReport = rejectionReport; }
        public java.util.List<String> getSafetyConcerns() { return safetyConcerns; }
        public void setSafetyConcerns(java.util.List<String> safetyConcerns) { this.safetyConcerns = safetyConcerns; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
    
    /**
     * Test coverage rejection result.
     */
    public static class TestCoverageRejection {
        private boolean rejected;
        private String rejectionReport;
        private double minimumThreshold;
        private java.util.List<String> coverageIssues;
        private String sessionId;
        
        public TestCoverageRejection() {
            this.coverageIssues = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isRejected() { return rejected; }
        public void setRejected(boolean rejected) { this.rejected = rejected; }
        public String getRejectionReport() { return rejectionReport; }
        public void setRejectionReport(String rejectionReport) { this.rejectionReport = rejectionReport; }
        public double getMinimumThreshold() { return minimumThreshold; }
        public void setMinimumThreshold(double minimumThreshold) { this.minimumThreshold = minimumThreshold; }
        public java.util.List<String> getCoverageIssues() { return coverageIssues; }
        public void setCoverageIssues(java.util.List<String> coverageIssues) { this.coverageIssues = coverageIssues; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
}

