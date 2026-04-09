package com.ai.company.agents.qa;

import com.ai.company.tools.filesystem.FileSystemReaderTool;
import com.ai.company.tools.logs.LogReaderTool;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QA Agent
 * 
 * Responsibilities:
 * - Creates test plans for features
 * - Designs Postman test collections
 * - Specifies E2E testing flows
 * - Defines unit test requirements
 * - Ensures test coverage
 * 
 * ZERO-IMPACT MODE: Only produces test specifications, never modifies existing tests.
 */
public class QAAgent {
    
    private static final Logger log = LoggerFactory.getLogger(QAAgent.class);
    
    private final QAAgentService agentService;
    private final FileSystemReaderTool fileSystemTool;
    private final LogReaderTool logTool;
    
    public QAAgent(ChatLanguageModel chatModel) {
        this.fileSystemTool = new FileSystemReaderTool();
        this.logTool = new LogReaderTool();
        
        this.agentService = AiServices.builder(QAAgentService.class)
            .chatLanguageModel(chatModel)
            .tools(fileSystemTool, logTool)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    public String createTestPlan(String feature, String sessionId) {
        log.info("QA Agent creating test plan");
        return agentService.createTestPlan(feature, sessionId);
    }
    
    public String designPostmanCollection(String apiEndpoints, String sessionId) {
        log.info("QA Agent designing Postman collection");
        return agentService.designPostmanCollection(apiEndpoints, sessionId);
    }
    
    public String specifyE2EFlow(String userStory, String sessionId) {
        log.info("QA Agent specifying E2E flow");
        return agentService.specifyE2EFlow(userStory, sessionId);
    }
    
    interface QAAgentService {
        
        @SystemMessage("""
            You are the QA Agent for AgroConnectWorld.
            
            Your role:
            - Create comprehensive test plans for features
            - Design Postman test collections (JSON format)
            - Specify E2E testing flows
            - Define unit test requirements
            - Ensure test coverage for all acceptance criteria
            - Design test data strategies
            
            ZERO-IMPACT MODE:
            - MUST NOT modify existing tests
            - MUST NOT run tests against production
            - MUST NOT change test infrastructure
            - Can only produce test specifications
            
            Test Types:
            - Unit tests (JUnit for backend, Jest for frontend)
            - Integration tests (API endpoints)
            - E2E tests (Critical user flows)
            - Postman collections (API testing)
            
            Output Format: JSON
            {
              "agent": "qa_agent",
              "test_plan": {
                "feature_name": "Feature to test",
                "test_scenarios": [{
                  "scenario_id": "TS-001",
                  "description": "Test scenario description",
                  "test_steps": ["Step 1", "Step 2"],
                  "expected_result": "Expected outcome",
                  "priority": "high|medium|low"
                }],
                "postman_collection": {
                  "collection_name": "Collection name",
                  "requests": [{
                    "name": "Request name",
                    "method": "GET|POST|PUT|DELETE",
                    "url": "/api/...",
                    "tests": ["Test assertions"]
                  }]
                },
                "e2e_flows": [{
                  "flow_name": "E2E flow description",
                  "steps": ["Flow steps"],
                  "assertions": ["Assertions"]
                }]
              },
              "test_coverage": {
                "unit_tests": "Unit test requirements",
                "integration_tests": "Integration test requirements",
                "e2e_tests": "E2E test requirements"
              }
            }
            """)
        String createTestPlan(@UserMessage("Create test plan for feature: {{feature}}") 
                             String feature, @MemoryId String sessionId);
        
        String designPostmanCollection(@UserMessage("Design Postman collection for API endpoints: {{apiEndpoints}}") 
                                      String apiEndpoints, @MemoryId String sessionId);
        
        String specifyE2EFlow(@UserMessage("Specify E2E test flow for user story: {{userStory}}") 
                             String userStory, @MemoryId String sessionId);
    }
}



