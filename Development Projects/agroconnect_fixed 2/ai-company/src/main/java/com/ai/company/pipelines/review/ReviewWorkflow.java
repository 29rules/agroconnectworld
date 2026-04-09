package com.ai.company.pipelines.review;

import com.ai.company.agents.architect.ArchitectAgent;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.devops.DevOpsAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.qa.QAAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Review Workflow
 * 
 * Validates all specifications, ensures compliance with zero-impact mode,
 * and prepares artifacts for implementation.
 * 
 * Participating Agents:
 * - CTO Agent (primary, final approval)
 * - AI Architect Agent (architecture compliance)
 * - AI Engineer Agent (implementation review)
 * - DevOps Agent (infrastructure impact)
 * - QA Agent (test coverage)
 */
public class ReviewWorkflow {
    
    private static final Logger log = LoggerFactory.getLogger(ReviewWorkflow.class);
    
    private final CTOAgent cto;
    private final ArchitectAgent architect;
    private final EngineerAgent engineer;
    private final DevOpsAgent devops;
    private final QAAgent qa;
    
    public ReviewWorkflow(CTOAgent cto, ArchitectAgent architect, EngineerAgent engineer,
                         DevOpsAgent devops, QAAgent qa) {
        this.cto = cto;
        this.architect = architect;
        this.engineer = engineer;
        this.devops = devops;
        this.qa = qa;
    }
    
    /**
     * Execute review workflow
     * 
     * Input: Implementation specifications from Development workflow
     * Output: Review reports, approval decisions, final specifications
     */
    public ReviewResult execute(String implementationSpecs, String sessionId) {
        log.info("Starting Review Workflow for session: {}", sessionId);
        
        // Step 1: CTO performs initial compliance check
        log.info("Step 1: CTO performing initial compliance check");
        String complianceCheck = cto.assessRisk(implementationSpecs, sessionId);
        
        // Step 2: AI Architect reviews architecture alignment
        log.info("Step 2: AI Architect reviewing architecture alignment");
        String architectureReview = architect.designArchitecture(implementationSpecs, sessionId);
        
        // Step 3: AI Engineer reviews implementation approach
        log.info("Step 3: AI Engineer reviewing implementation approach");
        String implementationReview = engineer.createImplementationSpec(implementationSpecs, sessionId);
        
        // Step 4: DevOps reviews infrastructure impact
        log.info("Step 4: DevOps reviewing infrastructure impact");
        String infrastructureReview = devops.analyzeInfrastructure(sessionId);
        
        // Step 5: QA reviews test coverage
        log.info("Step 5: QA reviewing test coverage");
        String testCoverageReview = qa.createTestPlan(implementationSpecs, sessionId);
        
        // Step 6: CTO makes final approval decision
        log.info("Step 6: CTO making final approval decision");
        String finalApproval = cto.approveDecision(
            complianceCheck + "\n" + architectureReview + "\n" + implementationReview,
            sessionId
        );
        
        log.info("Review Workflow completed for session: {}", sessionId);
        
        return new ReviewResult(
            complianceCheck,
            architectureReview,
            implementationReview,
            infrastructureReview,
            testCoverageReview,
            finalApproval
        );
    }
    
    /**
     * Result of review workflow
     */
    public static class ReviewResult {
        private final String complianceCheck;
        private final String architectureReview;
        private final String implementationReview;
        private final String infrastructureReview;
        private final String testCoverageReview;
        private final String finalApproval;
        
        public ReviewResult(String complianceCheck, String architectureReview,
                           String implementationReview, String infrastructureReview,
                           String testCoverageReview, String finalApproval) {
            this.complianceCheck = complianceCheck;
            this.architectureReview = architectureReview;
            this.implementationReview = implementationReview;
            this.infrastructureReview = infrastructureReview;
            this.testCoverageReview = testCoverageReview;
            this.finalApproval = finalApproval;
        }
        
        // Getters
        public String getComplianceCheck() { return complianceCheck; }
        public String getArchitectureReview() { return architectureReview; }
        public String getImplementationReview() { return implementationReview; }
        public String getInfrastructureReview() { return infrastructureReview; }
        public String getTestCoverageReview() { return testCoverageReview; }
        public String getFinalApproval() { return finalApproval; }
    }
}



