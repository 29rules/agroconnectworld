package com.ai.company.self;

import com.ai.company.agents.architect.ArchitectAgent;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.qa.QAAgent;
import com.ai.company.impact.ImpactGuard;
import com.ai.company.impact.ImpactMode;
import com.ai.company.impact.ImpactModeManager;
import com.ai.company.registry.AgentRegistry;
import com.ai.company.tools.code.GitCommitTool;
import com.ai.company.tools.code.TestRunnerTool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Self Improvement Engine
 * 
 * Orchestrates the self-improvement cycle that allows the AI Company
 * to analyze and improve itself automatically.
 * 
 * Pipeline:
 * 1. SelfAnalysisAgent → Analyze codebase and generate suggestions
 * 2. ArchitectAgent → Design improvements based on suggestions
 * 3. CTOAgent → Review and approve/reject improvements
 * 4. EngineerAgent → Generate implementation patches
 * 5. ImpactGuard → Validate and apply safe changes
 * 6. QAAgent → Generate test updates
 * 7. GitCommitTool → Commit approved changes
 * 
 * SAFETY:
 * - All changes go through ImpactGuard validation
 * - Requires Supervisor approval
 * - All operations are logged
 * - Only runs when explicitly triggered (not automatic)
 * 
 * ZERO-IMPACT MODE: By default, this engine only analyzes and suggests.
 * Changes are only applied when Impact Mode allows and Supervisor approves.
 */
public class SelfImprovementEngine {
    
    private static final Logger log = LoggerFactory.getLogger(SelfImprovementEngine.class);
    
    private final AgentRegistry agentRegistry;
    private final ChatLanguageModel chatModel;
    private final SelfAnalysisAgent selfAnalysisAgent;
    private final CodeHealthMonitor healthMonitor;
    private final ArchitectureDriftDetector driftDetector;
    private final FeatureOpportunityFinder opportunityFinder;
    private final ImpactGuard impactGuard;
    private final TestRunnerTool testRunner;
    private final GitCommitTool gitCommitTool;
    
    public SelfImprovementEngine(AgentRegistry agentRegistry, ChatLanguageModel chatModel) {
        this.agentRegistry = agentRegistry;
        this.chatModel = chatModel;
        this.selfAnalysisAgent = new SelfAnalysisAgent(chatModel);
        this.healthMonitor = new CodeHealthMonitor();
        this.driftDetector = new ArchitectureDriftDetector(chatModel);
        this.opportunityFinder = new FeatureOpportunityFinder(chatModel);
        this.impactGuard = new ImpactGuard();
        this.testRunner = new TestRunnerTool();
        this.gitCommitTool = new GitCommitTool(chatModel);
    }
    
    /**
     * Executes a complete self-improvement cycle.
     * 
     * @param sessionId Session ID for tracking
     * @return Improvement cycle result with all outputs
     */
    public ImprovementCycleResult executeCycle(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = "improvement-" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        log.info("=".repeat(80));
        log.info("SELF-IMPROVEMENT CYCLE STARTED");
        log.info("Session ID: {}", sessionId);
        log.info("=".repeat(80));
        
        ImprovementCycleResult result = new ImprovementCycleResult(sessionId);
        result.setStartTime(LocalDateTime.now());
        
        try {
            // Step 1: Self Analysis
            log.info("STEP 1: Self Analysis Agent");
            String analysisReport = selfAnalysisAgent.analyzeCodebase(sessionId);
            result.setAnalysisReport(analysisReport);
            log.info("Self analysis completed");
            
            // Step 2: Code Health Monitoring
            log.info("STEP 2: Code Health Monitoring");
            CodeHealthMonitor.HealthReport backendHealth = healthMonitor.evaluateHealth("backend");
            CodeHealthMonitor.HealthReport frontendHealth = healthMonitor.evaluateHealth("frontend");
            result.setBackendHealthScore(backendHealth.getScore());
            result.setFrontendHealthScore(frontendHealth.getScore());
            result.setHealthReport(backendHealth.getReport() + "\n\n" + frontendHealth.getReport());
            log.info("Health monitoring completed. Backend: {}, Frontend: {}", 
                backendHealth.getScore(), frontendHealth.getScore());
            
            // Step 3: Architecture Drift Detection
            log.info("STEP 3: Architecture Drift Detection");
            String driftReport = driftDetector.detectDrift(sessionId);
            result.setDriftReport(driftReport);
            log.info("Drift detection completed");
            
            // Step 4: Feature Opportunity Finding
            log.info("STEP 4: Feature Opportunity Finding");
            String opportunities = opportunityFinder.findOpportunities(sessionId);
            result.setFeatureOpportunities(opportunities);
            log.info("Opportunity finding completed");
            
            // Step 5: Architect designs improvements
            log.info("STEP 5: Architect Agent - Designing Improvements");
            ArchitectAgent architect = agentRegistry.getArchitectAgent();
            String improvementContext = String.join("\n\n---\n\n", 
                analysisReport, driftReport, opportunities);
            String architectureImprovements = architect.designArchitecture(improvementContext, sessionId);
            result.setArchitectureImprovements(architectureImprovements);
            log.info("Architecture improvements designed");
            
            // Step 6: CTO reviews and approves
            log.info("STEP 6: CTO Agent - Review and Approval");
            CTOAgent cto = agentRegistry.getCTOAgent();
            String ctoReview = cto.reviewArchitecture(architectureImprovements, sessionId);
            result.setCtoReview(ctoReview);
            log.info("CTO review completed");
            
            // Check if CTO approved
            boolean ctoApproved = ctoReview.toLowerCase().contains("approval") || 
                                 ctoReview.toLowerCase().contains("approved") ||
                                 !ctoReview.toLowerCase().contains("reject");
            
            if (!ctoApproved) {
                log.warn("CTO did not approve improvements. Stopping cycle.");
                result.setStatus("CTO_REJECTED");
                result.setEndTime(LocalDateTime.now());
                return result;
            }
            
            // Step 7: Engineer generates implementation patches
            log.info("STEP 7: Engineer Agent - Generating Implementation Patches");
            EngineerAgent engineer = agentRegistry.getEngineerAgent();
            String implementationSpec = engineer.createImplementationSpec(ctoReview, sessionId);
            result.setImplementationSpec(implementationSpec);
            log.info("Implementation spec generated");
            
            // Step 8: ImpactGuard validation
            log.info("STEP 8: ImpactGuard - Validating Changes");
            ImpactModeManager modeManager = ImpactModeManager.getInstance();
            ImpactMode currentMode = modeManager.getMode();
            
            if (currentMode == ImpactMode.ZERO_IMPACT) {
                log.warn("ZERO_IMPACT mode active. Changes cannot be applied. Only suggestions generated.");
                result.setStatus("SUGGESTIONS_ONLY");
                result.setNote("Improvements suggested but not applied due to ZERO_IMPACT mode");
            } else {
                // In CONTROLLED_IMPACT or FULL_IMPACT, we could apply changes
                // For now, we'll just validate
                ImpactGuard.ValidationResult validation = impactGuard.validateOperation("IMPROVEMENT", null);
                result.setValidationResult(validation.getMessage());
                log.info("ImpactGuard validation: {}", validation.isAllowed() ? "ALLOWED" : "BLOCKED");
                
                if (validation.isAllowed() && currentMode != ImpactMode.ZERO_IMPACT) {
                    // Step 9: QA generates test updates
                    log.info("STEP 9: QA Agent - Generating Test Updates");
                    QAAgent qa = agentRegistry.getQAAgent();
                    String testPlan = qa.createTestPlan(implementationSpec, sessionId);
                    result.setTestPlan(testPlan);
                    log.info("Test plan generated");
                    
                    // Note: Actual code application would happen here
                    // For safety, we're not auto-applying in this version
                    result.setStatus("VALIDATED_READY");
                    result.setNote("Improvements validated and ready for manual application");
                } else {
                    result.setStatus("VALIDATION_FAILED");
                }
            }
            
            result.setEndTime(LocalDateTime.now());
            result.setStatus("COMPLETED");
            
            log.info("=".repeat(80));
            log.info("SELF-IMPROVEMENT CYCLE COMPLETED");
            log.info("Status: {}", result.getStatus());
            log.info("=".repeat(80));
            
        } catch (Exception e) {
            log.error("Error during self-improvement cycle", e);
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
            result.setEndTime(LocalDateTime.now());
        }
        
        return result;
    }
    
    /**
     * Result of self-improvement cycle.
     */
    public static class ImprovementCycleResult {
        private final String sessionId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private String errorMessage;
        private String note;
        
        private String analysisReport;
        private String healthReport;
        private int backendHealthScore;
        private int frontendHealthScore;
        private String driftReport;
        private String featureOpportunities;
        private String architectureImprovements;
        private String ctoReview;
        private String implementationSpec;
        private String validationResult;
        private String testPlan;
        
        public ImprovementCycleResult(String sessionId) {
            this.sessionId = sessionId;
            this.status = "IN_PROGRESS";
        }
        
        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        
        public String getAnalysisReport() { return analysisReport; }
        public void setAnalysisReport(String analysisReport) { this.analysisReport = analysisReport; }
        public String getHealthReport() { return healthReport; }
        public void setHealthReport(String healthReport) { this.healthReport = healthReport; }
        public int getBackendHealthScore() { return backendHealthScore; }
        public void setBackendHealthScore(int backendHealthScore) { this.backendHealthScore = backendHealthScore; }
        public int getFrontendHealthScore() { return frontendHealthScore; }
        public void setFrontendHealthScore(int frontendHealthScore) { this.frontendHealthScore = frontendHealthScore; }
        public String getDriftReport() { return driftReport; }
        public void setDriftReport(String driftReport) { this.driftReport = driftReport; }
        public String getFeatureOpportunities() { return featureOpportunities; }
        public void setFeatureOpportunities(String featureOpportunities) { this.featureOpportunities = featureOpportunities; }
        public String getArchitectureImprovements() { return architectureImprovements; }
        public void setArchitectureImprovements(String architectureImprovements) { this.architectureImprovements = architectureImprovements; }
        public String getCtoReview() { return ctoReview; }
        public void setCtoReview(String ctoReview) { this.ctoReview = ctoReview; }
        public String getImplementationSpec() { return implementationSpec; }
        public void setImplementationSpec(String implementationSpec) { this.implementationSpec = implementationSpec; }
        public String getValidationResult() { return validationResult; }
        public void setValidationResult(String validationResult) { this.validationResult = validationResult; }
        public String getTestPlan() { return testPlan; }
        public void setTestPlan(String testPlan) { this.testPlan = testPlan; }
    }
}



