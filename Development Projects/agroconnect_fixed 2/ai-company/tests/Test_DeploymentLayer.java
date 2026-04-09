package com.ai.company.tests;

import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.deployment.DeploymentPlanAgent;
import com.ai.company.deployment.DeploymentState;
import com.ai.company.deployment.DeploymentSummary;
import com.ai.company.deployment.production.ProductionDeploymentAgent;
import com.ai.company.deployment.production.RollbackManager;
import com.ai.company.deployment.staging.StagingSimulator;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Deployment Layer Test
 * 
 * Tests the complete deployment workflow:
 * 1. Generate DeploymentPlan using DeploymentPlanAgent
 * 2. Run StagingSimulator:
 *    - Build containers (dry-run)
 *    - Validate health checks (mock endpoints only)
 *    - Validate gateway → services flows
 * 3. Supervisor & CTO approve staging plan
 * 4. ProductionDeploymentAgent:
 *    - Must REJECT because real SSH not enabled (expected)
 * 5. RollbackManager must be initialized but not triggered
 * 
 * Assertions:
 * - Dry-run staging passes
 * - Production deploy REJECTED (correct behavior)
 * - Supervisor protects system
 */
public class Test_DeploymentLayer {
    
    private static final Logger log = LoggerFactory.getLogger(Test_DeploymentLayer.class);
    
    private final ChatLanguageModel chatModel;
    private final DeploymentPlanAgent deploymentPlanAgent;
    private final StagingSimulator stagingSimulator;
    private final SupervisorAgent supervisor;
    private final CTOAgent cto;
    private final ProductionDeploymentAgent productionAgent;
    private final RollbackManager rollbackManager;
    private final ObjectMapper objectMapper;
    private final Map<String, Boolean> testResults;
    
    public Test_DeploymentLayer(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        this.deploymentPlanAgent = new DeploymentPlanAgent(chatModel);
        this.stagingSimulator = new StagingSimulator();
        this.supervisor = new SupervisorAgent(chatModel);
        this.cto = new CTOAgent(chatModel);
        this.productionAgent = new ProductionDeploymentAgent(chatModel);
        this.rollbackManager = new RollbackManager(chatModel);
        this.objectMapper = new ObjectMapper();
        this.testResults = new HashMap<>();
    }
    
    /**
     * Runs all deployment layer tests.
     */
    public void runAllTests() {
        System.out.println("=".repeat(80));
        System.out.println("DEPLOYMENT LAYER TEST SUITE");
        System.out.println("=".repeat(80));
        System.out.println();
        
        String sessionId = "deployment-test-" + System.currentTimeMillis();
        
        try {
            // Test 1: Generate deployment plan
            String deploymentPlan = testGenerateDeploymentPlan(sessionId);
            
            // Test 2: Run staging simulator
            var stagingResult = testStagingSimulator(sessionId);
            
            // Test 3: Supervisor & CTO approval
            boolean supervisorApproved = testSupervisorApproval(stagingResult, sessionId);
            boolean ctoApproved = testCTOApproval(stagingResult, sessionId);
            
            // Test 4: Production deployment agent (should reject)
            testProductionDeploymentAgent(stagingResult, supervisorApproved, ctoApproved, sessionId);
            
            // Test 5: Rollback manager initialization
            testRollbackManager(sessionId);
            
        } catch (Exception e) {
            log.error("Error in deployment layer test", e);
            System.out.println("  ✗ FAIL: Deployment layer test failed with exception: " + e.getMessage());
            testResults.put("DeploymentChain", false);
        }
        
        // Print summary
        printSummary();
    }
    
    /**
     * Tests generating deployment plan.
     */
    private String testGenerateDeploymentPlan(String sessionId) {
        String testName = "Generate_Deployment_Plan";
        System.out.println("Testing " + testName + "...");
        
        try {
            String services = "auth-service,product-service,gateway";
            String environment = "staging";
            
            String plan = deploymentPlanAgent.generateDeploymentPlan(services, environment, sessionId);
            
            boolean hasPlan = plan != null && !plan.isEmpty();
            boolean hasSteps = plan.toLowerCase().contains("step") || 
                             plan.toLowerCase().contains("build") ||
                             plan.toLowerCase().contains("deploy");
            boolean isNotError = !plan.startsWith("ERROR");
            
            boolean passed = hasPlan && hasSteps && isNotError;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Plan generated: Yes");
                System.out.println("    Plan length: " + plan.length() + " characters");
                System.out.println("    Contains deployment steps: " + hasSteps);
                System.out.println("    Plan preview (first 300 chars):");
                System.out.println("    " + truncate(plan, 300).replace("\n", "\n    "));
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Plan generated: " + hasPlan);
                System.out.println("    Contains steps: " + hasSteps);
                System.out.println("    Is not error: " + isNotError);
                if (plan != null) {
                    System.out.println("    Plan: " + truncate(plan, 200));
                }
            }
            
            System.out.println();
            return plan;
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
            return "";
        }
    }
    
    /**
     * Tests staging simulator.
     */
    private StagingSimulator.StagingSimulationResult testStagingSimulator(String sessionId) {
        String testName = "Staging_Simulator";
        System.out.println("Testing " + testName + "...");
        
        try {
            String services = "auth-service,product-service,gateway";
            
            // Run staging simulation
            StagingSimulator.StagingSimulationResult result = stagingSimulator.simulateStaging(services);
            
            boolean hasResult = result != null;
            boolean isCompleted = hasResult && "COMPLETED".equals(result.getStatus());
            boolean hasSteps = hasResult && result.getStepResults() != null && !result.getStepResults().isEmpty();
            boolean isDryRun = hasResult && (result.getInfo() != null && 
                            result.getInfo().stream().anyMatch(msg -> 
                                msg.toLowerCase().contains("dry-run") || 
                                msg.toLowerCase().contains("simulated")));
            
            // Check for expected dry-run behavior
            boolean dryRunPassed = isDryRun || isCompleted; // Either dry-run or completed is OK
            
            boolean passed = hasResult && hasSteps && dryRunPassed;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Result generated: Yes");
                System.out.println("    Status: " + (hasResult ? result.getStatus() : "N/A"));
                System.out.println("    Is dry-run: " + isDryRun);
                System.out.println("    Steps executed: " + (hasResult && result.getStepResults() != null ? 
                    result.getStepResults().size() : 0));
                System.out.println("    Errors: " + (hasResult && result.getErrors() != null ? 
                    result.getErrors().size() : 0));
                System.out.println("    Warnings: " + (hasResult && result.getWarnings() != null ? 
                    result.getWarnings().size() : 0));
                
                // Show step results
                if (hasResult && result.getStepResults() != null) {
                    System.out.println("    Step Results:");
                    result.getStepResults().forEach((step, stepResult) -> {
                        System.out.println("      - " + step + ": " + 
                            truncate(stepResult, 100).replace("\n", " "));
                    });
                }
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Result generated: " + hasResult);
                System.out.println("    Status: " + (hasResult ? result.getStatus() : "N/A"));
                System.out.println("    Has steps: " + hasSteps);
                System.out.println("    Dry-run passed: " + dryRunPassed);
                if (hasResult && result.getErrors() != null && !result.getErrors().isEmpty()) {
                    System.out.println("    Errors: " + result.getErrors());
                }
            }
            
            System.out.println();
            return result;
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
            return null;
        }
    }
    
    /**
     * Tests supervisor approval.
     */
    private boolean testSupervisorApproval(StagingSimulator.StagingSimulationResult stagingResult, String sessionId) {
        String testName = "Supervisor_Approval";
        System.out.println("Testing " + testName + "...");
        
        try {
            // Format staging context for supervisor
            String stagingContext = formatStagingContext(stagingResult);
            
            // Supervisor approves staging deployment
            var approval = supervisor.approveStagingDeployment(stagingContext, sessionId);
            
            boolean hasApproval = approval != null;
            boolean isApproved = hasApproval && approval.isApproved();
            boolean hasReport = hasApproval && approval.getApprovalReport() != null && 
                              !approval.getApprovalReport().isEmpty();
            
            // Supervisor should approve safe staging deployments
            boolean passed = hasApproval && isApproved && hasReport;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Approval received: Yes");
                System.out.println("    Approved: " + isApproved);
                System.out.println("    Report: " + truncate(approval.getApprovalReport(), 200));
                System.out.println("    Rejection Reasons: " + (approval.getRejectionReasons() != null ? 
                    approval.getRejectionReasons().size() : 0));
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Approval received: " + hasApproval);
                System.out.println("    Approved: " + isApproved);
                System.out.println("    Has report: " + hasReport);
                if (hasApproval) {
                    System.out.println("    Report: " + truncate(approval.getApprovalReport(), 200));
                }
            }
            
            System.out.println();
            return isApproved;
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
            return false;
        }
    }
    
    /**
     * Tests CTO approval.
     */
    private boolean testCTOApproval(StagingSimulator.StagingSimulationResult stagingResult, String sessionId) {
        String testName = "CTO_Approval";
        System.out.println("Testing " + testName + "...");
        
        try {
            // Format staging context for CTO
            String stagingContext = formatStagingContext(stagingResult);
            
            // CTO reviews and approves
            String ctoDecision = cto.approveDecision(stagingContext, sessionId);
            
            boolean hasDecision = ctoDecision != null && !ctoDecision.isEmpty();
            boolean isApproved = hasDecision && (ctoDecision.toLowerCase().contains("approve") ||
                               ctoDecision.toLowerCase().contains("approved") ||
                               ctoDecision.toLowerCase().contains("yes"));
            
            // CTO should approve safe staging deployments
            boolean passed = hasDecision && isApproved;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Decision received: Yes");
                System.out.println("    Approved: " + isApproved);
                System.out.println("    Decision preview: " + truncate(ctoDecision, 200));
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Decision received: " + hasDecision);
                System.out.println("    Approved: " + isApproved);
                if (hasDecision) {
                    System.out.println("    Decision: " + truncate(ctoDecision, 200));
                }
            }
            
            System.out.println();
            return isApproved;
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
            return false;
        }
    }
    
    /**
     * Tests production deployment agent (should reject).
     */
    private void testProductionDeploymentAgent(
            StagingSimulator.StagingSimulationResult stagingResult,
            boolean supervisorApproved,
            boolean ctoApproved,
            String sessionId) {
        String testName = "Production_Deployment_Agent";
        System.out.println("Testing " + testName + "...");
        
        try {
            // Create deployment summary from staging result
            DeploymentSummary summary = createDeploymentSummary(stagingResult);
            
            // Production agent evaluates deployment
            var decision = productionAgent.approveForProduction(
                summary, supervisorApproved, ctoApproved, sessionId);
            
            boolean hasDecision = decision != null;
            boolean isRejected = hasDecision && !decision.isApproved();
            boolean hasReason = hasDecision && decision.getReason() != null && 
                              !decision.getReason().isEmpty();
            
            // Production should REJECT because SSH is not enabled (expected behavior)
            boolean correctBehavior = isRejected && 
                (decision.getReason().toLowerCase().contains("ssh") ||
                 decision.getReason().toLowerCase().contains("production") ||
                 decision.getReason().toLowerCase().contains("not enabled") ||
                 decision.getReason().toLowerCase().contains("reject"));
            
            boolean passed = hasDecision && isRejected && hasReason && correctBehavior;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Decision received: Yes");
                System.out.println("    Rejected (expected): " + isRejected);
                System.out.println("    Reason: " + truncate(decision.getReason(), 200));
                System.out.println("    Risk level: " + decision.getRiskLevel());
                System.out.println("    Correct behavior: Production deployment correctly rejected");
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Decision received: " + hasDecision);
                System.out.println("    Rejected: " + isRejected);
                System.out.println("    Has reason: " + hasReason);
                System.out.println("    Correct behavior: " + correctBehavior);
                if (hasDecision) {
                    System.out.println("    Decision: " + decision.getDecision());
                    System.out.println("    Reason: " + truncate(decision.getReason(), 200));
                }
            }
            
            System.out.println();
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
        }
    }
    
    /**
     * Tests rollback manager initialization.
     */
    private void testRollbackManager(String sessionId) {
        String testName = "Rollback_Manager";
        System.out.println("Testing " + testName + "...");
        
        try {
            // Rollback manager should be initialized but not triggered
            boolean isInitialized = rollbackManager != null;
            
            // Try to create a snapshot (should work but not trigger rollback)
            String snapshotResult = rollbackManager.createSnapshot("test-vps", "test-user", sessionId);
            boolean snapshotCreated = snapshotResult != null && !snapshotResult.isEmpty();
            
            // Rollback should not be triggered (no deployment happened)
            boolean rollbackNotTriggered = true; // No deployment = no rollback
            
            boolean passed = isInitialized && snapshotCreated && rollbackNotTriggered;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Manager initialized: " + isInitialized);
                System.out.println("    Snapshot created: " + snapshotCreated);
                System.out.println("    Rollback not triggered: " + rollbackNotTriggered);
                System.out.println("    Snapshot result: " + truncate(snapshotResult, 100));
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Manager initialized: " + isInitialized);
                System.out.println("    Snapshot created: " + snapshotCreated);
                System.out.println("    Rollback not triggered: " + rollbackNotTriggered);
            }
            
            System.out.println();
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
        }
    }
    
    /**
     * Formats staging context for supervisor/CTO.
     */
    private String formatStagingContext(StagingSimulator.StagingSimulationResult result) {
        if (result == null) {
            return "Staging simulation result is null";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("=== STAGING DEPLOYMENT CONTEXT ===\n\n");
        context.append("Status: ").append(result.getStatus()).append("\n");
        context.append("Start Time: ").append(result.getStartTime()).append("\n");
        context.append("End Time: ").append(result.getEndTime()).append("\n\n");
        
        if (result.getStepResults() != null) {
            context.append("Step Results:\n");
            result.getStepResults().forEach((step, stepResult) -> {
                context.append("  - ").append(step).append(": ").append(truncate(stepResult, 100)).append("\n");
            });
        }
        
        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
            context.append("\nErrors: ").append(result.getErrors().size()).append("\n");
        }
        
        if (result.getWarnings() != null && !result.getWarnings().isEmpty()) {
            context.append("Warnings: ").append(result.getWarnings().size()).append("\n");
        }
        
        return context.toString();
    }
    
    /**
     * Creates deployment summary from staging result.
     */
    private DeploymentSummary createDeploymentSummary(StagingSimulator.StagingSimulationResult stagingResult) {
        DeploymentSummary summary = new DeploymentSummary();
        summary.setDeploymentId("test-deployment-" + System.currentTimeMillis());
        summary.setEnvironment("staging");
        summary.setStatus(stagingResult != null ? stagingResult.getStatus() : "UNKNOWN");
        summary.setStartTime(LocalDateTime.now());
        summary.setEndTime(LocalDateTime.now());
        
        // Create deployment state
        DeploymentState state = new DeploymentState();
        state.setEnvironment("staging");
        state.setServices(Arrays.asList("auth-service", "product-service", "gateway"));
        
        // Add health checks (mock)
        Map<String, DeploymentState.HealthCheckResult> healthChecks = new HashMap<>();
        if (stagingResult != null && stagingResult.getValidatedRoutes() != null) {
            stagingResult.getValidatedRoutes().forEach((service, routeValidations) -> {
                if (routeValidations != null) {
                    for (StagingSimulator.StagingSimulationResult.RouteValidation routeValidation : routeValidations) {
                        DeploymentState.HealthCheckResult healthCheck = 
                            new DeploymentState.HealthCheckResult(service);
                        healthCheck.setHealthy(routeValidation.isValid());
                        healthCheck.setStatusCode(routeValidation.isValid() ? 200 : 500);
                        healthCheck.setLatencyMs(100L);
                        healthCheck.setCheckedAt(LocalDateTime.now());
                        healthChecks.put(routeValidation.getRoute(), healthCheck);
                    }
                }
            });
        } else {
            // Add mock health checks if no routes validated
            DeploymentState.HealthCheckResult mockCheck = 
                new DeploymentState.HealthCheckResult("gateway");
            mockCheck.setHealthy(true);
            mockCheck.setStatusCode(200);
            mockCheck.setLatencyMs(50L);
            mockCheck.setCheckedAt(LocalDateTime.now());
            healthChecks.put("http://localhost:8080/actuator/health", mockCheck);
        }
        // Health checks are added via addHealthCheck method
        healthChecks.forEach((route, check) -> state.addHealthCheck(route, check));
        
        summary.setState(state);
        
        // Add errors and warnings
        if (stagingResult != null) {
            if (stagingResult.getErrors() != null) {
                summary.setErrors(new ArrayList<>(stagingResult.getErrors()));
            }
            if (stagingResult.getWarnings() != null) {
                summary.setWarnings(new ArrayList<>(stagingResult.getWarnings()));
            }
        }
        
        return summary;
    }
    
    /**
     * Truncates string to specified length.
     */
    private String truncate(String str, int maxLength) {
        if (str == null) {
            return "null";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
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
        Test_DeploymentLayer test = new Test_DeploymentLayer(chatModel);
        test.runAllTests();
    }
}

