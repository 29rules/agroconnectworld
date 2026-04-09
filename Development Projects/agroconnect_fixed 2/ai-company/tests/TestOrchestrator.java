package com.ai.company.tests;

import com.ai.company.orchestrator.MultiAgentOrchestrator;
import com.ai.company.registry.AgentRegistry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;
import java.util.UUID;

/**
 * Test Orchestrator
 * 
 * Tests the MultiAgentOrchestrator by executing a full pipeline
 * for a feature request: "Build a Seller Dashboard for AgroConnectWorld."
 * 
 * Pipeline Flow:
 * 1. Product Manager → Creates product specifications
 * 2. Architect → Designs system architecture
 * 3. CTO → Reviews and approves
 * 4. Engineer → Creates implementation specs
 * 5. Full-Stack → Designs frontend components
 * 6. QA → Creates test plans
 * 7. Supervisor → Final validation
 * 
 * ZERO-IMPACT MODE:
 * - This is an AI-only dry run
 * - No real backend/frontend code is modified
 * - No real microservices are called
 * - All outputs are specifications and documentation only
 */
public class TestOrchestrator {
    
    private static final String FEATURE_REQUEST = "Build a Seller Dashboard for AgroConnectWorld.";
    private static final String TEST_SESSION_ID = "orchestration-test-" + System.currentTimeMillis();
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("AgroConnectWorld AI Company - Multi-Agent Orchestrator Test");
        System.out.println("=".repeat(80));
        System.out.println("Feature Request: " + FEATURE_REQUEST);
        System.out.println("Session ID: " + TEST_SESSION_ID);
        System.out.println("Zero-Impact Mode: ENABLED (AI-only dry run)");
        System.out.println("=".repeat(80));
        System.out.println();
        
        try {
            // Initialize ChatLanguageModel
            ChatLanguageModel chatModel = createChatModel();
            System.out.println("✓ ChatLanguageModel initialized");
            System.out.println();
            
            // Initialize AgentRegistry
            System.out.println("Initializing AgentRegistry...");
            AgentRegistry agentRegistry = AgentRegistry.getInstance(chatModel);
            System.out.println("✓ AgentRegistry initialized with " + agentRegistry.getAgentNames().size() + " agents");
            System.out.println();
            
            // Create MultiAgentOrchestrator
            System.out.println("Creating MultiAgentOrchestrator...");
            MultiAgentOrchestrator orchestrator = new MultiAgentOrchestrator(agentRegistry, chatModel);
            System.out.println("✓ MultiAgentOrchestrator created");
            System.out.println();
            
            // Execute Pipeline
            System.out.println("=".repeat(80));
            System.out.println("EXECUTING MULTI-AGENT PIPELINE");
            System.out.println("=".repeat(80));
            System.out.println();
            
            MultiAgentOrchestrator.OrchestrationResult result = orchestrator.executePipeline(
                FEATURE_REQUEST, 
                TEST_SESSION_ID
            );
            
            // Print Results
            System.out.println();
            System.out.println("=".repeat(80));
            System.out.println("PIPELINE EXECUTION COMPLETE");
            System.out.println("=".repeat(80));
            System.out.println("Status: " + result.getStatus());
            System.out.println("Session ID: " + result.getSessionId());
            System.out.println();
            
            if (result.getStatus().equals("COMPLETED")) {
                System.out.println("=".repeat(80));
                System.out.println("STEP 1: PRODUCT MANAGER OUTPUT");
                System.out.println("=".repeat(80));
                System.out.println(result.getProductManagerOutput());
                System.out.println();
                
                System.out.println("=".repeat(80));
                System.out.println("STEP 2: ARCHITECT OUTPUT");
                System.out.println("=".repeat(80));
                System.out.println(result.getArchitectOutput());
                System.out.println();
                
                System.out.println("=".repeat(80));
                System.out.println("STEP 3: CTO REVIEW OUTPUT");
                System.out.println("=".repeat(80));
                System.out.println(result.getCtoOutput());
                System.out.println();
                
                System.out.println("=".repeat(80));
                System.out.println("STEP 4: ENGINEER OUTPUT");
                System.out.println("=".repeat(80));
                System.out.println(result.getEngineerOutput());
                System.out.println();
                
                System.out.println("=".repeat(80));
                System.out.println("STEP 5: FULL-STACK OUTPUT");
                System.out.println("=".repeat(80));
                System.out.println(result.getFullStackOutput());
                System.out.println();
                
                System.out.println("=".repeat(80));
                System.out.println("STEP 6: QA OUTPUT");
                System.out.println("=".repeat(80));
                System.out.println(result.getQaOutput());
                System.out.println();
                
                System.out.println("=".repeat(80));
                System.out.println("STEP 7: SUPERVISOR VALIDATION OUTPUT");
                System.out.println("=".repeat(80));
                System.out.println(result.getSupervisorOutput());
                System.out.println();
                
                System.out.println("=".repeat(80));
                System.out.println("✓ ALL STEPS COMPLETED SUCCESSFULLY");
                System.out.println("=".repeat(80));
                System.out.println("Zero-Impact Mode: Verified - No code modifications made");
                System.out.println("All outputs are specifications and documentation only");
                System.out.println("=".repeat(80));
            } else {
                System.err.println("Pipeline execution failed!");
                System.err.println("Error: " + result.getErrorMessage());
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: Test execution failed");
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a ChatLanguageModel instance.
     * 
     * Uses OpenRouter with gpt-4o-mini for cost-effective testing.
     */
    private static ChatLanguageModel createChatModel() {
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
                    .timeout(Duration.ofSeconds(120)) // Longer timeout for orchestrator
                    .build();
            } else {
                System.out.println("Using OpenAI ChatLanguageModel with gpt-4o-mini");
                return OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName("gpt-4o-mini")
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(120))
                    .build();
            }
        }
        
        throw new IllegalStateException(
            "OPENAI_API_KEY not found. Please set the environment variable to test with real agents.\n" +
            "Example: export OPENAI_API_KEY='your-api-key-here'"
        );
    }
}

