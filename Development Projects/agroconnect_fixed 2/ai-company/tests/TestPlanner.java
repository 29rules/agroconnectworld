package com.ai.company.tests;

import com.ai.company.planner.TaskPlanner;
import com.ai.company.planner.PlanningOutput;
import com.ai.company.registry.AgentRegistry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

/**
 * Test class for Task Planner system.
 * 
 * This test:
 * - Instantiates TaskPlanner
 * - Creates a task graph for a feature request
 * - Validates task decomposition and assignment
 * 
 * ZERO-IMPACT MODE: This test is safe and does not modify any existing code.
 * All outputs are planning specifications only.
 */
public class TestPlanner {
    
    private static final String TEST_SESSION_ID = "planner-test-" + System.currentTimeMillis();
    
    public static void main(String[] args) {
        try {
            // Initialize ChatLanguageModel
            ChatLanguageModel chatModel = createChatModel();
            
            // Initialize AgentRegistry
            AgentRegistry agentRegistry = AgentRegistry.getInstance(chatModel);
            
            // Create TaskAssignmentEngine
            com.ai.company.planner.TaskAssignmentEngine assignmentEngine = 
                new com.ai.company.planner.TaskAssignmentEngine();
            
            // Create TaskPlanner
            TaskPlanner taskPlanner = new TaskPlanner(chatModel, assignmentEngine);
            
            // Test task decomposition
            String featureRequest = "Build a Seller Dashboard for AgroConnectWorld with product listing, order management, and analytics.";
            
            PlanningOutput result = taskPlanner.plan(featureRequest, TEST_SESSION_ID);
            
            // Validate result
            if (result != null && result.getTasks() != null && !result.getTasks().isEmpty()) {
                System.out.println("Task graph generated successfully");
                System.out.println("Total tasks: " + result.getTasks().size());
            } else {
                throw new RuntimeException("Task planner returned empty result");
            }
            
        } catch (Exception e) {
            System.err.println("TestPlanner failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static ChatLanguageModel createChatModel() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey != null && !apiKey.isEmpty()) {
            if (apiKey.startsWith("sk-or-v1-")) {
                return OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl("https://openrouter.ai/api/v1")
                    .modelName("openai/gpt-4o-mini")
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            } else {
                return OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName("gpt-4o-mini")
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            }
        }
        
        throw new IllegalStateException(
            "OPENAI_API_KEY not found. Please set the environment variable."
        );
    }
}

