package com.ai.company.pipelines.development;

import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.fullstack.FullStackAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Development Workflow
 * 
 * Converts architecture designs into detailed implementation specifications,
 * pseudo-code, and code structure outlines.
 * 
 * Participating Agents:
 * - AI Engineer Agent (backend specs)
 * - Full-Stack Developer Agent (frontend specs)
 * - CTO Agent (review)
 */
public class DevelopmentWorkflow {
    
    private static final Logger log = LoggerFactory.getLogger(DevelopmentWorkflow.class);
    
    private final EngineerAgent engineer;
    private final FullStackAgent fullstack;
    private final CTOAgent cto;
    
    public DevelopmentWorkflow(EngineerAgent engineer, FullStackAgent fullstack, CTOAgent cto) {
        this.engineer = engineer;
        this.fullstack = fullstack;
        this.cto = cto;
    }
    
    /**
     * Execute development workflow
     * 
     * Input: Approved architecture from Architecture workflow
     * Output: Implementation specifications, pseudo-code, code structure
     */
    public DevelopmentResult execute(String approvedArchitecture, String sessionId) {
        log.info("Starting Development Workflow for session: {}", sessionId);
        
        // Step 1: AI Engineer creates backend implementation specs
        log.info("Step 1: Creating backend implementation specifications");
        String backendSpecs = engineer.createImplementationSpec(approvedArchitecture, sessionId);
        
        // Step 2: Full-Stack Developer creates frontend specs
        log.info("Step 2: Creating frontend implementation specifications");
        String frontendSpecs = fullstack.suggestComponent(approvedArchitecture, sessionId);
        
        // Step 3: Generate pseudo-code for complex logic
        log.info("Step 3: Generating pseudo-code");
        String pseudoCode = engineer.generatePseudoCode(backendSpecs, sessionId);
        
        // Step 4: Create integration specs
        log.info("Step 4: Creating integration specifications");
        String integrationSpecs = fullstack.createIntegrationSpec(frontendSpecs, backendSpecs, sessionId);
        
        // Step 5: CTO reviews implementation approach
        log.info("Step 5: CTO reviewing implementation approach");
        String ctoReview = cto.reviewArchitecture(backendSpecs + "\n" + frontendSpecs, sessionId);
        
        log.info("Development Workflow completed for session: {}", sessionId);
        
        return new DevelopmentResult(
            backendSpecs,
            frontendSpecs,
            pseudoCode,
            integrationSpecs,
            ctoReview
        );
    }
    
    /**
     * Result of development workflow
     */
    public static class DevelopmentResult {
        private final String backendSpecs;
        private final String frontendSpecs;
        private final String pseudoCode;
        private final String integrationSpecs;
        private final String ctoReview;
        
        public DevelopmentResult(String backendSpecs, String frontendSpecs, String pseudoCode,
                                String integrationSpecs, String ctoReview) {
            this.backendSpecs = backendSpecs;
            this.frontendSpecs = frontendSpecs;
            this.pseudoCode = pseudoCode;
            this.integrationSpecs = integrationSpecs;
            this.ctoReview = ctoReview;
        }
        
        // Getters
        public String getBackendSpecs() { return backendSpecs; }
        public String getFrontendSpecs() { return frontendSpecs; }
        public String getPseudoCode() { return pseudoCode; }
        public String getIntegrationSpecs() { return integrationSpecs; }
        public String getCtoReview() { return ctoReview; }
    }
}



