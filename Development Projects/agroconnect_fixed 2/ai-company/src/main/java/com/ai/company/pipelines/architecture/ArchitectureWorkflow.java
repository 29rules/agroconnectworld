package com.ai.company.pipelines.architecture;

import com.ai.company.agents.architect.ArchitectAgent;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.devops.DevOpsAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Architecture Workflow
 * 
 * Converts product specifications into technical architecture designs,
 * API contracts, and system interaction diagrams.
 * 
 * Participating Agents:
 * - AI Architect Agent (primary)
 * - DevOps Agent (infrastructure input)
 * - CTO Agent (approval)
 */
public class ArchitectureWorkflow {
    
    private static final Logger log = LoggerFactory.getLogger(ArchitectureWorkflow.class);
    
    private final ArchitectAgent architect;
    private final DevOpsAgent devops;
    private final CTOAgent cto;
    
    public ArchitectureWorkflow(ArchitectAgent architect, DevOpsAgent devops, CTOAgent cto) {
        this.architect = architect;
        this.devops = devops;
        this.cto = cto;
    }
    
    /**
     * Execute architecture workflow
     * 
     * Input: Product specifications from Planning workflow
     * Output: Architecture specifications, API contracts, service diagrams
     */
    public ArchitectureResult execute(String productSpecs, String sessionId) {
        log.info("Starting Architecture Workflow for session: {}", sessionId);
        
        // Step 1: Analyze existing infrastructure
        log.info("Step 1: Analyzing existing infrastructure");
        String infrastructureAnalysis = devops.analyzeInfrastructure(sessionId);
        
        // Step 2: AI Architect designs architecture
        log.info("Step 2: Designing system architecture");
        String architectureDesign = architect.designArchitecture(productSpecs, sessionId);
        
        // Step 3: Generate API contracts
        log.info("Step 3: Generating API contracts");
        String apiContracts = architect.generateApiContract("", "", sessionId);
        
        // Step 4: Create service interaction diagrams
        log.info("Step 4: Creating service interaction diagrams");
        String serviceDiagrams = architect.createServiceDiagram(architectureDesign, sessionId);
        
        // Step 5: CTO reviews and approves
        log.info("Step 5: CTO reviewing architecture");
        String ctoApproval = cto.reviewArchitecture(architectureDesign, sessionId);
        
        log.info("Architecture Workflow completed for session: {}", sessionId);
        
        return new ArchitectureResult(
            infrastructureAnalysis,
            architectureDesign,
            apiContracts,
            serviceDiagrams,
            ctoApproval
        );
    }
    
    /**
     * Result of architecture workflow
     */
    public static class ArchitectureResult {
        private final String infrastructureAnalysis;
        private final String architectureDesign;
        private final String apiContracts;
        private final String serviceDiagrams;
        private final String ctoApproval;
        
        public ArchitectureResult(String infrastructureAnalysis, String architectureDesign,
                                  String apiContracts, String serviceDiagrams, String ctoApproval) {
            this.infrastructureAnalysis = infrastructureAnalysis;
            this.architectureDesign = architectureDesign;
            this.apiContracts = apiContracts;
            this.serviceDiagrams = serviceDiagrams;
            this.ctoApproval = ctoApproval;
        }
        
        // Getters
        public String getInfrastructureAnalysis() { return infrastructureAnalysis; }
        public String getArchitectureDesign() { return architectureDesign; }
        public String getApiContracts() { return apiContracts; }
        public String getServiceDiagrams() { return serviceDiagrams; }
        public String getCtoApproval() { return ctoApproval; }
    }
}



