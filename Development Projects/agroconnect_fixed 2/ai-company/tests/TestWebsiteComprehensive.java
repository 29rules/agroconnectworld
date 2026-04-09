package com.ai.company.tests;

import com.ai.company.orchestrator.WebsiteTestOrchestrator;
import dev.ai4j.openai4j.OpenAiClient;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive Website Testing
 * 
 * Tests AgroConnectWorld website from all angles across all environments:
 * - Development
 * - UAT
 * - Pre-production
 * - Production
 * 
 * Test Categories:
 * - Functional Testing
 * - API Testing
 * - Performance Testing
 * - Security Testing
 * - UI/UX Testing
 * - Integration Testing
 * - Regression Testing
 */
public class TestWebsiteComprehensive {
    
    private static final Logger log = LoggerFactory.getLogger(TestWebsiteComprehensive.class);
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("🌐 AGROCONNECTWORLD COMPREHENSIVE WEBSITE TESTING");
        System.out.println("=".repeat(80));
        System.out.println();
        
        // Initialize Chat Model
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("OPENROUTER_API_KEY");
        }
        
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("❌ ERROR: OPENAI_API_KEY or OPENROUTER_API_KEY environment variable not set");
            System.exit(1);
        }
        
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
            .apiKey(apiKey)
            .baseUrl("https://openrouter.ai/api/v1")
            .modelName("openai/gpt-4o-mini")
            .timeout(Duration.ofSeconds(60))
            .temperature(0.3)
            .build();
        
        // Test only development environment
        String devUrl = "http://localhost:8080";
        
        System.out.println("📋 Testing Environment:");
        System.out.println("   • DEVELOPMENT: " + devUrl);
        System.out.println();
        
        // Initialize orchestrator
        WebsiteTestOrchestrator orchestrator = new WebsiteTestOrchestrator(chatModel);
        
        try {
            System.out.println("🚀 Starting comprehensive website testing for DEVELOPMENT...");
            System.out.println();
            
            // Test development environment only
            String report = orchestrator.runEnvironmentTests("development", devUrl);
            
            System.out.println("=".repeat(80));
            System.out.println("📊 COMPREHENSIVE TEST REPORT");
            System.out.println("=".repeat(80));
            System.out.println();
            System.out.println(report);
            System.out.println();
            System.out.println("=".repeat(80));
            System.out.println("✅ Testing Complete!");
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            log.error("Error running comprehensive tests", e);
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

