package com.ai.company.agents.architect;

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
 * AI Architect Agent
 * 
 * Responsibilities:
 * - Converts business requirements into technical architecture
 * - Designs scalable microservice patterns
 * - Generates API contract specifications (OpenAPI)
 * - Creates service interaction diagrams
 * - Proposes integration patterns
 * 
 * ZERO-IMPACT MODE: Only produces specifications, never modifies existing architecture.
 */
public class ArchitectAgent {
    
    private static final Logger log = LoggerFactory.getLogger(ArchitectAgent.class);
    
    private final ArchitectAgentService agentService;
    private final GitHubReaderTool githubTool;
    private final FileSystemReaderTool fileSystemTool;
    
    public ArchitectAgent(ChatLanguageModel chatModel) {
        this.githubTool = new GitHubReaderTool();
        this.fileSystemTool = new FileSystemReaderTool();
        
        this.agentService = AiServices.builder(ArchitectAgentService.class)
            .chatLanguageModel(chatModel)
            .tools(githubTool, fileSystemTool)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    public String designArchitecture(String requirements, String sessionId) {
        log.info("Architect Agent designing architecture");
        return agentService.designArchitecture(requirements, sessionId);
    }
    
    public String generateApiContract(String endpoint, String method, String sessionId) {
        log.info("Architect Agent generating API contract");
        return agentService.generateApiContract(endpoint, method, sessionId);
    }
    
    public String createServiceDiagram(String services, String sessionId) {
        log.info("Architect Agent creating service interaction diagram");
        return agentService.createServiceDiagram(services, sessionId);
    }
    
    interface ArchitectAgentService {
        
        @SystemMessage("""
            You are the AI Architect Agent for AgroConnectWorld.
            
            Your role:
            - Convert business requirements into technical architecture
            - Design scalable microservice patterns
            - Generate API contract specifications (OpenAPI 3.0 format)
            - Create service interaction diagrams (Mermaid)
            - Propose integration patterns
            - Ensure compatibility with existing Spring Boot microservices
            
            ZERO-IMPACT MODE:
            - MUST NOT modify existing microservices
            - MUST NOT change existing API contracts
            - MUST NOT modify database schemas
            - Can only propose new designs as extensions
            
            Existing Architecture:
            - Microservices: auth, product, supplier, order, quote, contact, gateway
            - Gateway: Spring Cloud Gateway
            - Database: PostgreSQL with separate schemas per service
            - Frontend: React with Bootstrap
            
            Output Format: JSON
            {
              "agent": "architect_agent",
              "architecture_proposal": {
                "feature_name": "Feature description",
                "design_approach": "How this extends existing architecture",
                "new_services": ["List if any"],
                "api_contracts": [{
                  "endpoint": "/api/...",
                  "method": "GET|POST|PUT|DELETE",
                  "request_schema": {},
                  "response_schema": {}
                }],
                "service_interactions": "Description",
                "data_flow": "Description"
              },
              "diagrams": {
                "architecture": "Mermaid diagram",
                "service_interaction": "Mermaid diagram"
              },
              "compatibility_check": {
                "existing_system_impact": "Analysis",
                "breaking_changes": false,
                "migration_required": false
              }
            }
            """)
        String designArchitecture(@UserMessage String requirements, @MemoryId String sessionId);
        
        String generateApiContract(@UserMessage("Generate OpenAPI 3.0 contract for endpoint {{endpoint}} with method {{method}}") 
                                  String endpoint, String method, @MemoryId String sessionId);
        
        String createServiceDiagram(@UserMessage("Create service interaction diagram for: {{services}}") 
                                   String services, @MemoryId String sessionId);
    }
}



