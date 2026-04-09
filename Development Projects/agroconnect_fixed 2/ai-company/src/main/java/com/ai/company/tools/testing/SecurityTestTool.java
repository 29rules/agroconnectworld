package com.ai.company.tools.testing;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Security Test Tool
 * 
 * Tests security aspects: authentication, authorization, CORS, input validation.
 * Read-only operations for testing.
 */
public class SecurityTestTool {
    
    private static final Logger log = LoggerFactory.getLogger(SecurityTestTool.class);
    
    /**
     * Test CORS configuration.
     */
    @Tool("Test CORS configuration by checking CORS headers in response. Returns CORS test results. Read-only.")
    public String testCORS(String endpointUrl) {
        log.info("Testing CORS for: {}", endpointUrl);
        
        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("OPTIONS");
            connection.setRequestProperty("Origin", "http://localhost:5173");
            connection.setRequestProperty("Access-Control-Request-Method", "GET");
            connection.setRequestProperty("Access-Control-Request-Headers", "Content-Type");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int statusCode = connection.getResponseCode();
            
            String accessControlAllowOrigin = connection.getHeaderField("Access-Control-Allow-Origin");
            String accessControlAllowMethods = connection.getHeaderField("Access-Control-Allow-Methods");
            String accessControlAllowHeaders = connection.getHeaderField("Access-Control-Allow-Headers");
            String accessControlAllowCredentials = connection.getHeaderField("Access-Control-Allow-Credentials");
            
            StringBuilder result = new StringBuilder();
            result.append("=== CORS Security Test ===\n");
            result.append("Endpoint: ").append(endpointUrl).append("\n");
            result.append("Status Code: ").append(statusCode).append("\n");
            result.append("Access-Control-Allow-Origin: ").append(accessControlAllowOrigin != null ? accessControlAllowOrigin : "NOT SET").append("\n");
            result.append("Access-Control-Allow-Methods: ").append(accessControlAllowMethods != null ? accessControlAllowMethods : "NOT SET").append("\n");
            result.append("Access-Control-Allow-Headers: ").append(accessControlAllowHeaders != null ? accessControlAllowHeaders : "NOT SET").append("\n");
            result.append("Access-Control-Allow-Credentials: ").append(accessControlAllowCredentials != null ? accessControlAllowCredentials : "NOT SET").append("\n");
            
            // Security assessment
            List<String> issues = new ArrayList<>();
            if (accessControlAllowOrigin == null || accessControlAllowOrigin.isEmpty()) {
                issues.add("Missing Access-Control-Allow-Origin header");
            } else if ("*".equals(accessControlAllowOrigin) && "true".equals(accessControlAllowCredentials)) {
                issues.add("CORS misconfiguration: Cannot use '*' with credentials");
            }
            
            if (issues.isEmpty()) {
                result.append("Result: PASS - CORS configured correctly\n");
            } else {
                result.append("Result: FAIL - CORS issues found:\n");
                for (String issue : issues) {
                    result.append("  - ").append(issue).append("\n");
                }
            }
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("Error testing CORS: {}", endpointUrl, e);
            return "ERROR: CORS test failed - " + e.getMessage();
        }
    }
    
    /**
     * Test authentication security.
     */
    @Tool("Test authentication security by attempting unauthorized access. Returns security test results. Read-only.")
    public String testAuthenticationSecurity(String protectedEndpoint) {
        log.info("Testing authentication security for: {}", protectedEndpoint);
        
        StringBuilder result = new StringBuilder();
        result.append("=== Authentication Security Test ===\n");
        result.append("Endpoint: ").append(protectedEndpoint).append("\n\n");
        
        try {
            // Test 1: Access without token (should fail with 401)
            result.append("Test 1: Access without authentication token\n");
            URL url = new URL(protectedEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int statusCode = connection.getResponseCode();
            result.append("  Status Code: ").append(statusCode).append("\n");
            
            if (statusCode == 401) {
                result.append("  Result: PASS - Correctly requires authentication\n");
            } else {
                result.append("  Result: FAIL - Should return 401 Unauthorized\n");
            }
            result.append("\n");
            
            // Test 2: Access with invalid token (should fail with 401)
            result.append("Test 2: Access with invalid authentication token\n");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer invalid_token_12345");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            statusCode = connection.getResponseCode();
            result.append("  Status Code: ").append(statusCode).append("\n");
            
            if (statusCode == 401) {
                result.append("  Result: PASS - Correctly rejects invalid token\n");
            } else {
                result.append("  Result: FAIL - Should return 401 for invalid token\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("Error testing authentication security: {}", protectedEndpoint, e);
            return "ERROR: Authentication security test failed - " + e.getMessage();
        }
    }
}



