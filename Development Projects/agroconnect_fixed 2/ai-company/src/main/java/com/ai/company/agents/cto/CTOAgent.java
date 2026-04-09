package com.ai.company.agents.cto;

import com.ai.company.tools.audit.SystemAuditTool;
import com.ai.company.tools.docker.DockerStatsTool;
import com.ai.company.tools.filesystem.FileSystemReaderTool;
import com.ai.company.tools.filesystem.ProjectSummaryTool;
import com.ai.company.tools.github.GitHubReaderTool;
import com.ai.company.tools.logs.LogReaderTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CTO Agent - Chief Technology Officer
 */
public class CTOAgent {

    private static final Logger log = LoggerFactory.getLogger(CTOAgent.class);

    private final CTOAgentService agentService;
    private final GitHubReaderTool githubTool;
    private final FileSystemReaderTool fileSystemTool;
    private final ProjectSummaryTool projectSummaryTool;
    private final DockerStatsTool dockerTool;
    private final LogReaderTool logTool;
    private final SystemAuditTool systemAuditTool;

    /** NEW FIELDS */
    private final boolean ctoAgentHasAuditTool;
    private final String loadedApiKey;

    /**
     * Default constructor (no SystemAuditTool)
     */
    public CTOAgent(ChatLanguageModel chatModel) {
        this.githubTool = new GitHubReaderTool();
        this.fileSystemTool = new FileSystemReaderTool();
        this.projectSummaryTool = new ProjectSummaryTool();
        this.dockerTool = new DockerStatsTool();
        this.logTool = new LogReaderTool();
        this.systemAuditTool = null;

        /** NEW */
        this.ctoAgentHasAuditTool = false;
        this.loadedApiKey = extractApiKeyPrefix(chatModel);

        this.agentService = AiServices.builder(CTOAgentService.class)
                .chatLanguageModel(chatModel)
                .tools(projectSummaryTool, githubTool, fileSystemTool, dockerTool, logTool)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
                .build();
    }

    /**
     * Constructor WITH SystemAuditTool
     */
    public CTOAgent(ChatLanguageModel chatModel, SystemAuditTool systemAuditTool) {
        this.githubTool = new GitHubReaderTool();
        this.fileSystemTool = new FileSystemReaderTool();
        this.projectSummaryTool = new ProjectSummaryTool();
        this.dockerTool = new DockerStatsTool();
        this.logTool = new LogReaderTool();
        this.systemAuditTool = systemAuditTool;

        /** NEW */
        this.ctoAgentHasAuditTool = (systemAuditTool != null);
        this.loadedApiKey = extractApiKeyPrefix(chatModel);

        this.agentService = AiServices.builder(CTOAgentService.class)
                .chatLanguageModel(chatModel)
                .tools(projectSummaryTool, githubTool, fileSystemTool, dockerTool, logTool, systemAuditTool)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
                .build();
    }

    /**
     * Extract masked API key prefix (safe)
     */
    private String extractApiKeyPrefix(ChatLanguageModel model) {
        try {
            String raw = model.toString();
            if (raw.contains("sk-")) {
                int idx = raw.indexOf("sk-");
                return raw.substring(idx, Math.min(idx + 10, raw.length()));
            }
        } catch (Exception ignore) {}
        return "not_set";
    }

    // ---- Existing agent methods ----

    public String reviewArchitecture(String architectureProposal, String sessionId) {
        log.info("CTO Agent reviewing architecture proposal");
        return agentService.reviewArchitecture(architectureProposal, sessionId);
    }

    public String generateDocumentation(String topic, String sessionId) {
        log.info("CTO Agent generating documentation");
        return agentService.generateDocumentation(topic, sessionId);
    }

    public String createDiagram(String diagramType, String data, String sessionId) {
        log.info("CTO Agent creating diagram");
        return agentService.createDiagram(diagramType, data, sessionId);
    }

    public String assessRisk(String changeProposal, String sessionId) {
        log.info("CTO Agent assessing risk");
        return agentService.assessRisk(changeProposal, sessionId);
    }

    public String approveDecision(String decision, String sessionId) {
        log.info("CTO Agent approving decision");
        return agentService.approveDecision(decision, sessionId);
    }

    public String generateSprintPlan(String requirements, String sessionId) {
        log.info("CTO Agent generating sprint plan");
        return agentService.generateSprintPlan(requirements, sessionId);
    }

    // --- AI Service Interface ---
    interface CTOAgentService {
        @SystemMessage("""
            You are the Chief Technology Officer (CTO) of AgroConnectWorld's AI-driven development company.
            Your role is to ensure technical excellence, architectural integrity, and zero-impact operations.

            SYSTEM CONSTRAINTS:
            1. ZERO-IMPACT MODE: Never modify existing production code, configurations, or infrastructure.
            2. READ-ONLY ACCESS: All tools provide read-only access to the codebase.
            3. SUPERVISION: You supervise AI Architect, AI Engineer, DevOps, and Full-Stack Developer agents.
            4. APPROVAL AUTHORITY: All technical decisions require your approval before implementation.

            YOUR RESPONSIBILITIES:
            - Review and approve all architectural designs
            - Ensure compliance with existing system constraints
            - Generate comprehensive technical documentation
            - Create system architecture diagrams (Mermaid format)
            - Plan technical sprints and roadmaps
            - Assess risks of proposed changes
            - Maintain engineering standards
            - Analyze the AgroConnectWorld codebase when asked

            EXISTING SYSTEM (READ-ONLY):
            - Spring Boot 3.3.3 microservices (Java 21): auth, product, supplier, order, quote, contact, gateway
            - React 19 frontend with Bootstrap 5 and Vite
            - PostgreSQL with separate schemas per service
            - Docker Compose orchestration with Nginx reverse proxy
            - JWT authentication with role-based access control (ADMIN, BUYER, SUPPLIER, CEO)
            - AI Company layer with LangChain4j 0.29.1 agents

            OUTPUT FORMAT:
            Respond as a structured JSON object with this schema:
            {
              "agent": "cto_agent",
              "timestamp": "<ISO8601>",
              "decision_type": "approval|rejection|modification_request|analysis|documentation",
              "review_summary": "Brief summary",
              "technical_assessment": {
                "risk_level": "low|medium|high|critical",
                "impact_analysis": "Impact on existing systems",
                "compliance_check": "Zero-impact compliance check"
              },
              "recommendations": ["List of actionable recommendations"],
              "approved_artifacts": ["List of approved items"],
              "next_steps": ["Action items for other agents"]
            }

            STYLE:
            - Professional, technical, and precise
            - Clear risk assessments
            - Actionable recommendations
            - Use Mermaid syntax for diagrams
            - Always include compliance check against zero-impact mode
            """)
        String reviewArchitecture(@UserMessage String architectureProposal, @MemoryId String sessionId);

        String generateDocumentation(@UserMessage String topic, @MemoryId String sessionId);

        String createDiagram(@UserMessage("Create a {{diagramType}} diagram with the following data: {{data}}")
                             String diagramType, String data, @MemoryId String sessionId);

        String assessRisk(@UserMessage String changeProposal, @MemoryId String sessionId);

        String approveDecision(@UserMessage String decision, @MemoryId String sessionId);

        String generateSprintPlan(@UserMessage String requirements, @MemoryId String sessionId);
    }
}
