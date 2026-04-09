package com.ai.company.orchestrator;

import com.ai.company.agents.testing.WebsiteTestAgent;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Website Test Orchestrator
 * 
 * Orchestrates comprehensive website testing across all environments.
 * Tests from all angles: functional, API, performance, security, UI/UX, integration, regression.
 */
public class WebsiteTestOrchestrator {
    
    private static final Logger log = LoggerFactory.getLogger(WebsiteTestOrchestrator.class);
    
    private final WebsiteTestAgent testAgent;
    
    public WebsiteTestOrchestrator(ChatLanguageModel chatModel) {
        this.testAgent = new WebsiteTestAgent(chatModel);
    }
    
    /**
     * Run comprehensive tests for all environments.
     * 
     * @param environments Map of environment name to base URL
     * @return Comprehensive test report for all environments
     */
    public String runAllEnvironmentTests(Map<String, String> environments) {
        log.info("Running comprehensive tests for all environments: {}", environments.keySet());
        
        String sessionId = UUID.randomUUID().toString();
        Map<String, String> environmentResults = new HashMap<>();
        
        for (Map.Entry<String, String> env : environments.entrySet()) {
            String envName = env.getKey();
            String baseUrl = env.getValue();
            
            log.info("Testing environment: {} at {}", envName, baseUrl);
            
            try {
                String result = testAgent.runComprehensiveTests(baseUrl, envName, sessionId);
                environmentResults.put(envName, result);
            } catch (Exception e) {
                log.error("Error testing environment: {}", envName, e);
                environmentResults.put(envName, "ERROR: " + e.getMessage());
            }
        }
        
        // Generate comprehensive report
        StringBuilder finalReport = new StringBuilder();
        finalReport.append("=== COMPREHENSIVE TEST REPORT FOR ALL ENVIRONMENTS ===\n\n");
        
        for (Map.Entry<String, String> entry : environmentResults.entrySet()) {
            finalReport.append("--- ").append(entry.getKey().toUpperCase()).append(" ENVIRONMENT ---\n");
            finalReport.append(entry.getValue()).append("\n\n");
        }
        
        return finalReport.toString();
    }
    
    /**
     * Run tests for a specific environment.
     * 
     * @param environment Environment name (dev, uat, preprod, production)
     * @param baseUrl Base URL
     * @return Test report
     */
    public String runEnvironmentTests(String environment, String baseUrl) {
        log.info("Running tests for environment: {} at {}", environment, baseUrl);
        
        String sessionId = UUID.randomUUID().toString();
        return testAgent.runComprehensiveTests(baseUrl, environment, sessionId);
    }
    
    /**
     * Test a specific feature across all environments.
     * 
     * @param feature Feature name
     * @param environments Map of environment name to base URL
     * @return Feature test report
     */
    public String testFeatureAcrossEnvironments(String feature, Map<String, String> environments) {
        log.info("Testing feature: {} across environments: {}", feature, environments.keySet());
        
        String sessionId = UUID.randomUUID().toString();
        Map<String, String> results = new HashMap<>();
        
        for (Map.Entry<String, String> env : environments.entrySet()) {
            String envName = env.getKey();
            String baseUrl = env.getValue();
            
            try {
                String result = testAgent.testFeature(feature, baseUrl, sessionId);
                results.put(envName, result);
            } catch (Exception e) {
                log.error("Error testing feature {} in environment: {}", feature, envName, e);
                results.put(envName, "ERROR: " + e.getMessage());
            }
        }
        
        StringBuilder finalReport = new StringBuilder();
        finalReport.append("=== FEATURE TEST REPORT: ").append(feature).append(" ===\n\n");
        
        for (Map.Entry<String, String> entry : results.entrySet()) {
            finalReport.append("--- ").append(entry.getKey().toUpperCase()).append(" ENVIRONMENT ---\n");
            finalReport.append(entry.getValue()).append("\n\n");
        }
        
        return finalReport.toString();
    }
}

