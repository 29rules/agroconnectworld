package com.ai.company.deployment;

import com.ai.company.tools.code.CodeReaderTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deployment Plan Agent
 * 
 * Generates comprehensive deployment plans for AgroConnectWorld services.
 * 
 * This agent:
 * - Analyzes codebase and infrastructure
 * - Creates step-by-step deployment plans
 * - Identifies risks and dependencies
 * - Suggests rollback strategies
 * - Outputs structured JSON plans
 * 
 * ZERO-IMPACT MODE: Only generates plans, never executes deployments.
 */
public class DeploymentPlanAgent {
    
    private static final Logger log = LoggerFactory.getLogger(DeploymentPlanAgent.class);
    
    private final DeploymentPlanService planService;
    private final CodeReaderTool codeReader;
    
    public DeploymentPlanAgent(ChatLanguageModel chatModel) {
        this.codeReader = new CodeReaderTool();
        this.planService = AiServices.builder(DeploymentPlanService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    /**
     * Generates a deployment plan for specified services.
     * 
     * @param services Comma-separated list of services to deploy (e.g., "auth-service,product-service")
     * @param environment Deployment environment ("staging" or "production")
     * @param sessionId Session ID for tracking
     * @return Deployment plan in JSON format
     */
    public String generateDeploymentPlan(String services, String environment, String sessionId) {
        log.info("Generating deployment plan for services: {} to environment: {}", services, environment);
        
        try {
            // Gather system context
            String systemContext = gatherSystemContext();
            
            String plan = planService.generatePlan(services, environment, systemContext, sessionId);
            
            log.info("Deployment plan generated successfully");
            return plan;
            
        } catch (Exception e) {
            log.error("Error generating deployment plan", e);
            return "ERROR: Deployment plan generation failed: " + e.getMessage();
        }
    }
    
    /**
     * Gathers system context for planning.
     */
    private String gatherSystemContext() {
        StringBuilder context = new StringBuilder();
        context.append("=== SYSTEM CONTEXT FOR DEPLOYMENT PLANNING ===\n\n");
        
        // Read docker-compose.yml
        try {
            String composeContent = codeReader.readFile("ops/docker-compose.yml");
            if (!composeContent.startsWith("ERROR")) {
                context.append("--- Docker Compose Configuration ---\n");
                String preview = composeContent.length() > 2000 ? composeContent.substring(0, 2000) + "..." : composeContent;
                context.append(preview).append("\n\n");
            }
        } catch (Exception e) {
            log.warn("Could not read docker-compose.yml", e);
        }
        
        // Read service configurations
        String[] serviceConfigs = {
            "backend/gateway/pom.xml",
            "backend/auth-service/pom.xml",
            "backend/product-service/pom.xml"
        };
        
        context.append("--- Service Configurations ---\n");
        for (String config : serviceConfigs) {
            try {
                String content = codeReader.readFile(config);
                if (!content.startsWith("ERROR")) {
                    String serviceName = config.split("/")[1];
                    context.append("Service: ").append(serviceName).append("\n");
                    String preview = content.length() > 500 ? content.substring(0, 500) + "..." : content;
                    context.append(preview).append("\n\n");
                }
            } catch (Exception e) {
                log.warn("Could not read config: {}", config, e);
            }
        }
        
        return context.toString();
    }
    
    /**
     * LangChain4j AI Service interface for deployment planning.
     */
    interface DeploymentPlanService {
        
        @SystemMessage("""
            You are a Senior DevOps Engineer specializing in deployment planning and risk assessment.
            
            Your role:
            - Generate comprehensive deployment plans for microservices
            - Identify risks and dependencies
            - Create step-by-step deployment procedures
            - Suggest rollback strategies
            - Assess deployment readiness
            
            DEPLOYMENT STAGES:
            1. Code Validation
               - Check code quality
               - Validate configuration
               - Review dependencies
            
            2. Build
               - Build Docker images
               - Tag images appropriately
               - Validate build artifacts
            
            3. Test
               - Run backend tests
               - Run frontend tests
               - Integration tests (if applicable)
            
            4. Package
               - Package artifacts
               - Create deployment bundles
               - Generate checksums
            
            5. Deploy
               - Upload to staging/production
               - Deploy containers
               - Configure environment
            
            6. Validate
               - Health checks
               - Smoke tests
               - Monitor logs
            
            RISK LEVELS:
            - LOW: Safe deployment, minimal impact
            - MEDIUM: Some risk, requires monitoring
            - HIGH: Significant risk, requires careful planning
            - CRITICAL: High risk, requires rollback plan
            
            OUTPUT FORMAT: JSON with structure:
            {
              "plan_id": "unique-plan-id",
              "generated_at": "ISO8601 timestamp",
              "services": ["list of services"],
              "environment": "staging|production",
              "overall_risk": "LOW|MEDIUM|HIGH|CRITICAL",
              "stages": [
                {
                  "stage": "code_validation|build|test|package|deploy|validate",
                  "order": 1,
                  "tasks": [
                    {
                      "task_id": "unique-task-id",
                      "description": "Task description",
                      "tool": "tool-name",
                      "command": "command or operation",
                      "expected_duration": "minutes",
                      "risk_level": "LOW|MEDIUM|HIGH|CRITICAL",
                      "dependencies": ["list of task_ids"],
                      "rollback_steps": ["steps to rollback if fails"]
                    }
                  ],
                  "estimated_duration": "minutes",
                  "risk_level": "LOW|MEDIUM|HIGH|CRITICAL"
                }
              ],
              "dependencies": {
                "service_dependencies": ["list of service dependencies"],
                "infrastructure_dependencies": ["list of infrastructure needs"]
              },
              "rollback_plan": {
                "triggers": ["conditions that trigger rollback"],
                "steps": ["rollback steps"],
                "estimated_rollback_time": "minutes"
              },
              "pre_deployment_checks": [
                "List of checks to perform before deployment"
              ],
              "post_deployment_checks": [
                "List of checks to perform after deployment"
              ],
              "monitoring": {
                "metrics_to_watch": ["list of metrics"],
                "alert_conditions": ["conditions that trigger alerts"]
              }
            }
            
            Be thorough and consider all risks. Prioritize safety and reliability.
            """)
        String generatePlan(@UserMessage("""
            Generate a deployment plan for:
            - Services: {{services}}
            - Environment: {{environment}}
            
            System Context:
            {{systemContext}}
            
            Create a comprehensive deployment plan with all stages, tasks, risks, and rollback strategies.
            """) String services, String environment, String systemContext, @MemoryId String sessionId);
    }
}



