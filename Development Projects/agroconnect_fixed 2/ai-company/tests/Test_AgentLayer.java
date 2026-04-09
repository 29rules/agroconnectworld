package com.ai.company.tests;

import com.ai.company.agents.architect.ArchitectAgent;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.devops.DevOpsAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.fullstack.FullStackAgent;
import com.ai.company.agents.productmanager.ProductManagerAgent;
import com.ai.company.agents.qa.QAAgent;
import com.ai.company.agents.scrummaster.ScrumMasterAgent;
import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.deployment.DeploymentPlanAgent;
import com.ai.company.registry.AgentRegistry;
import com.ai.company.retro.RetroAgent;
import com.ai.company.standup.StandupAgent;
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
 * Agent Layer Test
 * 
 * Tests all agents in the AI company system:
 * - Instantiates AgentRegistry
 * - Pings each agent with test tasks
 * - Validates JSON outputs
 * - Ensures no code modification attempts
 * - Logs failures
 */
public class Test_AgentLayer {
    
    private static final Logger log = LoggerFactory.getLogger(Test_AgentLayer.class);
    
    private final AgentRegistry agentRegistry;
    private final ChatLanguageModel chatModel;
    private final AgentOutputValidator validator;
    private final ObjectMapper objectMapper;
    private final Map<String, Boolean> testResults;
    
    public Test_AgentLayer(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        this.agentRegistry = AgentRegistry.getInstance(chatModel);
        this.validator = new AgentOutputValidator();
        this.objectMapper = new ObjectMapper();
        this.testResults = new HashMap<>();
    }
    
    /**
     * Runs all agent tests.
     */
    public void runAllTests() {
        System.out.println("=".repeat(80));
        System.out.println("AGENT LAYER TEST SUITE");
        System.out.println("=".repeat(80));
        System.out.println();
        
        // Test CTO Agent
        testCTOAgent();
        
        // Test Architect Agent
        testArchitectAgent();
        
        // Test Engineer Agent
        testEngineerAgent();
        
        // Test FullStack Agent
        testFullStackAgent();
        
        // Test DevOps Agent
        testDevOpsAgent();
        
        // Test QA Agent
        testQAAgent();
        
        // Test Product Manager Agent
        testProductManagerAgent();
        
        // Test Scrum Master Agent
        testScrumMasterAgent();
        
        // Test Standup Agent
        testStandupAgent();
        
        // Test Retro Agent
        testRetroAgent();
        
        // Test Deployment Plan Agent
        testDeploymentPlanAgent();
        
        // Test Supervisor Agent
        testSupervisorAgent();
        
        // Print summary
        printSummary();
    }
    
    /**
     * Tests CTO Agent.
     */
    private void testCTOAgent() {
        String agentName = "CTOAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            CTOAgent agent = agentRegistry.getCTOAgent();
            String task = "Review system health";
            String sessionId = "test-cto-" + System.currentTimeMillis();
            
            String response = agent.generateDocumentation(task, sessionId);
            
            // Validate response
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests Architect Agent.
     */
    private void testArchitectAgent() {
        String agentName = "ArchitectAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            ArchitectAgent agent = agentRegistry.getArchitectAgent();
            String task = "Design a simple inventory module";
            String sessionId = "test-architect-" + System.currentTimeMillis();
            
            String response = agent.designArchitecture(task, sessionId);
            
            // Validate response
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests Engineer Agent.
     */
    private void testEngineerAgent() {
        String agentName = "EngineerAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            EngineerAgent agent = agentRegistry.getEngineerAgent();
            String task = "Produce backend spec for calculating discounts";
            String sessionId = "test-engineer-" + System.currentTimeMillis();
            
            String response = agent.generatePseudoCode(task, sessionId);
            
            // Validate response
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests FullStack Agent.
     */
    private void testFullStackAgent() {
        String agentName = "FullStackAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            FullStackAgent agent = agentRegistry.getFullStackAgent();
            String task = "Suggest UI component structure for product card";
            String sessionId = "test-fullstack-" + System.currentTimeMillis();
            
            String response = agent.suggestComponent(task, sessionId);
            
            // Validate response
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests DevOps Agent.
     */
    private void testDevOpsAgent() {
        String agentName = "DevOpsAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            DevOpsAgent agent = agentRegistry.getDevOpsAgent();
            String sessionId = "test-devops-" + System.currentTimeMillis();
            
            // analyzeInfrastructure only takes sessionId
            String response = agent.analyzeInfrastructure(sessionId);
            
            // Validate response
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests QA Agent.
     */
    private void testQAAgent() {
        String agentName = "QAAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            QAAgent agent = agentRegistry.getQAAgent();
            String task = "Write test plan for login endpoint";
            String sessionId = "test-qa-" + System.currentTimeMillis();
            
            String response = agent.createTestPlan(task, sessionId);
            
            // Validate response
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests Product Manager Agent.
     */
    private void testProductManagerAgent() {
        String agentName = "ProductManagerAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            ProductManagerAgent agent = agentRegistry.getProductManagerAgent();
            String task = "Create epic for seller analytics";
            String sessionId = "test-pm-" + System.currentTimeMillis();
            
            String response = agent.createEpic(task, sessionId);
            
            // Validate response
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests Scrum Master Agent.
     */
    private void testScrumMasterAgent() {
        String agentName = "ScrumMasterAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            ScrumMasterAgent agent = new ScrumMasterAgent(chatModel);
            String task = "Create sprint plan for 2-week cycle";
            String sessionId = "test-scrum-" + System.currentTimeMillis();
            
            String response = agent.createSprintPlan(1, 2, task, sessionId);
            
            // Validate JSON response
            boolean isValidJson = isValidJson(response);
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = isValidJson && validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                if (!isValidJson) {
                    System.out.println("  Invalid JSON format");
                }
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests Standup Agent.
     */
    private void testStandupAgent() {
        String agentName = "StandupAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            StandupAgent agent = new StandupAgent(chatModel);
            String sessionId = "test-standup-" + System.currentTimeMillis();
            
            String backlogState = "Backlog: 10 TODO, 5 IN_PROGRESS, 3 DONE";
            String yesterdayProgress = "Completed: User Registration API";
            String blockers = "No blockers";
            
            String response = agent.generateStandupReport(sessionId, backlogState, yesterdayProgress, blockers);
            
            // Validate JSON response
            boolean isValidJson = isValidJson(response);
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = isValidJson && validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                if (!isValidJson) {
                    System.out.println("  Invalid JSON format");
                }
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests Retro Agent.
     */
    private void testRetroAgent() {
        String agentName = "RetroAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            RetroAgent agent = new RetroAgent(chatModel);
            String sessionId = "test-retro-" + System.currentTimeMillis();
            
            String sprintResults = "Sprint: Completed 8/10 stories, 40/50 story points";
            String burndownData = "Burndown: On track";
            String blockers = "Blockers: Database migration issue";
            String velocityReport = "Velocity: 20 story points";
            
            String response = agent.generateRetrospective(
                sessionId, sprintResults, burndownData, blockers, velocityReport);
            
            // Validate JSON response
            boolean isValidJson = isValidJson(response);
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = isValidJson && validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                if (!isValidJson) {
                    System.out.println("  Invalid JSON format");
                }
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests Deployment Plan Agent.
     */
    private void testDeploymentPlanAgent() {
        String agentName = "DeploymentPlanAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            DeploymentPlanAgent agent = new DeploymentPlanAgent(chatModel);
            String services = "auth-service,product-service,gateway";
            String environment = "production";
            String sessionId = "test-deployment-" + System.currentTimeMillis();
            
            String response = agent.generateDeploymentPlan(services, environment, sessionId);
            
            // Validate response
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Tests Supervisor Agent.
     */
    private void testSupervisorAgent() {
        String agentName = "SupervisorAgent";
        System.out.println("Testing " + agentName + "...");
        
        try {
            SupervisorAgent agent = new SupervisorAgent(chatModel);
            String sessionId = "test-supervisor-" + System.currentTimeMillis();
            
            // Collect all test results for validation
            StringBuilder allResults = new StringBuilder();
            allResults.append("Agent Test Results:\n");
            allResults.append("Validate all of the above agent outputs for compliance:\n");
            for (Map.Entry<String, Boolean> entry : testResults.entrySet()) {
                allResults.append(String.format("  %s: %s\n", entry.getKey(), entry.getValue() ? "PASS" : "FAIL"));
            }
            
            var enforcement = agent.enforceConstraints(allResults.toString(), sessionId);
            String response = enforcement.getReport();
            
            // Validate response
            ValidationResult validation = validator.validate(response, agentName);
            boolean isValid = validation.isValid() && 
                            (validation.getComplianceCheck() == null || 
                             validation.getComplianceCheck().isNoCodeModifications());
            
            testResults.put(agentName, isValid);
            
            if (isValid) {
                System.out.println("  ✓ PASS: " + agentName);
                System.out.println("  Response: " + truncate(response, 100));
                System.out.println("  Compliance: " + (enforcement.isCompliant() ? "COMPLIANT" : "NON-COMPLIANT"));
            } else {
                System.out.println("  ✗ FAIL: " + agentName);
                System.out.println("  Validation: " + validation.getErrors());
            }
            
        } catch (Exception e) {
            log.error("Error testing " + agentName, e);
            testResults.put(agentName, false);
            System.out.println("  ✗ FAIL: " + agentName + " - " + e.getMessage());
        }
        
        System.out.println();
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
            // Not JSON or invalid JSON
            return false;
        }
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
        Test_AgentLayer test = new Test_AgentLayer(chatModel);
        test.runAllTests();
    }
}

