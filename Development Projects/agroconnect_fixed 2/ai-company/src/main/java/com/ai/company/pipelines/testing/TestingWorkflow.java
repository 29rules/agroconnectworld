package com.ai.company.pipelines.testing;

import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.fullstack.FullStackAgent;
import com.ai.company.agents.productmanager.ProductManagerAgent;
import com.ai.company.agents.qa.QAAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing Workflow
 * 
 * Creates comprehensive test plans, Postman collections, and E2E testing flows
 * to ensure quality standards.
 * 
 * Participating Agents:
 * - QA Agent (primary)
 * - Product Manager Agent (acceptance criteria validation)
 * - AI Engineer Agent (backend test requirements)
 * - Full-Stack Developer Agent (frontend test specs)
 * - CTO Agent (test strategy approval)
 */
public class TestingWorkflow {
    
    private static final Logger log = LoggerFactory.getLogger(TestingWorkflow.class);
    
    private final QAAgent qa;
    private final ProductManagerAgent productManager;
    private final EngineerAgent engineer;
    private final FullStackAgent fullstack;
    private final CTOAgent cto;
    
    public TestingWorkflow(QAAgent qa, ProductManagerAgent productManager,
                           EngineerAgent engineer, FullStackAgent fullstack, CTOAgent cto) {
        this.qa = qa;
        this.productManager = productManager;
        this.engineer = engineer;
        this.fullstack = fullstack;
        this.cto = cto;
    }
    
    /**
     * Execute testing workflow
     * 
     * Input: Approved implementation specifications
     * Output: Test plans, Postman collections, E2E specs
     */
    public TestingResult execute(String approvedSpecs, String userStories, String sessionId) {
        log.info("Starting Testing Workflow for session: {}", sessionId);
        
        // Step 1: QA creates test plans
        log.info("Step 1: Creating test plans");
        String testPlans = qa.createTestPlan(approvedSpecs, sessionId);
        
        // Step 2: QA designs Postman collections
        log.info("Step 2: Designing Postman collections");
        String postmanCollections = qa.designPostmanCollection(approvedSpecs, sessionId);
        
        // Step 3: QA specifies E2E flows
        log.info("Step 3: Specifying E2E test flows");
        String e2eFlows = qa.specifyE2EFlow(userStories, sessionId);
        
        // Step 4: Product Manager validates acceptance criteria coverage
        log.info("Step 4: Validating acceptance criteria coverage");
        String acceptanceValidation = productManager.defineAcceptanceCriteria(userStories, sessionId);
        
        // Step 5: AI Engineer reviews backend test requirements
        log.info("Step 5: Reviewing backend test requirements");
        String backendTestReview = engineer.createImplementationSpec(testPlans, sessionId);
        
        // Step 6: Full-Stack Developer reviews frontend test specs
        log.info("Step 6: Reviewing frontend test specifications");
        String frontendTestReview = fullstack.suggestComponent(testPlans, sessionId);
        
        // Step 7: CTO approves test strategy
        log.info("Step 7: CTO approving test strategy");
        String ctoApproval = cto.approveDecision(testPlans, sessionId);
        
        log.info("Testing Workflow completed for session: {}", sessionId);
        
        return new TestingResult(
            testPlans,
            postmanCollections,
            e2eFlows,
            acceptanceValidation,
            backendTestReview,
            frontendTestReview,
            ctoApproval
        );
    }
    
    /**
     * Result of testing workflow
     */
    public static class TestingResult {
        private final String testPlans;
        private final String postmanCollections;
        private final String e2eFlows;
        private final String acceptanceValidation;
        private final String backendTestReview;
        private final String frontendTestReview;
        private final String ctoApproval;
        
        public TestingResult(String testPlans, String postmanCollections, String e2eFlows,
                            String acceptanceValidation, String backendTestReview,
                            String frontendTestReview, String ctoApproval) {
            this.testPlans = testPlans;
            this.postmanCollections = postmanCollections;
            this.e2eFlows = e2eFlows;
            this.acceptanceValidation = acceptanceValidation;
            this.backendTestReview = backendTestReview;
            this.frontendTestReview = frontendTestReview;
            this.ctoApproval = ctoApproval;
        }
        
        // Getters
        public String getTestPlans() { return testPlans; }
        public String getPostmanCollections() { return postmanCollections; }
        public String getE2eFlows() { return e2eFlows; }
        public String getAcceptanceValidation() { return acceptanceValidation; }
        public String getBackendTestReview() { return backendTestReview; }
        public String getFrontendTestReview() { return frontendTestReview; }
        public String getCtoApproval() { return ctoApproval; }
    }
}



