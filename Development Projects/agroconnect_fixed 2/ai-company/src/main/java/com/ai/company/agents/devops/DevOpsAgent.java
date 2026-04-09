package com.ai.company.agents.devops;

import com.ai.company.tools.docker.DockerStatsTool;
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
 * DevOps Agent
 * 
 * Responsibilities:
 * - Maps deployment architecture (read-only)
 * - Analyzes Docker Compose configurations
 * - Reviews Nginx setup
 * - Suggests CI/CD improvements
 * - Proposes monitoring solutions
 * - Designs scaling strategies
 * 
 * ZERO-IMPACT MODE: Only reads and analyzes, never modifies infrastructure.
 */
public class DevOpsAgent {
    
    private static final Logger log = LoggerFactory.getLogger(DevOpsAgent.class);
    
    private final DevOpsAgentService agentService;
    private final DockerStatsTool dockerTool;
    private final FileSystemReaderTool fileSystemTool;
    private final LogReaderTool logTool;
    
    public DevOpsAgent(ChatLanguageModel chatModel) {
        this.dockerTool = new DockerStatsTool();
        this.fileSystemTool = new FileSystemReaderTool();
        this.logTool = new LogReaderTool();
        
        this.agentService = AiServices.builder(DevOpsAgentService.class)
            .chatLanguageModel(chatModel)
            .tools(dockerTool, fileSystemTool, logTool)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    public String analyzeInfrastructure(String sessionId) {
        log.info("DevOps Agent analyzing infrastructure");
        return agentService.analyzeInfrastructure(sessionId);
    }
    
    public String suggestCICDImprovements(String currentSetup, String sessionId) {
        log.info("DevOps Agent suggesting CI/CD improvements");
        return agentService.suggestCICDImprovements(currentSetup, sessionId);
    }
    
    public String createDeploymentDiagram(String services, String sessionId) {
        log.info("DevOps Agent creating deployment diagram");
        return agentService.createDeploymentDiagram(services, sessionId);
    }
    
    interface DevOpsAgentService {
        
        @SystemMessage("""
            You are the DevOps Agent for AgroConnectWorld.
            
            Your role:
            - Map deployment architecture (read-only analysis)
            - Analyze Docker Compose configurations
            - Review Nginx reverse proxy setup
            - Suggest CI/CD pipeline improvements
            - Propose monitoring and alerting solutions
            - Design scaling strategies
            - Analyze infrastructure health
            
            ZERO-IMPACT MODE:
            - MUST NOT modify Docker Compose files
            - MUST NOT change Nginx configurations
            - MUST NOT deploy to production
            - MUST NOT modify server configurations
            - Can only read and analyze existing setup
            
            Existing Infrastructure:
            - Docker Compose for orchestration
            - Nginx as reverse proxy
            - PostgreSQL database
            - Redis cache
            - Spring Boot microservices
            - React frontend
            
            Output Format: JSON
            {
              "agent": "devops_agent",
              "infrastructure_analysis": {
                "current_architecture": "Architecture description",
                "docker_compose_analysis": {
                  "services": ["List of services"],
                  "networks": ["Network configuration"],
                  "health_checks": "Health check analysis"
                },
                "nginx_analysis": {
                  "routing_config": "Routing review",
                  "proxy_settings": "Proxy review"
                }
              },
              "recommendations": [{
                "category": "CI/CD|Monitoring|Scaling|Security",
                "recommendation": "Recommendation description",
                "impact": "low|medium|high",
                "effort": "low|medium|high"
              }],
              "diagrams": {
                "deployment": "Mermaid deployment diagram",
                "infrastructure": "Mermaid infrastructure diagram"
              }
            }
            """)
        String analyzeInfrastructure(@UserMessage("Analyze the current infrastructure setup") 
                                   @MemoryId String sessionId);
        
        String suggestCICDImprovements(@UserMessage("Suggest CI/CD improvements for: {{currentSetup}}") 
                                      String currentSetup, @MemoryId String sessionId);
        
        String createDeploymentDiagram(@UserMessage("Create deployment diagram for services: {{services}}") 
                                      String services, @MemoryId String sessionId);
    }
}



