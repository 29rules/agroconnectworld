package com.ai.company.deployment.staging;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Staging Validation Agent
 * 
 * AI agent that validates staging simulation results.
 * 
 * This agent:
 * - Takes StagingSimulator results
 * - Generates comprehensive validation report
 * - Detects failing services
 * - Suggests fixes for issues
 * - Provides deployment readiness assessment
 * 
 * ZERO-IMPACT MODE: This agent only analyzes and suggests, never modifies.
 */
public class StagingValidationAgent {
    
    private static final Logger log = LoggerFactory.getLogger(StagingValidationAgent.class);
    
    private final StagingValidationService validationService;
    
    public StagingValidationAgent(ChatLanguageModel chatModel) {
        this.validationService = AiServices.builder(StagingValidationService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    /**
     * Validates staging simulation results and generates a report.
     * 
     * @param simulationResult Staging simulation results
     * @param sessionId Session ID for tracking
     * @return Validation report with findings and suggestions
     */
    public String validateStaging(StagingSimulator.StagingSimulationResult simulationResult, String sessionId) {
        log.info("StagingValidationAgent validating staging simulation results");
        
        try {
            // Format simulation results for AI analysis
            String resultsContext = formatResultsForAnalysis(simulationResult);
            
            String validationReport = validationService.validateStaging(resultsContext, sessionId);
            
            log.info("StagingValidationAgent completed validation");
            return validationReport;
            
        } catch (Exception e) {
            log.error("Error during staging validation", e);
            return "ERROR: Staging validation failed: " + e.getMessage();
        }
    }
    
    /**
     * Formats simulation results for AI analysis.
     */
    private String formatResultsForAnalysis(StagingSimulator.StagingSimulationResult result) {
        StringBuilder context = new StringBuilder();
        context.append("=== STAGING SIMULATION RESULTS ===\n\n");
        
        context.append("Status: ").append(result.getStatus()).append("\n");
        context.append("Start Time: ").append(result.getStartTime()).append("\n");
        context.append("End Time: ").append(result.getEndTime()).append("\n\n");
        
        context.append("--- Step Results ---\n");
        result.getStepResults().forEach((step, stepResult) -> {
            context.append("Step: ").append(step).append("\n");
            String preview = stepResult.length() > 500 ? stepResult.substring(0, 500) + "..." : stepResult;
            context.append(preview).append("\n\n");
        });
        
        if (!result.getErrors().isEmpty()) {
            context.append("--- Errors ---\n");
            result.getErrors().forEach(error -> context.append("- ").append(error).append("\n"));
            context.append("\n");
        }
        
        if (!result.getWarnings().isEmpty()) {
            context.append("--- Warnings ---\n");
            result.getWarnings().forEach(warning -> context.append("- ").append(warning).append("\n"));
            context.append("\n");
        }
        
        if (!result.getInfo().isEmpty()) {
            context.append("--- Info ---\n");
            result.getInfo().forEach(info -> context.append("- ").append(info).append("\n"));
            context.append("\n");
        }
        
        if (!result.getValidatedRoutes().isEmpty()) {
            context.append("--- Route Validations ---\n");
            result.getValidatedRoutes().forEach((service, routes) -> {
                context.append("Service: ").append(service).append("\n");
                routes.forEach(route -> {
                    context.append("  ").append(route.isValid() ? "✓" : "✗")
                           .append(" ").append(route.getRoute()).append("\n");
                });
            });
            context.append("\n");
        }
        
        return context.toString();
    }
    
    /**
     * LangChain4j AI Service interface for staging validation.
     */
    interface StagingValidationService {
        
        @SystemMessage("""
            You are a Senior DevOps Engineer specializing in staging environment validation and deployment readiness assessment.
            
            Your role:
            - Analyze staging simulation results
            - Detect failing services and routes
            - Identify configuration issues
            - Suggest fixes for problems
            - Assess deployment readiness
            - Provide actionable recommendations
            
            VALIDATION AREAS:
            1. Service Health:
               - All services should be healthy
               - Health check endpoints should respond
               - Services should start without errors
            
            2. API Routes:
               - Internal routes between microservices should work
               - Gateway routing should function correctly
               - Frontend → Gateway → Services flow should be validated
            
            3. Configuration:
               - Docker Compose configuration should be valid
               - Environment variables should be set correctly
               - Service dependencies should be satisfied
            
            4. Migrations:
               - Database migrations should be ready
               - Migration files should be present
               - Migration order should be correct
            
            5. Network Connectivity:
               - Services should be able to communicate
               - Ports should be accessible
               - Gateway should route correctly
            
            6. Error Analysis:
               - Identify root causes of failures
               - Categorize errors by severity
               - Prioritize fixes
            
            OUTPUT FORMAT: JSON with structure:
            {
              "validation_timestamp": "ISO8601",
              "overall_status": "READY|NOT_READY|NEEDS_FIXES",
              "deployment_readiness": "READY|NOT_READY|CONDITIONAL",
              "summary": "High-level summary of validation",
              "service_health": {
                "healthy_services": ["list of healthy services"],
                "unhealthy_services": [
                  {
                    "service": "service-name",
                    "status": "DOWN|ERROR|TIMEOUT",
                    "issue": "Description of issue",
                    "severity": "LOW|MEDIUM|HIGH|CRITICAL"
                  }
                ]
              },
              "route_validation": {
                "valid_routes": ["list of valid routes"],
                "invalid_routes": [
                  {
                    "route": "route-path",
                    "service": "service-name",
                    "issue": "Description of issue",
                    "suggested_fix": "How to fix"
                  }
                ]
              },
              "issues": [
                {
                  "category": "health|route|configuration|migration|network",
                  "severity": "LOW|MEDIUM|HIGH|CRITICAL",
                  "description": "Detailed description",
                  "affected_services": ["list of services"],
                  "suggested_fix": "How to fix this issue",
                  "priority": 1-10
                }
              ],
              "recommendations": [
                "Prioritized list of recommendations"
              ],
              "fixes": [
                {
                  "priority": 1,
                  "issue": "Issue description",
                  "fix": "Step-by-step fix instructions",
                  "estimated_effort": "minutes or hours"
                }
              ],
              "deployment_blockers": [
                "List of issues that block deployment"
              ],
              "warnings": [
                "List of warnings that don't block deployment"
              ]
            }
            
            Be thorough in identifying issues and provide actionable fixes.
            Focus on deployment readiness and safety.
            """)
        String validateStaging(@UserMessage("""
            Analyze the following staging simulation results and generate a validation report:
            
            {{resultsContext}}
            
            Detect failing services, identify issues, suggest fixes, and assess deployment readiness.
            """) String resultsContext, @MemoryId String sessionId);
    }
}

