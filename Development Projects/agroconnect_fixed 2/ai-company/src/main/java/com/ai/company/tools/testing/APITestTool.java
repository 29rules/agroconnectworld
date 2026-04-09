package com.ai.company.tools.testing;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * API Test Tool
 * 
 * Tests REST API endpoints, authentication, and data validation.
 * Read-only operations for testing.
 */
public class APITestTool {
    
    private static final Logger log = LoggerFactory.getLogger(APITestTool.class);
    
    /**
     * Test an API endpoint.
     */
    @Tool("Test an API endpoint. Provide URL, method (GET/POST/PUT/DELETE), optional body (JSON), and optional auth token. Returns response status, body, and test result. Read-only.")
    public String testAPIEndpoint(String endpointUrl, String method, String requestBody, String authToken) {
        log.info("Testing API endpoint: {} {} with token: {}", method, endpointUrl, authToken != null ? "provided" : "none");
        
        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method != null ? method : "GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "AI-Company-API-Test/1.0");
            
            if (authToken != null && !authToken.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + authToken);
            }
            
            // Send request body for POST/PUT
            if ((method.equals("POST") || method.equals("PUT")) && requestBody != null && !requestBody.isEmpty()) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(requestBody.getBytes(StandardCharsets.UTF_8));
                }
            }
            
            int statusCode = connection.getResponseCode();
            
            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    statusCode >= 200 && statusCode < 300 
                        ? connection.getInputStream() 
                        : connection.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line).append("\n");
                }
            }
            
            StringBuilder result = new StringBuilder();
            result.append("=== API Endpoint Test ===\n");
            result.append("URL: ").append(endpointUrl).append("\n");
            result.append("Method: ").append(method).append("\n");
            result.append("Status Code: ").append(statusCode).append("\n");
            result.append("Response Body: ").append(responseBody.toString().trim()).append("\n");
            
            if (statusCode >= 200 && statusCode < 300) {
                result.append("Result: PASS - API call successful\n");
            } else if (statusCode == 401) {
                result.append("Result: FAIL - Authentication required\n");
            } else if (statusCode == 403) {
                result.append("Result: FAIL - Authorization failed\n");
            } else if (statusCode == 404) {
                result.append("Result: FAIL - Endpoint not found\n");
            } else {
                result.append("Result: FAIL - API call failed\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("Error testing API endpoint: {}", endpointUrl, e);
            return "ERROR: API test failed - " + e.getMessage();
        }
    }
    
    /**
     * Test API authentication.
     */
    @Tool("Test API authentication by calling a protected endpoint with and without token. Returns auth test results. Read-only.")
    public String testAPIAuthentication(String protectedEndpoint, String authToken) {
        log.info("Testing API authentication for: {}", protectedEndpoint);
        
        StringBuilder result = new StringBuilder();
        result.append("=== API Authentication Test ===\n");
        result.append("Endpoint: ").append(protectedEndpoint).append("\n\n");
        
        // Test without token (should fail)
        result.append("Test 1: Without authentication token\n");
        String noAuthResult = testAPIEndpoint(protectedEndpoint, "GET", null, null);
        result.append(noAuthResult).append("\n");
        
        // Test with token (should pass)
        if (authToken != null && !authToken.isEmpty()) {
            result.append("Test 2: With authentication token\n");
            String withAuthResult = testAPIEndpoint(protectedEndpoint, "GET", null, authToken);
            result.append(withAuthResult).append("\n");
        } else {
            result.append("Test 2: SKIPPED - No auth token provided\n");
        }
        
        return result.toString();
    }
}



