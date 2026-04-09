package com.ai.company.testing;

import com.ai.company.tools.code.CodeReaderTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for Automated Test Generation
 */
@Service
public class TestGenerationService {
    
    private static final Logger log = LoggerFactory.getLogger(TestGenerationService.class);
    
    private final TestGenerationAgent testAgent;
    
    @Autowired
    public TestGenerationService(ChatLanguageModel chatModel) {
        this.testAgent = AiServices.builder(TestGenerationAgent.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    @Autowired
    private CodeReaderTool codeReader;
    
    private static final String TEST_OUTPUT_DIR = "ai-company/outputs/tests";
    
    /**
     * Generate API test cases
     */
    public List<APITestCase> generateAPITestCases() {
        log.info("Generating API test cases...");
        
        try {
            String apiSpecs = readAPISpecs();
            String testCasesJson = testAgent.generateAPITestCases(apiSpecs);
            
            List<APITestCase> testCases = parseAPITestCases(testCasesJson);
            saveAPITestCases(testCases);
            
            return testCases;
        } catch (Exception e) {
            log.error("Failed to generate API test cases", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Generate Postman collection
     */
    public String generatePostmanCollection() {
        log.info("Generating Postman collection...");
        
        try {
            String apiEndpoints = readAPIEndpoints();
            String collection = testAgent.generatePostmanCollection(apiEndpoints);
            
            savePostmanCollection(collection);
            
            return collection;
        } catch (Exception e) {
            log.error("Failed to generate Postman collection", e);
            return "";
        }
    }
    
    /**
     * Generate Selenium UI tests
     */
    public String generateSeleniumUITests() {
        log.info("Generating Selenium UI tests...");
        
        try {
            String uiSpecs = readUISpecs();
            String seleniumTests = testAgent.generateSeleniumUITests(uiSpecs);
            
            saveSeleniumTests(seleniumTests);
            
            return seleniumTests;
        } catch (Exception e) {
            log.error("Failed to generate Selenium tests", e);
            return "";
        }
    }
    
    private String readAPISpecs() {
        try {
            StringBuilder specs = new StringBuilder();
            
            // Read OpenAPI spec
            Path openapiPath = Paths.get("ai-company/api/openapi.yaml");
            if (Files.exists(openapiPath)) {
                specs.append("OpenAPI Spec:\n").append(Files.readString(openapiPath)).append("\n\n");
            }
            
            // Read gateway routes
            String gatewayConfig = codeReader.readFile("backend/gateway/src/main/java/com/agroconnectworld/gateway/GatewayConfig.java");
            specs.append("Gateway Routes:\n").append(gatewayConfig);
            
            return specs.toString();
        } catch (Exception e) {
            log.warn("Failed to read API specs", e);
            return "";
        }
    }
    
    private String readAPIEndpoints() {
        try {
            // Extract endpoints from controllers
            String[] services = {"auth-service", "product-service", "supplier-service", 
                               "quote-service", "order-service", "contact-service"};
            
            StringBuilder endpoints = new StringBuilder();
            for (String service : services) {
                try {
                    String controller = codeReader.readFolder("backend/" + service + "/src/main/java");
                    endpoints.append(service).append(":\n").append(controller).append("\n\n");
                } catch (Exception e) {
                    log.warn("Failed to read endpoints for {}", service, e);
                }
            }
            
            return endpoints.toString();
        } catch (Exception e) {
            log.warn("Failed to read API endpoints", e);
            return "";
        }
    }
    
    private String readUISpecs() {
        try {
            // Read frontend components and routes
            String frontendCode = codeReader.readFolder("frontend/src");
            String routes = codeReader.readFile("frontend/src/App.jsx");
            
            return "Frontend Components:\n" + frontendCode + "\n\nRoutes:\n" + routes;
        } catch (Exception e) {
            log.warn("Failed to read UI specs", e);
            return "";
        }
    }
    
    private List<APITestCase> parseAPITestCases(String json) {
        // Parse JSON and create APITestCase objects
        // Implementation similar to other parsers
        return new ArrayList<>();
    }
    
    private void saveAPITestCases(List<APITestCase> testCases) {
        try {
            Path testDir = Paths.get(TEST_OUTPUT_DIR, "api");
            Files.createDirectories(testDir);
            // Save test cases
        } catch (IOException e) {
            log.error("Failed to save API test cases", e);
        }
    }
    
    private void savePostmanCollection(String collection) {
        try {
            Path testDir = Paths.get(TEST_OUTPUT_DIR, "postman");
            Files.createDirectories(testDir);
            Path collectionPath = testDir.resolve("AgroConnectWorld.postman_collection.json");
            Files.writeString(collectionPath, collection);
            log.info("Postman collection saved to {}", collectionPath);
        } catch (IOException e) {
            log.error("Failed to save Postman collection", e);
        }
    }
    
    private void saveSeleniumTests(String tests) {
        try {
            Path testDir = Paths.get(TEST_OUTPUT_DIR, "selenium");
            Files.createDirectories(testDir);
            Path testPath = testDir.resolve("UITests.java");
            Files.writeString(testPath, tests);
            log.info("Selenium tests saved to {}", testPath);
        } catch (IOException e) {
            log.error("Failed to save Selenium tests", e);
        }
    }
}

