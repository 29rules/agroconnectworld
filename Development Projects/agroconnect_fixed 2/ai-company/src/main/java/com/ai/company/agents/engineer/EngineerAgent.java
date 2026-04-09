package com.ai.company.agents.engineer;

import com.ai.company.tools.filesystem.FileSystemReaderTool;
import com.ai.company.tools.github.GitHubReaderTool;
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
 * AI Engineer Agent
 * 
 * Responsibilities:
 * - Converts architecture specs into implementation outlines
 * - Generates pseudo-code for complex logic
 * - Creates service layer specifications
 * - Designs integration patterns
 * - Specifies database interaction patterns
 * 
 * ZERO-IMPACT MODE: Only produces specifications and pseudo-code, never commits code.
 */
public class EngineerAgent {
    
    private static final Logger log = LoggerFactory.getLogger(EngineerAgent.class);
    
    private final EngineerAgentService agentService;
    private final GitHubReaderTool githubTool;
    private final FileSystemReaderTool fileSystemTool;
    
    public EngineerAgent(ChatLanguageModel chatModel) {
        this.githubTool = new GitHubReaderTool();
        this.fileSystemTool = new FileSystemReaderTool();
        
        this.agentService = AiServices.builder(EngineerAgentService.class)
            .chatLanguageModel(chatModel)
            .tools(githubTool, fileSystemTool)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    public String createImplementationSpec(String architecture, String sessionId) {
        log.info("Engineer Agent creating implementation specification");
        return agentService.createImplementationSpec(architecture, sessionId);
    }
    
    public String generatePseudoCode(String requirement, String sessionId) {
        log.info("Engineer Agent generating pseudo-code");
        return agentService.generatePseudoCode(requirement, sessionId);
    }
    
    public String designServiceLayer(String serviceName, String sessionId) {
        log.info("Engineer Agent designing service layer");
        return agentService.designServiceLayer(serviceName, sessionId);
    }
    
    interface EngineerAgentService {
        
        @SystemMessage("""
            You are the AI Engineer Agent for AgroConnectWorld.
            
            Your role:
            - Convert architecture designs into implementation specifications
            - Generate pseudo-code for complex logic
            - Create service layer specifications (Spring Boot patterns)
            - Design integration patterns
            - Specify database interaction patterns (JPA/Hibernate)
            - Ensure alignment with existing codebase patterns
            
            ZERO-IMPACT MODE:
            - MUST NOT commit code to repository
            - MUST NOT modify existing production code
            - MUST NOT run database migrations
            - Can only produce specifications and pseudo-code
            
            Existing Patterns:
            - Spring Boot 3.3.3 with Java 21
            - JPA/Hibernate for database access
            - RESTful APIs
            - Separate schemas per microservice
            - UUID primary keys
            - Instant for timestamps
            
            Output Format: JSON
            {
              "agent": "engineer_agent",
              "implementation_spec": {
                "feature_name": "Feature description",
                "service_specifications": [{
                  "service_name": "Service name",
                  "class_structure": "Class outline",
                  "methods": [{
                    "method_name": "Method name",
                    "pseudo_code": "Pseudo-code implementation",
                    "parameters": [],
                    "return_type": "Return type"
                  }],
                  "dependencies": ["List of dependencies"]
                }],
                "api_implementation": {
                  "controller_spec": "Controller outline",
                  "endpoint_details": "Endpoint implementation"
                },
                "database_interactions": {
                  "queries": ["Query patterns"],
                  "entity_changes": "Entity specs (if any)"
                }
              },
              "code_structure": {
                "directory_layout": "Directory structure",
                "file_organization": "File organization"
              }
            }
            """)
        String createImplementationSpec(@UserMessage String architecture, @MemoryId String sessionId);
        
        String generatePseudoCode(@UserMessage("Generate pseudo-code for: {{requirement}}") 
                                 String requirement, @MemoryId String sessionId);
        
        String designServiceLayer(@UserMessage("Design service layer for: {{serviceName}}") 
                                 String serviceName, @MemoryId String sessionId);
    }
}



