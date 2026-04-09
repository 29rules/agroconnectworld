package com.ai.company.testing;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
/**
 * AI Agent Service Interface for Automated Test Generation
 * Generates API test cases, Postman collections, and Selenium UI flows
 * 
 * This interface is used with AiServices.builder() to create the agent implementation
 */
public interface TestGenerationAgent {
    
    @SystemMessage("""
        You are a Senior QA Engineer specializing in automated test generation.
        Your role is to generate:
        1. API test cases (REST endpoints)
        2. Postman collections
        3. Selenium UI test flows
        
        Always provide well-structured, executable test code.
        """)
    @UserMessage("""
        Generate API test cases for the following endpoints:
        {{$apiSpecs}}
        
        Include:
        - Test cases for each endpoint
        - Positive test cases
        - Negative test cases
        - Edge cases
        - Authentication tests
        - Error handling tests
        
        Format as JSON with test structure.
        """)
    String generateAPITestCases(String apiSpecs);
    
    @UserMessage("""
        Generate a Postman collection for: {{$apiEndpoints}}
        
        Include:
        - All endpoints with proper HTTP methods
        - Request bodies
        - Headers (Authorization, Content-Type)
        - Environment variables
        - Test scripts
        - Pre-request scripts
        
        Format as Postman Collection v2.1 JSON.
        """)
    String generatePostmanCollection(String apiEndpoints);
    
    @UserMessage("""
        Generate Selenium UI test flows for: {{$uiSpecs}}
        
        Include:
        - Page object models
        - Test scenarios
        - Element locators
        - Assertions
        - Error handling
        
        Format as Java Selenium WebDriver code.
        """)
    String generateSeleniumUITests(String uiSpecs);
}

