package com.ai.company.registry;

import com.ai.company.agents.architect.ArchitectAgent;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.devops.DevOpsAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.fullstack.FullStackAgent;
import com.ai.company.agents.productmanager.ProductManagerAgent;
import com.ai.company.agents.qa.QAAgent;
import com.ai.company.agents.scrummaster.ScrumMasterAgent;
import com.ai.company.api.dto.CtoDebugResponse;

import dev.langchain4j.model.chat.ChatLanguageModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized registry for all AI agents.
 */
public class AgentRegistry {

    private static final Logger log = LoggerFactory.getLogger(AgentRegistry.class);

    private static volatile AgentRegistry instance;

    private final ChatLanguageModel chatModel;
    private final Map<String, Object> agents = new ConcurrentHashMap<>();
    private final Map<String, AgentMetadata> metadata = new ConcurrentHashMap<>();

    /** ADDED FIELDS **/
    private boolean ctoAgentHasAuditTool = false;
    private String loadedApiKey = "";
    private final MemoryStore memoryStore = new MemoryStore(); // Simple in-memory message counter

    /**
     * Private constructor
     */
    private AgentRegistry(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;

        // Capture API key prefix if available
        try {
            String modelString = chatModel.toString();
            // not perfect but avoids leaking key
            this.loadedApiKey = extractPossibleKey(modelString);
        } catch (Exception ignore) {}

        initializeAgents();
        log.info("AgentRegistry initialized with {} agents", agents.size());
    }

    public static AgentRegistry getInstance(ChatLanguageModel chatModel) {
        if (instance == null) {
            synchronized (AgentRegistry.class) {
                if (instance == null) {
                    instance = new AgentRegistry(chatModel);
                }
            }
        }
        return instance;
    }

    public static AgentRegistry getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AgentRegistry not initialized. Call getInstance(ChatLanguageModel) first.");
        }
        return instance;
    }

    /**
     * Initializes all agents.
     */
    private void initializeAgents() {

        // CTO Agent (system audit tool added later)
        CTOAgent ctoAgent = new CTOAgent(chatModel);
        agents.put("cto_agent", ctoAgent);
        agents.put("supervisor_agent", ctoAgent);

        metadata.put("cto_agent", createCTOMetadata());
        metadata.put("supervisor_agent", createCTOMetadata());

        // Architect
        ArchitectAgent architectAgent = new ArchitectAgent(chatModel);
        agents.put("architect_agent", architectAgent);
        metadata.put("architect_agent", createArchitectMetadata());

        // Engineer
        EngineerAgent engineerAgent = new EngineerAgent(chatModel);
        agents.put("engineer_agent", engineerAgent);
        metadata.put("engineer_agent", createEngineerMetadata());

        // DevOps
        DevOpsAgent devopsAgent = new DevOpsAgent(chatModel);
        agents.put("devops_agent", devopsAgent);
        metadata.put("devops_agent", createDevOpsMetadata());

        // FullStack
        FullStackAgent fullstackAgent = new FullStackAgent(chatModel);
        agents.put("fullstack_agent", fullstackAgent);
        metadata.put("fullstack_agent", createFullStackMetadata());

        // Product Manager
        ProductManagerAgent productManagerAgent = new ProductManagerAgent(chatModel);
        agents.put("product_manager_agent", productManagerAgent);
        metadata.put("product_manager_agent", createProductManagerMetadata());

        // QA
        QAAgent qaAgent = new QAAgent(chatModel);
        agents.put("qa_agent", qaAgent);
        metadata.put("qa_agent", createQAMetadata());

        // Scrum Master
        ScrumMasterAgent scrumMasterAgent = new ScrumMasterAgent(chatModel);
        agents.put("scrum_master_agent", scrumMasterAgent);
        metadata.put("scrum_master_agent", createScrumMasterMetadata());
    }

    // ---------------- GETTERS ----------------

    public CTOAgent getCTOAgent() {
        return (CTOAgent) agents.get("cto_agent");
    }

    /**
     * Inject SystemAuditTool (via Spring)
     */
    public void updateCTOAgentWithTool(com.ai.company.tools.audit.SystemAuditTool systemAuditTool) {
        if (systemAuditTool != null) {
            CTOAgent newCto = new CTOAgent(chatModel, systemAuditTool);
            this.ctoAgentHasAuditTool = true;

            agents.put("cto_agent", newCto);
            agents.put("supervisor_agent", newCto);

            log.info("CTO Agent updated with SystemAuditTool");
        }
    }

    public CTOAgent getSupervisorAgent() {
        return (CTOAgent) agents.get("supervisor_agent");
    }

    public ArchitectAgent getArchitectAgent() {
        return (ArchitectAgent) agents.get("architect_agent");
    }

    public EngineerAgent getEngineerAgent() {
        return (EngineerAgent) agents.get("engineer_agent");
    }

    public DevOpsAgent getDevOpsAgent() {
        return (DevOpsAgent) agents.get("devops_agent");
    }

    public FullStackAgent getFullStackAgent() {
        return (FullStackAgent) agents.get("fullstack_agent");
    }

    public ProductManagerAgent getProductManagerAgent() {
        return (ProductManagerAgent) agents.get("product_manager_agent");
    }

    public QAAgent getQAAgent() {
        return (QAAgent) agents.get("qa_agent");
    }

    public ScrumMasterAgent getScrumMasterAgent() {
        return (ScrumMasterAgent) agents.get("scrum_master_agent");
    }

    public Object getAgent(String name) {
        return agents.get(name.toLowerCase());
    }

    public Map<String, AgentMetadata> getAllMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public Set<String> getAgentNames() {
        return Collections.unmodifiableSet(agents.keySet());
    }
    /**
     * Returns metadata for a specific agent
     */
    public AgentMetadata getMetadata(String agentName) {
        return metadata.get(agentName);
    }

    // ---------------- METADATA BUILDERS ----------------
    private AgentMetadata createCTOMetadata() {
        return new AgentMetadata(
                "cto_agent",
                "Chief Technology Officer",
                "Supervises technical agents, approves architecture, creates docs",
                Set.of("architecture","approval","diagrams","planning"),
                Set.of("review","approve","plan","document"),
                true,
                "com.ai.company.agents.cto.CTOAgent"
        );
    }

    private AgentMetadata createArchitectMetadata() {
        return new AgentMetadata(
                "architect_agent",
                "AI Architect",
                "Designs system architecture",
                Set.of("architecture","design"),
                Set.of("design_architecture"),
                true,
                "com.ai.company.agents.architect.ArchitectAgent"
        );
    }

    private AgentMetadata createEngineerMetadata() {
        return new AgentMetadata(
                "engineer_agent",
                "AI Engineer",
                "Implementation specs",
                Set.of("backend","implementation"),
                Set.of("generate_specs"),
                true,
                "com.ai.company.agents.engineer.EngineerAgent"
        );
    }

    private AgentMetadata createDevOpsMetadata() {
        return new AgentMetadata(
                "devops_agent",
                "DevOps Specialist",
                "CI/CD & infra",
                Set.of("cicd","docker","infra"),
                Set.of("analyze_infra"),
                true,
                "com.ai.company.agents.devops.DevOpsAgent"
        );
    }

    private AgentMetadata createFullStackMetadata() {
        return new AgentMetadata(
                "fullstack_agent",
                "Full Stack Developer",
                "Frontend/Backend integration",
                Set.of("frontend","integration"),
                Set.of("suggest_components"),
                true,
                "com.ai.company.agents.fullstack.FullStackAgent"
        );
    }

    private AgentMetadata createProductManagerMetadata() {
        return new AgentMetadata(
                "product_manager_agent",
                "Product Manager",
                "Roadmaps, user stories",
                Set.of("epics","stories"),
                Set.of("create_epics"),
                true,
                "com.ai.company.agents.productmanager.ProductManagerAgent"
        );
    }

    private AgentMetadata createQAMetadata() {
        return new AgentMetadata(
                "qa_agent",
                "QA Specialist",
                "Test plans, Postman flows",
                Set.of("testing"),
                Set.of("create_test_plan"),
                true,
                "com.ai.company.agents.qa.QAAgent"
        );
    }

    private AgentMetadata createScrumMasterMetadata() {
        return new AgentMetadata(
                "scrum_master_agent",
                "Scrum Master",
                "Sprint planning, backlog management, standups, velocity",
                Set.of("scrum","agile","sprint","backlog"),
                Set.of("create_sprint_plan","break_down_epic","daily_standup"),
                true,
                "com.ai.company.agents.scrummaster.ScrumMasterAgent"
        );
    }

    // ---------------- DEBUGGER METHOD ----------------

    public CtoDebugResponse getCtoDebugInfo() {
        CtoDebugResponse dto = new CtoDebugResponse();

        dto.setReady(getCTOAgent() != null);
        dto.setModel(chatModel.getClass().getSimpleName());

        dto.setConstructorUsed(
                ctoAgentHasAuditTool ? "with_system_audit_tool" : "default_constructor"
        );

        dto.setTools(
                List.of(
                        "GitHubReaderTool",
                        "FileSystemReaderTool",
                        "ProjectSummaryTool",
                        "DockerStatsTool",
                        "LogReaderTool",
                        ctoAgentHasAuditTool ? "SystemAuditTool" : "none"
                )
        );

        dto.setMemory(new CtoDebugResponse.MemoryInfo(
                50,
                memoryStore.getTotalMessagesForAgent("cto_agent")
        ));

        dto.setApiKeyPrefix(maskApiKey(loadedApiKey));
        dto.setTimestamp(Instant.now());

        return dto;
    }

    // ---------------- UTILITIES ----------------

    private String extractPossibleKey(String raw) {
        if (raw == null) return "";
        if (raw.contains("sk-")) {
            int i = raw.indexOf("sk-");
            return raw.substring(i, Math.min(i + 10, raw.length()));
        }
        return "";
    }

    private String maskApiKey(String key) {
        if (key == null || key.isEmpty()) return "not_set";
        if (key.length() <= 6) return "***";
        return key.substring(0, 6) + "******";
    }

    /**
     * Simple in-memory tracking of message counts per agent
     */
    private static class MemoryStore {
        private final Map<String,Integer> store = new HashMap<>();

        public int getTotalMessagesForAgent(String agent) {
            return store.getOrDefault(agent, 0);
        }

        public void increment(String agent) {
            store.put(agent, getTotalMessagesForAgent(agent) + 1);
        }
    }
}
