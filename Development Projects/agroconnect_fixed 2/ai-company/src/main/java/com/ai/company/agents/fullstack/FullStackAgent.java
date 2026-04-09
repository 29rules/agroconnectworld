package com.ai.company.agents.fullstack;

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
 * Full-Stack Developer Agent
 * 
 * Responsibilities:
 * - Suggests React component implementations
 * - Designs API request flows
 * - Creates frontend-backend integration specs
 * - Proposes state management patterns
 * - Specifies data fetching strategies
 * 
 * ZERO-IMPACT MODE: Only produces implementation suggestions, never modifies code.
 */
public class FullStackAgent {
    
    private static final Logger log = LoggerFactory.getLogger(FullStackAgent.class);
    
    private final FullStackAgentService agentService;
    private final GitHubReaderTool githubTool;
    private final FileSystemReaderTool fileSystemTool;
    
    public FullStackAgent(ChatLanguageModel chatModel) {
        this.githubTool = new GitHubReaderTool();
        this.fileSystemTool = new FileSystemReaderTool();
        
        this.agentService = AiServices.builder(FullStackAgentService.class)
            .chatLanguageModel(chatModel)
            .tools(githubTool, fileSystemTool)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    public String suggestComponent(String requirement, String sessionId) {
        log.info("Full-Stack Agent suggesting React component");
        return agentService.suggestComponent(requirement, sessionId);
    }
    
    public String designApiFlow(String endpoint, String sessionId) {
        log.info("Full-Stack Agent designing API flow");
        return agentService.designApiFlow(endpoint, sessionId);
    }
    
    public String createIntegrationSpec(String frontend, String backend, String sessionId) {
        log.info("Full-Stack Agent creating integration spec");
        return agentService.createIntegrationSpec(frontend, backend, sessionId);
    }
    
    interface FullStackAgentService {
        
        @SystemMessage("""
            You are the Full-Stack Developer Agent for AgroConnectWorld.
            
            Your role:
            - Suggest React component implementations
            - Design API request flows
            - Create frontend-backend integration specifications
            - Propose state management patterns (React Context)
            - Specify data fetching strategies
            - Design UI/UX implementation approaches
            
            ZERO-IMPACT MODE:
            - MUST NOT modify existing React components
            - MUST NOT change existing API integrations
            - MUST NOT commit code changes
            - Can only produce implementation suggestions
            
            Existing Patterns:
            - React with Vite
            - Bootstrap for UI components
            - React Router for routing
            - React Context for state management
            - RESTful API integration
            - Axios/Fetch for HTTP requests
            
            Output Format: JSON
            {
              "agent": "fullstack_agent",
              "implementation_suggestion": {
                "component_name": "Component name",
                "component_structure": {
                  "file_location": "src/components/...",
                  "props_interface": "TypeScript interface",
                  "state_management": "Context/State approach",
                  "lifecycle_hooks": "Required hooks"
                },
                "api_integration": {
                  "endpoints": ["List of API endpoints"],
                  "request_flow": "Request flow description",
                  "error_handling": "Error handling approach",
                  "loading_states": "Loading state management"
                },
                "ui_specification": {
                  "bootstrap_components": ["Bootstrap components used"],
                  "styling_approach": "CSS/styling strategy"
                }
              },
              "integration_specs": {
                "data_flow": "Data flow description",
                "state_sync": "State synchronization approach"
              }
            }
            """)
        String suggestComponent(@UserMessage("Suggest React component implementation for: {{requirement}}") 
                               String requirement, @MemoryId String sessionId);
        
        String designApiFlow(@UserMessage("Design API request flow for endpoint: {{endpoint}}") 
                            String endpoint, @MemoryId String sessionId);
        
        String createIntegrationSpec(@UserMessage("Create integration spec for frontend {{frontend}} and backend {{backend}}") 
                                     String frontend, String backend, @MemoryId String sessionId);
    }
}



