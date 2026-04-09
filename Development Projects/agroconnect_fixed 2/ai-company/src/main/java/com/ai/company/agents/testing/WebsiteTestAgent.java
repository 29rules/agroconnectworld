package com.ai.company.agents.testing;

import com.ai.company.tools.deployment.HealthCheckTool;
import com.ai.company.tools.testing.*;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Website Test Agent
 * 
 * Comprehensive testing agent that tests the AgroConnectWorld website from all angles:
 * - Functional Testing (UI, User Flows, Features)
 * - API Testing (REST endpoints, Authentication, Data validation)
 * - Performance Testing (Load, Response times, Resource usage)
 * - Security Testing (Authentication, Authorization, Input validation, CORS)
 * - UI/UX Testing (Responsiveness, Accessibility, Cross-browser)
 * - Integration Testing (Frontend-Backend, Database, External services)
 * - Regression Testing (Existing features, Breaking changes)
 * 
 * ZERO-IMPACT MODE: Only reads and tests, never modifies code or data.
 */
public class WebsiteTestAgent {
    
    private static final Logger log = LoggerFactory.getLogger(WebsiteTestAgent.class);
    
    private final WebsiteTestAgentService agentService;
    private final UITestTool uiTestTool;
    private final APITestTool apiTestTool;
    private final PerformanceTestTool performanceTestTool;
    private final SecurityTestTool securityTestTool;
    private final HealthCheckTool healthCheckTool;
    
    public WebsiteTestAgent(ChatLanguageModel chatModel) {
        this.uiTestTool = new UITestTool();
        this.apiTestTool = new APITestTool();
        this.performanceTestTool = new PerformanceTestTool();
        this.securityTestTool = new SecurityTestTool();
        this.healthCheckTool = new HealthCheckTool();
        
        this.agentService = AiServices.builder(WebsiteTestAgentService.class)
            .chatLanguageModel(chatModel)
            .tools(uiTestTool, apiTestTool, performanceTestTool, securityTestTool, healthCheckTool)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(100))
            .build();
    }
    
    /**
     * Run comprehensive website testing from all angles.
     * 
     * @param baseUrl Base URL of the website (e.g., http://localhost:8080)
     * @param environment Environment name (dev, uat, preprod, production)
     * @param sessionId Session ID for conversation context
     * @return Comprehensive test report
     */
    public String runComprehensiveTests(String baseUrl, String environment, String sessionId) {
        log.info("Running comprehensive website tests for: {} in environment: {}", baseUrl, environment);
        return agentService.runComprehensiveTests(baseUrl, environment, sessionId);
    }
    
    /**
     * Test specific feature or functionality.
     * 
     * @param feature Feature name to test
     * @param baseUrl Base URL
     * @param sessionId Session ID
     * @return Test results for the feature
     */
    public String testFeature(String feature, String baseUrl, String sessionId) {
        log.info("Testing feature: {} at {}", feature, baseUrl);
        return agentService.testFeature(feature, baseUrl, sessionId);
    }
    
    /**
     * Generate test report for all test results.
     * 
     * @param testResults Map of test category to results
     * @param sessionId Session ID
     * @return Formatted test report
     */
    public String generateTestReport(Map<String, String> testResults, String sessionId) {
        log.info("Generating comprehensive test report");
        // Format the results into a readable report
        StringBuilder report = new StringBuilder();
        report.append("=== COMPREHENSIVE TEST REPORT ===\n\n");
        
        for (Map.Entry<String, String> entry : testResults.entrySet()) {
            report.append("--- ").append(entry.getKey().toUpperCase()).append(" ---\n");
            report.append(entry.getValue()).append("\n\n");
        }
        
        return report.toString();
    }
    
    /**
     * AI Service Interface for Website Testing
     */
    @SystemMessage("""
        You are a comprehensive Website Test Agent for AgroConnectWorld, a B2B marketplace for food and agricultural products.
        
        Your responsibilities:
        1. FUNCTIONAL TESTING:
           - Test all user flows (buyer registration, supplier registration, product browsing, cart, checkout, orders, quotes)
           - Verify UI components render correctly
           - Test form validations and submissions
           - Test navigation and routing
           - Test authentication and authorization flows
           - Test role-based access (Buyer, Supplier, Admin)
        
        2. API TESTING:
           - Test all REST endpoints (/api/auth/*, /api/products/*, /api/quotes/*, /api/orders/*, /api/contact)
           - Verify request/response formats
           - Test authentication (JWT tokens)
           - Test error handling (400, 401, 403, 404, 500)
           - Test data validation
           - Test CORS configuration
        
        3. PERFORMANCE TESTING:
           - Measure page load times
           - Test API response times
           - Check resource usage (CPU, memory)
           - Test under load (multiple concurrent requests)
           - Identify bottlenecks
        
        4. SECURITY TESTING:
           - Test authentication mechanisms
           - Test authorization (role-based access)
           - Test input validation (SQL injection, XSS)
           - Test CORS configuration
           - Test JWT token security
           - Test sensitive data exposure
        
        5. UI/UX TESTING:
           - Test responsive design (mobile, tablet, desktop)
           - Test accessibility (WCAG compliance)
           - Test cross-browser compatibility
           - Test user experience flows
           - Test error messages and feedback
        
        6. INTEGRATION TESTING:
           - Test frontend-backend integration
           - Test database connectivity
           - Test external service integration (OpenRouter API for chatbot)
           - Test Docker container health
           - Test service dependencies
        
        7. REGRESSION TESTING:
           - Verify existing features still work
           - Test for breaking changes
           - Test backward compatibility
        
        TESTING APPROACH:
        - Use the provided tools (UITestTool, APITestTool, PerformanceTestTool, SecurityTestTool, HealthCheckTool)
        - Test systematically: start with health checks, then functional, then performance, then security
        - Document all findings with clear descriptions
        - Provide actionable recommendations
        - Prioritize critical issues
        
        OUTPUT FORMAT:
        - Structured JSON or markdown report
        - Include: test category, test name, status (PASS/FAIL/SKIP), details, recommendations
        - Group by test category
        - Include summary statistics
        
        ZERO-IMPACT MODE:
        - Only read and test, never modify code or data
        - Use read-only operations
        - Report findings without making changes
        """)
    interface WebsiteTestAgentService {
        
        @UserMessage("Run comprehensive website tests for {{baseUrl}} in {{environment}} environment. Test from all angles: functional, API, performance, security, UI/UX, integration, and regression.")
        String runComprehensiveTests(@MemoryId String sessionId, String baseUrl, String environment);
        
        @UserMessage("Test the following feature: {{feature}} at {{baseUrl}}. Include functional, API, and UI tests.")
        String testFeature(@MemoryId String sessionId, String feature, String baseUrl);
        
        @UserMessage("Generate a comprehensive test report summary. Analyze the test results and provide findings and recommendations.")
        String generateTestReport(@MemoryId String sessionId, String testResultsSummary);
    }
}

