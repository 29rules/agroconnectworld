package com.ai.company.orchestrator;

import com.ai.company.agents.architect.ArchitectAgent;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.fullstack.FullStackAgent;
import com.ai.company.agents.productmanager.ProductManagerAgent;
import com.ai.company.agents.qa.QAAgent;
import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.registry.AgentRegistry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Multi-Agent Orchestrator
 * 
 * Coordinates multiple AI agents in a sequential pipeline to execute
 * complex feature development workflows.
 * 
 * Pipeline Flow:
 * 1. Product Manager Agent - Creates product specifications and epics
 * 2. Architect Agent - Designs system architecture
 * 3. CTO Agent - Reviews and approves architecture
 * 4. Engineer Agent - Creates implementation specifications
 * 5. Full-Stack Agent - Designs frontend components
 * 6. QA Agent - Creates test plans
 * 7. Supervisor Agent - Final validation and approval
 * 
 * ZERO-IMPACT MODE: All agents operate in read-only mode.
 * No real code modifications are made to existing systems.
 */
public class MultiAgentOrchestrator {
    
    private static final Logger log = LoggerFactory.getLogger(MultiAgentOrchestrator.class);
    
    private final AgentRegistry agentRegistry;
    private final ChatLanguageModel chatModel;
    
    public MultiAgentOrchestrator(AgentRegistry agentRegistry, ChatLanguageModel chatModel) {
        this.agentRegistry = agentRegistry;
        this.chatModel = chatModel;
    }
    
    /**
     * Execute the full multi-agent pipeline for a feature request.
     * 
     * @param featureRequest The feature request description
     * @param sessionId Optional session ID (generated if not provided)
     * @return OrchestrationResult containing all agent outputs
     */
    public OrchestrationResult executePipeline(String featureRequest, String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = "orchestration-" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        log.info("Starting Multi-Agent Pipeline for feature: {}", featureRequest);
        log.info("Session ID: {}", sessionId);
        
        OrchestrationResult result = new OrchestrationResult(sessionId, featureRequest);
        
        try {
            // Step 1: Product Manager Agent
            log.info("=".repeat(80));
            log.info("STEP 1: Product Manager Agent");
            log.info("=".repeat(80));
            ProductManagerAgent productManager = agentRegistry.getProductManagerAgent();
            String productSpec = productManager.createEpic(featureRequest, sessionId);
            result.setProductManagerOutput(productSpec);
            log.info("Product Manager completed");
            
            // Step 2: Architect Agent
            log.info("=".repeat(80));
            log.info("STEP 2: Architect Agent");
            log.info("=".repeat(80));
            ArchitectAgent architect = agentRegistry.getArchitectAgent();
            String architectureDesign = architect.designArchitecture(productSpec, sessionId);
            result.setArchitectOutput(architectureDesign);
            log.info("Architect completed");
            
            // Step 3: CTO Agent (Review and Approval)
            log.info("=".repeat(80));
            log.info("STEP 3: CTO Agent - Review and Approval");
            log.info("=".repeat(80));
            CTOAgent cto = agentRegistry.getCTOAgent();
            String ctoReview = cto.reviewArchitecture(architectureDesign, sessionId);
            result.setCtoOutput(ctoReview);
            log.info("CTO Review completed");
            
            // Step 4: Engineer Agent
            log.info("=".repeat(80));
            log.info("STEP 4: Engineer Agent");
            log.info("=".repeat(80));
            EngineerAgent engineer = agentRegistry.getEngineerAgent();
            String implementationSpec = engineer.createImplementationSpec(ctoReview + "\n" + architectureDesign, sessionId);
            result.setEngineerOutput(implementationSpec);
            log.info("Engineer completed");
            
            // Step 5: Full-Stack Agent
            log.info("=".repeat(80));
            log.info("STEP 5: Full-Stack Agent");
            log.info("=".repeat(80));
            FullStackAgent fullstack = agentRegistry.getFullStackAgent();
            String frontendSpec = fullstack.suggestComponent(implementationSpec, sessionId);
            result.setFullStackOutput(frontendSpec);
            log.info("Full-Stack Developer completed");
            
            // Step 6: QA Agent
            log.info("=".repeat(80));
            log.info("STEP 6: QA Agent");
            log.info("=".repeat(80));
            QAAgent qa = agentRegistry.getQAAgent();
            String testPlan = qa.createTestPlan(implementationSpec + "\n" + frontendSpec, sessionId);
            result.setQaOutput(testPlan);
            log.info("QA completed");
            
            // Step 7: Supervisor Agent (Final Validation)
            log.info("=".repeat(80));
            log.info("STEP 7: Supervisor Agent - Final Validation");
            log.info("=".repeat(80));
            SupervisorAgent supervisor = new SupervisorAgent(chatModel);
            String allOutputs = String.join("\n\n---\n\n", 
                productSpec, architectureDesign, ctoReview, 
                implementationSpec, frontendSpec, testPlan);
            SupervisorAgent.ConstraintEnforcement enforcement = supervisor.enforceConstraints(allOutputs, sessionId);
            result.setSupervisorOutput(enforcement.getReport());
            log.info("Supervisor validation completed");
            
            result.setStatus("COMPLETED");
            log.info("=".repeat(80));
            log.info("Multi-Agent Pipeline COMPLETED successfully");
            log.info("=".repeat(80));
            
        } catch (Exception e) {
            log.error("Error during pipeline execution", e);
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Result of the orchestration pipeline.
     */
    public static class OrchestrationResult {
        private final String sessionId;
        private final String featureRequest;
        private String status;
        private String errorMessage;
        
        private String productManagerOutput;
        private String architectOutput;
        private String ctoOutput;
        private String engineerOutput;
        private String fullStackOutput;
        private String qaOutput;
        private String supervisorOutput;
        
        public OrchestrationResult(String sessionId, String featureRequest) {
            this.sessionId = sessionId;
            this.featureRequest = featureRequest;
            this.status = "IN_PROGRESS";
        }
        
        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public String getFeatureRequest() { return featureRequest; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public String getProductManagerOutput() { return productManagerOutput; }
        public void setProductManagerOutput(String productManagerOutput) { this.productManagerOutput = productManagerOutput; }
        
        public String getArchitectOutput() { return architectOutput; }
        public void setArchitectOutput(String architectOutput) { this.architectOutput = architectOutput; }
        
        public String getCtoOutput() { return ctoOutput; }
        public void setCtoOutput(String ctoOutput) { this.ctoOutput = ctoOutput; }
        
        public String getEngineerOutput() { return engineerOutput; }
        public void setEngineerOutput(String engineerOutput) { this.engineerOutput = engineerOutput; }
        
        public String getFullStackOutput() { return fullStackOutput; }
        public void setFullStackOutput(String fullStackOutput) { this.fullStackOutput = fullStackOutput; }
        
        public String getQaOutput() { return qaOutput; }
        public void setQaOutput(String qaOutput) { this.qaOutput = qaOutput; }
        
        public String getSupervisorOutput() { return supervisorOutput; }
        public void setSupervisorOutput(String supervisorOutput) { this.supervisorOutput = supervisorOutput; }
    }
}



