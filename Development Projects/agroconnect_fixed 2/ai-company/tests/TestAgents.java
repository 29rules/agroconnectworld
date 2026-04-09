package com.ai.company.tests;

import com.ai.company.agents.architect.ArchitectAgent;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.devops.DevOpsAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.fullstack.FullStackAgent;
import com.ai.company.agents.productmanager.ProductManagerAgent;
import com.ai.company.agents.qa.QAAgent;
import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.registry.AgentRegistry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for all AI agents.
 * 
 * This test:
 * - Instantiates AgentRegistry with all agents
 * - Calls each agent with a simple test task
 * - Prints all results to console
 * 
 * ZERO-IMPACT MODE: This test is safe and does not modify any existing code.
 * All agent outputs are specifications only.
 */
public class TestAgents {
    
    private static final String TEST_SESSION_ID = "test-session-" + System.currentTimeMillis();
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("AgroConnectWorld AI Company - Agent Test Suite");
        System.out.println("=".repeat(80));
        System.out.println("Session ID: " + TEST_SESSION_ID);
        System.out.println("Zero-Impact Mode: ENABLED");
        System.out.println("=".repeat(80));
        System.out.println();
        
        try {
            // Initialize ChatLanguageModel
            // NOTE: For testing, you need to provide an API key or use a mock model
            // This example uses OpenAI, but you can replace with any ChatLanguageModel implementation
            ChatLanguageModel chatModel = createChatModel();
            
            // Initialize AgentRegistry
            System.out.println("Initializing AgentRegistry...");
            AgentRegistry registry = AgentRegistry.getInstance(chatModel);
            System.out.println("✓ AgentRegistry initialized with " + registry.getAgentNames().size() + " agents");
            System.out.println();
            
            // Test results storage
            Map<String, String> agentResults = new HashMap<>();
            
            // Test 1: CTO Agent
            System.out.println("-".repeat(80));
            System.out.println("TEST 1: CTO Agent");
            System.out.println("-".repeat(80));
            testCTOAgent(registry, agentResults);
            System.out.println();
            
            // Test 2: Architect Agent
            System.out.println("-".repeat(80));
            System.out.println("TEST 2: Architect Agent");
            System.out.println("-".repeat(80));
            testArchitectAgent(registry, agentResults);
            System.out.println();
            
            // Test 3: Engineer Agent
            System.out.println("-".repeat(80));
            System.out.println("TEST 3: Engineer Agent");
            System.out.println("-".repeat(80));
            testEngineerAgent(registry, agentResults);
            System.out.println();
            
            // Test 4: DevOps Agent
            System.out.println("-".repeat(80));
            System.out.println("TEST 4: DevOps Agent");
            System.out.println("-".repeat(80));
            testDevOpsAgent(registry, agentResults);
            System.out.println();
            
            // Test 5: Full-Stack Agent
            System.out.println("-".repeat(80));
            System.out.println("TEST 5: Full-Stack Agent");
            System.out.println("-".repeat(80));
            testFullStackAgent(registry, agentResults);
            System.out.println();
            
            // Test 6: QA Agent
            System.out.println("-".repeat(80));
            System.out.println("TEST 6: QA Agent");
            System.out.println("-".repeat(80));
            testQAAgent(registry, agentResults);
            System.out.println();
            
            // Test 7: Product Manager Agent
            System.out.println("-".repeat(80));
            System.out.println("TEST 7: Product Manager Agent");
            System.out.println("-".repeat(80));
            testProductManagerAgent(registry, agentResults);
            System.out.println();
            
            // Test 8: Supervisor Agent
            System.out.println("-".repeat(80));
            System.out.println("TEST 8: Supervisor Agent");
            System.out.println("-".repeat(80));
            testSupervisorAgent(registry, agentResults, chatModel);
            System.out.println();
            
            // Summary
            printSummary(agentResults);
            
        } catch (Exception e) {
            System.err.println("ERROR: Test execution failed");
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a ChatLanguageModel instance.
     * 
     * NOTE: Replace this with your actual model configuration.
     * For testing without API keys, you can use a mock implementation.
     */
    private static ChatLanguageModel createChatModel() {
        // Option 1: Use OpenAI or OpenRouter (requires API key)
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey != null && !apiKey.isEmpty()) {
            // Check if it's an OpenRouter key (starts with sk-or-v1-)
            if (apiKey.startsWith("sk-or-v1-")) {
                System.out.println("Using OpenRouter ChatLanguageModel with gpt-4o-mini");
                return OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl("https://openrouter.ai/api/v1")
                    .modelName("openai/gpt-4o-mini")
                    .temperature(0.7)
                    .build();
            } else {
                System.out.println("Using OpenAI ChatLanguageModel with gpt-4o-mini");
                return OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName("gpt-4o-mini")
                    .temperature(0.7)
                    .build();
            }
        }
        
        // Option 2: Throw error if no API key
        throw new IllegalStateException(
            "OPENAI_API_KEY not found. Please set the environment variable to test with real agents.\n" +
            "Example: export OPENAI_API_KEY='your-api-key-here'"
        );
    }
    
    /**
     * Creates a mock ChatLanguageModel for testing without API keys.
     * Note: This is a simplified mock. For real testing, use OpenAI or another provider.
     */
    private static ChatLanguageModel createMockChatModel() {
        // For now, return null and let the real API key be used
        // If no API key, the test will fail gracefully
        return null;
    }
    
    private static void testCTOAgent(AgentRegistry registry, Map<String, String> results) {
        try {
            CTOAgent cto = registry.getCTOAgent();
            String task = "Review system architecture.";
            System.out.println("Task: " + task);
            System.out.println("Executing...");
            
            String response = cto.reviewArchitecture(task, TEST_SESSION_ID);
            results.put("CTO Agent", response);
            
            System.out.println("Response:");
            System.out.println(response);
            System.out.println("✓ CTO Agent test completed");
        } catch (Exception e) {
            System.err.println("✗ CTO Agent test failed: " + e.getMessage());
            results.put("CTO Agent", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testArchitectAgent(AgentRegistry registry, Map<String, String> results) {
        try {
            ArchitectAgent architect = registry.getArchitectAgent();
            String task = "Design order processing feature.";
            System.out.println("Task: " + task);
            System.out.println("Executing...");
            
            String response = architect.designArchitecture(task, TEST_SESSION_ID);
            results.put("Architect Agent", response);
            
            System.out.println("Response:");
            System.out.println(response);
            System.out.println("✓ Architect Agent test completed");
        } catch (Exception e) {
            System.err.println("✗ Architect Agent test failed: " + e.getMessage());
            results.put("Architect Agent", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testEngineerAgent(AgentRegistry registry, Map<String, String> results) {
        try {
            EngineerAgent engineer = registry.getEngineerAgent();
            String task = "Create implementation outline.";
            System.out.println("Task: " + task);
            System.out.println("Executing...");
            
            String response = engineer.createImplementationSpec(task, TEST_SESSION_ID);
            results.put("Engineer Agent", response);
            
            System.out.println("Response:");
            System.out.println(response);
            System.out.println("✓ Engineer Agent test completed");
        } catch (Exception e) {
            System.err.println("✗ Engineer Agent test failed: " + e.getMessage());
            results.put("Engineer Agent", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testDevOpsAgent(AgentRegistry registry, Map<String, String> results) {
        try {
            DevOpsAgent devops = registry.getDevOpsAgent();
            String task = "Analyze docker compose.";
            System.out.println("Task: " + task);
            System.out.println("Executing...");
            
            String response = devops.analyzeInfrastructure(TEST_SESSION_ID);
            results.put("DevOps Agent", response);
            
            System.out.println("Response:");
            System.out.println(response);
            System.out.println("✓ DevOps Agent test completed");
        } catch (Exception e) {
            System.err.println("✗ DevOps Agent test failed: " + e.getMessage());
            results.put("DevOps Agent", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testFullStackAgent(AgentRegistry registry, Map<String, String> results) {
        try {
            FullStackAgent fullstack = registry.getFullStackAgent();
            String task = "Suggest UI for order status page.";
            System.out.println("Task: " + task);
            System.out.println("Executing...");
            
            String response = fullstack.suggestComponent(task, TEST_SESSION_ID);
            results.put("Full-Stack Agent", response);
            
            System.out.println("Response:");
            System.out.println(response);
            System.out.println("✓ Full-Stack Agent test completed");
        } catch (Exception e) {
            System.err.println("✗ Full-Stack Agent test failed: " + e.getMessage());
            results.put("Full-Stack Agent", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testQAAgent(AgentRegistry registry, Map<String, String> results) {
        try {
            QAAgent qa = registry.getQAAgent();
            String task = "Write test plan for login feature.";
            System.out.println("Task: " + task);
            System.out.println("Executing...");
            
            String response = qa.createTestPlan(task, TEST_SESSION_ID);
            results.put("QA Agent", response);
            
            System.out.println("Response:");
            System.out.println(response);
            System.out.println("✓ QA Agent test completed");
        } catch (Exception e) {
            System.err.println("✗ QA Agent test failed: " + e.getMessage());
            results.put("QA Agent", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testProductManagerAgent(AgentRegistry registry, Map<String, String> results) {
        try {
            ProductManagerAgent pm = registry.getProductManagerAgent();
            String task = "Define epics for product search.";
            System.out.println("Task: " + task);
            System.out.println("Executing...");
            
            String response = pm.createEpic(task, TEST_SESSION_ID);
            results.put("Product Manager Agent", response);
            
            System.out.println("Response:");
            System.out.println(response);
            System.out.println("✓ Product Manager Agent test completed");
        } catch (Exception e) {
            System.err.println("✗ Product Manager Agent test failed: " + e.getMessage());
            results.put("Product Manager Agent", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testSupervisorAgent(AgentRegistry registry, Map<String, String> results, ChatLanguageModel chatModel) {
        try {
            SupervisorAgent supervisor = new SupervisorAgent(chatModel);
            
            // Collect all previous agent outputs for validation
            StringBuilder allOutputs = new StringBuilder();
            allOutputs.append("Agent Outputs Summary:\n");
            for (Map.Entry<String, String> entry : results.entrySet()) {
                allOutputs.append("\n").append(entry.getKey()).append(":\n");
                String value = entry.getValue();
                if (value != null && value.length() > 200) {
                    allOutputs.append(value.substring(0, 200)).append("...\n");
                } else {
                    allOutputs.append(value != null ? value : "No output").append("\n");
                }
            }
            
            String task = "Validate all outputs.";
            System.out.println("Task: " + task);
            System.out.println("Validating outputs from all agents...");
            System.out.println("Executing...");
            
            // Use enforceConstraints to validate all outputs
            String response = supervisor.enforceConstraints(allOutputs.toString(), TEST_SESSION_ID).getReport();
            results.put("Supervisor Agent", response);
            
            System.out.println("Response:");
            System.out.println(response);
            System.out.println("✓ Supervisor Agent test completed");
        } catch (Exception e) {
            System.err.println("✗ Supervisor Agent test failed: " + e.getMessage());
            results.put("Supervisor Agent", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printSummary(Map<String, String> results) {
        System.out.println("=".repeat(80));
        System.out.println("TEST SUMMARY");
        System.out.println("=".repeat(80));
        System.out.println();
        
        int successCount = 0;
        int errorCount = 0;
        
        for (Map.Entry<String, String> entry : results.entrySet()) {
            String agentName = entry.getKey();
            String result = entry.getValue();
            
            if (result.startsWith("ERROR:")) {
                System.out.println("✗ " + agentName + ": FAILED");
                errorCount++;
            } else {
                System.out.println("✓ " + agentName + ": SUCCESS");
                successCount++;
            }
        }
        
        System.out.println();
        System.out.println("Total Agents Tested: " + results.size());
        System.out.println("Successful: " + successCount);
        System.out.println("Failed: " + errorCount);
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("Zero-Impact Mode: All tests completed safely");
        System.out.println("No existing code was modified");
        System.out.println("=".repeat(80));
    }
}

