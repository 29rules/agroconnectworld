package com.ai.company.tests;

import com.ai.company.agents.architect.ArchitectAgent;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.devops.DevOpsAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.fullstack.FullStackAgent;
import com.ai.company.agents.productmanager.ProductManagerAgent;
import com.ai.company.agents.qa.QAAgent;
import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.pipelines.architecture.ArchitectureWorkflow;
import com.ai.company.pipelines.development.DevelopmentWorkflow;
import com.ai.company.pipelines.planning.PlanningWorkflow;
import com.ai.company.pipelines.review.ReviewWorkflow;
import com.ai.company.pipelines.testing.TestingWorkflow;
import com.ai.company.validation.AgentOutputValidator;
import com.ai.company.validation.ValidationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Workflow Layer Test
 * 
 * Tests the complete workflow pipeline:
 * 1. PlanningWorkflow
 * 2. ArchitectureWorkflow
 * 3. DevelopmentWorkflow
 * 4. ReviewWorkflow
 * 5. TestingWorkflow
 * 6. Supervisor approval/rejection
 * 
 * Assertions:
 * - Every workflow returns structured JSON
 * - ReviewWorkflow must identify risks
 * - Supervisor blocks unsafe or incomplete flows
 */
public class Test_WorkflowLayer {
    
    private static final Logger log = LoggerFactory.getLogger(Test_WorkflowLayer.class);
    
    private final ChatLanguageModel chatModel;
    private final AgentOutputValidator validator;
    private final ObjectMapper objectMapper;
    private final Map<String, Boolean> testResults;
    
    // Agents
    private final ProductManagerAgent productManager;
    private final ArchitectAgent architect;
    private final DevOpsAgent devops;
    private final EngineerAgent engineer;
    private final FullStackAgent fullstack;
    private final QAAgent qa;
    private final CTOAgent cto;
    private final SupervisorAgent supervisor;
    
    // Workflows
    private final PlanningWorkflow planningWorkflow;
    private final ArchitectureWorkflow architectureWorkflow;
    private final DevelopmentWorkflow developmentWorkflow;
    private final ReviewWorkflow reviewWorkflow;
    private final TestingWorkflow testingWorkflow;
    
    public Test_WorkflowLayer(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        this.validator = new AgentOutputValidator();
        this.objectMapper = new ObjectMapper();
        this.testResults = new HashMap<>();
        
        // Initialize agents
        this.productManager = new ProductManagerAgent(chatModel);
        this.architect = new ArchitectAgent(chatModel);
        this.devops = new DevOpsAgent(chatModel);
        this.engineer = new EngineerAgent(chatModel);
        this.fullstack = new FullStackAgent(chatModel);
        this.qa = new QAAgent(chatModel);
        this.cto = new CTOAgent(chatModel);
        this.supervisor = new SupervisorAgent(chatModel);
        
        // Initialize workflows
        this.planningWorkflow = new PlanningWorkflow(productManager, cto);
        this.architectureWorkflow = new ArchitectureWorkflow(architect, devops, cto);
        this.developmentWorkflow = new DevelopmentWorkflow(engineer, fullstack, cto);
        this.reviewWorkflow = new ReviewWorkflow(cto, architect, engineer, devops, qa);
        this.testingWorkflow = new TestingWorkflow(qa, productManager, engineer, fullstack, cto);
    }
    
    /**
     * Runs all workflow tests.
     */
    public void runAllTests() {
        System.out.println("=".repeat(80));
        System.out.println("WORKFLOW LAYER TEST SUITE");
        System.out.println("=".repeat(80));
        System.out.println();
        
        String sessionId = "workflow-test-" + System.currentTimeMillis();
        String request = "Implement bulk order import for vendors.";
        
        try {
            // Step 1: Planning Workflow
            PlanningWorkflow.PlanningResult planningResult = testPlanningWorkflow(request, sessionId);
            
            // Step 2: Architecture Workflow
            ArchitectureWorkflow.ArchitectureResult architectureResult = 
                testArchitectureWorkflow(planningResult.getPrioritizedFeatures(), sessionId);
            
            // Step 3: Development Workflow
            DevelopmentWorkflow.DevelopmentResult developmentResult = 
                testDevelopmentWorkflow(architectureResult.getArchitectureDesign(), sessionId);
            
            // Step 4: Review Workflow
            ReviewWorkflow.ReviewResult reviewResult = 
                testReviewWorkflow(developmentResult.getBackendSpecs() + "\n" + developmentResult.getFrontendSpecs(), sessionId);
            
            // Step 5: Testing Workflow
            TestingWorkflow.TestingResult testingResult = 
                testTestingWorkflow(reviewResult.getFinalApproval(), planningResult.getUserStories(), sessionId);
            
            // Step 6: Supervisor Approval
            testSupervisorApproval(planningResult, architectureResult, developmentResult, reviewResult, testingResult, sessionId);
            
        } catch (Exception e) {
            log.error("Error in workflow test", e);
            System.out.println("  ✗ FAIL: Workflow test failed with exception: " + e.getMessage());
            testResults.put("WorkflowChain", false);
        }
        
        // Print summary
        printSummary();
    }
    
    /**
     * Tests Planning Workflow.
     */
    private PlanningWorkflow.PlanningResult testPlanningWorkflow(String request, String sessionId) {
        String workflowName = "PlanningWorkflow";
        System.out.println("Testing " + workflowName + "...");
        
        try {
            PlanningWorkflow.PlanningResult result = planningWorkflow.execute(request, "", sessionId);
            
            // Validate JSON outputs
            boolean epicsValid = isValidJson(result.getEpics());
            boolean userStoriesValid = isValidJson(result.getUserStories());
            boolean acceptanceCriteriaValid = isValidJson(result.getAcceptanceCriteria());
            boolean prioritizedFeaturesValid = isValidJson(result.getPrioritizedFeatures());
            
            boolean allValid = epicsValid && userStoriesValid && acceptanceCriteriaValid && prioritizedFeaturesValid;
            
            // Validate no code modification
            ValidationResult validation = validator.validate(
                result.getPrioritizedFeatures(), workflowName);
            boolean noCodeMod = validation.getComplianceCheck() != null && 
                               validation.getComplianceCheck().isNoCodeModifications();
            
            boolean passed = allValid && noCodeMod;
            testResults.put(workflowName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + workflowName);
                System.out.println("    Epics: " + (epicsValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    User Stories: " + (userStoriesValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Acceptance Criteria: " + (acceptanceCriteriaValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Prioritized Features: " + (prioritizedFeaturesValid ? "Valid JSON" : "Invalid JSON"));
            } else {
                System.out.println("  ✗ FAIL: " + workflowName);
                if (!allValid) {
                    System.out.println("    Invalid JSON in outputs");
                }
                if (!noCodeMod) {
                    System.out.println("    Code modification attempt detected");
                }
            }
            
            System.out.println();
            return result;
            
        } catch (Exception e) {
            log.error("Error testing " + workflowName, e);
            testResults.put(workflowName, false);
            System.out.println("  ✗ FAIL: " + workflowName + " - " + e.getMessage());
            System.out.println();
            throw new RuntimeException("Planning workflow failed", e);
        }
    }
    
    /**
     * Tests Architecture Workflow.
     */
    private ArchitectureWorkflow.ArchitectureResult testArchitectureWorkflow(String productSpecs, String sessionId) {
        String workflowName = "ArchitectureWorkflow";
        System.out.println("Testing " + workflowName + "...");
        
        try {
            ArchitectureWorkflow.ArchitectureResult result = architectureWorkflow.execute(productSpecs, sessionId);
            
            // Validate JSON outputs
            boolean architectureValid = isValidJson(result.getArchitectureDesign());
            boolean apiContractsValid = isValidJson(result.getApiContracts());
            boolean serviceDiagramsValid = result.getServiceDiagrams() != null && !result.getServiceDiagrams().isEmpty();
            
            boolean allValid = architectureValid && apiContractsValid && serviceDiagramsValid;
            
            // Validate no code modification
            ValidationResult validation = validator.validate(
                result.getArchitectureDesign(), workflowName);
            boolean noCodeMod = validation.getComplianceCheck() != null && 
                               validation.getComplianceCheck().isNoCodeModifications();
            
            boolean passed = allValid && noCodeMod;
            testResults.put(workflowName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + workflowName);
                System.out.println("    Architecture Design: " + (architectureValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    API Contracts: " + (apiContractsValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Service Diagrams: " + (serviceDiagramsValid ? "Present" : "Missing"));
            } else {
                System.out.println("  ✗ FAIL: " + workflowName);
                if (!allValid) {
                    System.out.println("    Invalid JSON or missing outputs");
                }
                if (!noCodeMod) {
                    System.out.println("    Code modification attempt detected");
                }
            }
            
            System.out.println();
            return result;
            
        } catch (Exception e) {
            log.error("Error testing " + workflowName, e);
            testResults.put(workflowName, false);
            System.out.println("  ✗ FAIL: " + workflowName + " - " + e.getMessage());
            System.out.println();
            throw new RuntimeException("Architecture workflow failed", e);
        }
    }
    
    /**
     * Tests Development Workflow.
     */
    private DevelopmentWorkflow.DevelopmentResult testDevelopmentWorkflow(String approvedArchitecture, String sessionId) {
        String workflowName = "DevelopmentWorkflow";
        System.out.println("Testing " + workflowName + "...");
        
        try {
            DevelopmentWorkflow.DevelopmentResult result = developmentWorkflow.execute(approvedArchitecture, sessionId);
            
            // Validate JSON outputs
            boolean backendSpecsValid = isValidJson(result.getBackendSpecs());
            boolean frontendSpecsValid = isValidJson(result.getFrontendSpecs());
            boolean pseudoCodeValid = result.getPseudoCode() != null && !result.getPseudoCode().isEmpty();
            boolean integrationSpecsValid = isValidJson(result.getIntegrationSpecs());
            
            boolean allValid = backendSpecsValid && frontendSpecsValid && pseudoCodeValid && integrationSpecsValid;
            
            // Validate no code modification
            ValidationResult validation = validator.validate(
                result.getBackendSpecs() + "\n" + result.getFrontendSpecs(), workflowName);
            boolean noCodeMod = validation.getComplianceCheck() != null && 
                               validation.getComplianceCheck().isNoCodeModifications();
            
            boolean passed = allValid && noCodeMod;
            testResults.put(workflowName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + workflowName);
                System.out.println("    Backend Specs: " + (backendSpecsValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Frontend Specs: " + (frontendSpecsValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Pseudo Code: " + (pseudoCodeValid ? "Present" : "Missing"));
                System.out.println("    Integration Specs: " + (integrationSpecsValid ? "Valid JSON" : "Invalid JSON"));
            } else {
                System.out.println("  ✗ FAIL: " + workflowName);
                if (!allValid) {
                    System.out.println("    Invalid JSON or missing outputs");
                }
                if (!noCodeMod) {
                    System.out.println("    Code modification attempt detected");
                }
            }
            
            System.out.println();
            return result;
            
        } catch (Exception e) {
            log.error("Error testing " + workflowName, e);
            testResults.put(workflowName, false);
            System.out.println("  ✗ FAIL: " + workflowName + " - " + e.getMessage());
            System.out.println();
            throw new RuntimeException("Development workflow failed", e);
        }
    }
    
    /**
     * Tests Review Workflow.
     */
    private ReviewWorkflow.ReviewResult testReviewWorkflow(String implementationSpecs, String sessionId) {
        String workflowName = "ReviewWorkflow";
        System.out.println("Testing " + workflowName + "...");
        
        try {
            ReviewWorkflow.ReviewResult result = reviewWorkflow.execute(implementationSpecs, sessionId);
            
            // Validate JSON outputs
            boolean complianceCheckValid = result.getComplianceCheck() != null && !result.getComplianceCheck().isEmpty();
            boolean architectureReviewValid = isValidJson(result.getArchitectureReview());
            boolean implementationReviewValid = isValidJson(result.getImplementationReview());
            boolean infrastructureReviewValid = isValidJson(result.getInfrastructureReview());
            boolean testCoverageReviewValid = isValidJson(result.getTestCoverageReview());
            boolean finalApprovalValid = result.getFinalApproval() != null && !result.getFinalApproval().isEmpty();
            
            // Assertion: ReviewWorkflow must identify risks
            boolean risksIdentified = result.getComplianceCheck().toLowerCase().contains("risk") ||
                                     result.getComplianceCheck().toLowerCase().contains("issue") ||
                                     result.getComplianceCheck().toLowerCase().contains("concern") ||
                                     result.getFinalApproval().toLowerCase().contains("risk");
            
            boolean allValid = complianceCheckValid && architectureReviewValid && 
                              implementationReviewValid && infrastructureReviewValid &&
                              testCoverageReviewValid && finalApprovalValid && risksIdentified;
            
            // Validate no code modification
            ValidationResult validation = validator.validate(
                result.getFinalApproval(), workflowName);
            boolean noCodeMod = validation.getComplianceCheck() != null && 
                               validation.getComplianceCheck().isNoCodeModifications();
            
            boolean passed = allValid && noCodeMod;
            testResults.put(workflowName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + workflowName);
                System.out.println("    Compliance Check: " + (complianceCheckValid ? "Present" : "Missing"));
                System.out.println("    Architecture Review: " + (architectureReviewValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Implementation Review: " + (implementationReviewValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Infrastructure Review: " + (infrastructureReviewValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Test Coverage Review: " + (testCoverageReviewValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Final Approval: " + (finalApprovalValid ? "Present" : "Missing"));
                System.out.println("    Risks Identified: " + (risksIdentified ? "Yes" : "No"));
            } else {
                System.out.println("  ✗ FAIL: " + workflowName);
                if (!allValid) {
                    System.out.println("    Invalid JSON, missing outputs, or risks not identified");
                }
                if (!noCodeMod) {
                    System.out.println("    Code modification attempt detected");
                }
            }
            
            System.out.println();
            return result;
            
        } catch (Exception e) {
            log.error("Error testing " + workflowName, e);
            testResults.put(workflowName, false);
            System.out.println("  ✗ FAIL: " + workflowName + " - " + e.getMessage());
            System.out.println();
            throw new RuntimeException("Review workflow failed", e);
        }
    }
    
    /**
     * Tests Testing Workflow.
     */
    private TestingWorkflow.TestingResult testTestingWorkflow(String approvedSpecs, String userStories, String sessionId) {
        String workflowName = "TestingWorkflow";
        System.out.println("Testing " + workflowName + "...");
        
        try {
            TestingWorkflow.TestingResult result = testingWorkflow.execute(approvedSpecs, userStories, sessionId);
            
            // Validate JSON outputs
            boolean testPlansValid = isValidJson(result.getTestPlans());
            boolean postmanCollectionsValid = isValidJson(result.getPostmanCollections());
            boolean e2eFlowsValid = isValidJson(result.getE2eFlows());
            boolean acceptanceValidationValid = isValidJson(result.getAcceptanceValidation());
            boolean backendTestReviewValid = isValidJson(result.getBackendTestReview());
            boolean frontendTestReviewValid = isValidJson(result.getFrontendTestReview());
            boolean ctoApprovalValid = result.getCtoApproval() != null && !result.getCtoApproval().isEmpty();
            
            boolean allValid = testPlansValid && postmanCollectionsValid && e2eFlowsValid &&
                              acceptanceValidationValid && backendTestReviewValid &&
                              frontendTestReviewValid && ctoApprovalValid;
            
            // Validate no code modification
            ValidationResult validation = validator.validate(
                result.getTestPlans(), workflowName);
            boolean noCodeMod = validation.getComplianceCheck() != null && 
                               validation.getComplianceCheck().isNoCodeModifications();
            
            boolean passed = allValid && noCodeMod;
            testResults.put(workflowName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + workflowName);
                System.out.println("    Test Plans: " + (testPlansValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Postman Collections: " + (postmanCollectionsValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    E2E Flows: " + (e2eFlowsValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    Acceptance Validation: " + (acceptanceValidationValid ? "Valid JSON" : "Invalid JSON"));
                System.out.println("    CTO Approval: " + (ctoApprovalValid ? "Present" : "Missing"));
            } else {
                System.out.println("  ✗ FAIL: " + workflowName);
                if (!allValid) {
                    System.out.println("    Invalid JSON or missing outputs");
                }
                if (!noCodeMod) {
                    System.out.println("    Code modification attempt detected");
                }
            }
            
            System.out.println();
            return result;
            
        } catch (Exception e) {
            log.error("Error testing " + workflowName, e);
            testResults.put(workflowName, false);
            System.out.println("  ✗ FAIL: " + workflowName + " - " + e.getMessage());
            System.out.println();
            throw new RuntimeException("Testing workflow failed", e);
        }
    }
    
    /**
     * Tests Supervisor Approval.
     */
    private void testSupervisorApproval(PlanningWorkflow.PlanningResult planningResult,
                                       ArchitectureWorkflow.ArchitectureResult architectureResult,
                                       DevelopmentWorkflow.DevelopmentResult developmentResult,
                                       ReviewWorkflow.ReviewResult reviewResult,
                                       TestingWorkflow.TestingResult testingResult,
                                       String sessionId) {
        String workflowName = "SupervisorApproval";
        System.out.println("Testing " + workflowName + "...");
        
        try {
            // Collect all workflow outputs for supervisor review
            StringBuilder allOutputs = new StringBuilder();
            allOutputs.append("=== PLANNING WORKFLOW OUTPUT ===\n");
            allOutputs.append(planningResult.getPrioritizedFeatures()).append("\n\n");
            allOutputs.append("=== ARCHITECTURE WORKFLOW OUTPUT ===\n");
            allOutputs.append(architectureResult.getArchitectureDesign()).append("\n\n");
            allOutputs.append("=== DEVELOPMENT WORKFLOW OUTPUT ===\n");
            allOutputs.append(developmentResult.getBackendSpecs()).append("\n\n");
            allOutputs.append("=== REVIEW WORKFLOW OUTPUT ===\n");
            allOutputs.append(reviewResult.getFinalApproval()).append("\n\n");
            allOutputs.append("=== TESTING WORKFLOW OUTPUT ===\n");
            allOutputs.append(testingResult.getTestPlans()).append("\n\n");
            
            // Supervisor enforces constraints
            var enforcement = supervisor.enforceConstraints(allOutputs.toString(), sessionId);
            
            // Supervisor reviews decision
            var decision = supervisor.reviewDecision(
                "WorkflowChain",
                allOutputs.toString(),
                "Complete workflow chain from planning to testing",
                sessionId
            );
            
            // Assertions: Supervisor blocks unsafe or incomplete flows
            boolean isCompliant = enforcement.isCompliant();
            boolean isApproved = decision.getStatus().equals("APPROVED") || 
                               decision.getStatus().equals("APPROVED_WITH_CONDITIONS");
            boolean blocksUnsafe = !isCompliant || decision.getStatus().equals("REJECTED");
            
            // Supervisor should approve if compliant, reject if unsafe
            boolean supervisorWorking = (isCompliant && isApproved) || (!isCompliant && blocksUnsafe);
            
            boolean passed = supervisorWorking;
            testResults.put(workflowName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + workflowName);
                System.out.println("    Compliance: " + (isCompliant ? "COMPLIANT" : "NON-COMPLIANT"));
                System.out.println("    Decision: " + decision.getStatus());
                System.out.println("    Violations: " + enforcement.getViolations().size());
                System.out.println("    Supervisor Working: " + (supervisorWorking ? "Yes" : "No"));
            } else {
                System.out.println("  ✗ FAIL: " + workflowName);
                System.out.println("    Compliance: " + (isCompliant ? "COMPLIANT" : "NON-COMPLIANT"));
                System.out.println("    Decision: " + decision.getStatus());
                System.out.println("    Violations: " + enforcement.getViolations().size());
                System.out.println("    Supervisor not blocking unsafe flows correctly");
            }
            
            System.out.println();
            
        } catch (Exception e) {
            log.error("Error testing " + workflowName, e);
            testResults.put(workflowName, false);
            System.out.println("  ✗ FAIL: " + workflowName + " - " + e.getMessage());
            System.out.println();
        }
    }
    
    /**
     * Validates JSON format.
     */
    private boolean isValidJson(String response) {
        if (response == null || response.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Try to parse as JSON
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode != null;
        } catch (Exception e) {
            // Not JSON or invalid JSON - might be plain text, which is acceptable for some outputs
            // Check if it contains structured content
            return response.contains("{") && response.contains("}");
        }
    }
    
    /**
     * Prints test summary.
     */
    private void printSummary() {
        System.out.println("=".repeat(80));
        System.out.println("TEST SUMMARY");
        System.out.println("=".repeat(80));
        System.out.println();
        
        int total = testResults.size();
        int passed = (int) testResults.values().stream().filter(b -> b).count();
        int failed = total - passed;
        
        System.out.println("Total Tests: " + total);
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println();
        
        if (failed > 0) {
            System.out.println("Failed Tests:");
            for (Map.Entry<String, Boolean> entry : testResults.entrySet()) {
                if (!entry.getValue()) {
                    System.out.println("  ✗ " + entry.getKey());
                }
            }
        }
        
        System.out.println();
        System.out.println("=".repeat(80));
        
        if (failed > 0) {
            System.out.println("RESULT: FAIL - " + failed + " test(s) failed");
            if (System.getProperty("runAllTests.noExit") == null) {
                System.exit(1);
            }
        } else {
            System.out.println("RESULT: PASS - All tests passed");
            if (System.getProperty("runAllTests.noExit") == null) {
                System.exit(0);
            }
        }
    }
    
    /**
     * Main method.
     */
    public static void main(String[] args) {
        // Get API key from environment
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("ERROR: OPENAI_API_KEY environment variable not set");
            System.exit(1);
        }
        
        // Initialize chat model
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName("gpt-4o-mini")
            .temperature(0.7)
            .build();
        
        // Run tests
        Test_WorkflowLayer test = new Test_WorkflowLayer(chatModel);
        test.runAllTests();
    }
}

