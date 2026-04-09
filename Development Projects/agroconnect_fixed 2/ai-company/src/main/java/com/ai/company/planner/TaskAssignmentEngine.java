package com.ai.company.planner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Maps subtasks to appropriate agents based on task characteristics and agent capabilities.
 * 
 * The assignment engine:
 * - Analyzes task requirements
 * - Matches tasks to agent capabilities
 * - Considers agent workload
 * - Ensures balanced distribution
 * - Handles specialized tasks
 */
public class TaskAssignmentEngine {
    
    private static final Logger log = LoggerFactory.getLogger(TaskAssignmentEngine.class);
    
    // Agent capabilities mapping
    private static final Map<String, Set<String>> AGENT_CAPABILITIES = new HashMap<>();
    static {
        AGENT_CAPABILITIES.put("cto_agent", Set.of("architecture", "approval", "review", "planning", "supervision"));
        AGENT_CAPABILITIES.put("architect_agent", Set.of("architecture", "design", "api_contracts", "diagrams", "system_design"));
        AGENT_CAPABILITIES.put("engineer_agent", Set.of("implementation", "backend", "pseudo_code", "service_specs", "integration"));
        AGENT_CAPABILITIES.put("fullstack_agent", Set.of("frontend", "ui", "react", "api_integration", "component_design"));
        AGENT_CAPABILITIES.put("devops_agent", Set.of("infrastructure", "deployment", "docker", "ci_cd", "monitoring"));
        AGENT_CAPABILITIES.put("product_manager_agent", Set.of("epic", "user_story", "requirements", "prioritization", "roadmap"));
        AGENT_CAPABILITIES.put("qa_agent", Set.of("testing", "test_plan", "postman", "e2e", "quality_assurance"));
    }
    
    // Task type keywords for matching
    private static final Map<String, String> TASK_TYPE_KEYWORDS = new HashMap<>();
    static {
        TASK_TYPE_KEYWORDS.put("architecture", "architect_agent");
        TASK_TYPE_KEYWORDS.put("design", "architect_agent");
        TASK_TYPE_KEYWORDS.put("api", "architect_agent");
        TASK_TYPE_KEYWORDS.put("implementation", "engineer_agent");
        TASK_TYPE_KEYWORDS.put("backend", "engineer_agent");
        TASK_TYPE_KEYWORDS.put("service", "engineer_agent");
        TASK_TYPE_KEYWORDS.put("frontend", "fullstack_agent");
        TASK_TYPE_KEYWORDS.put("react", "fullstack_agent");
        TASK_TYPE_KEYWORDS.put("ui", "fullstack_agent");
        TASK_TYPE_KEYWORDS.put("component", "fullstack_agent");
        TASK_TYPE_KEYWORDS.put("infrastructure", "devops_agent");
        TASK_TYPE_KEYWORDS.put("docker", "devops_agent");
        TASK_TYPE_KEYWORDS.put("deployment", "devops_agent");
        TASK_TYPE_KEYWORDS.put("ci_cd", "devops_agent");
        TASK_TYPE_KEYWORDS.put("epic", "product_manager_agent");
        TASK_TYPE_KEYWORDS.put("user_story", "product_manager_agent");
        TASK_TYPE_KEYWORDS.put("requirements", "product_manager_agent");
        TASK_TYPE_KEYWORDS.put("testing", "qa_agent");
        TASK_TYPE_KEYWORDS.put("test", "qa_agent");
        TASK_TYPE_KEYWORDS.put("quality", "qa_agent");
        TASK_TYPE_KEYWORDS.put("approval", "cto_agent");
        TASK_TYPE_KEYWORDS.put("review", "cto_agent");
        TASK_TYPE_KEYWORDS.put("supervision", "cto_agent");
    }
    
    /**
     * Assigns an agent to a task based on task description and requirements.
     * 
     * @param taskDescription The task description
     * @param taskType Optional task type hint
     * @return The assigned agent name
     */
    public String assignAgent(String taskDescription, String taskType) {
        log.debug("Assigning agent for task: {}", taskDescription);
        
        String lowerDescription = taskDescription.toLowerCase();
        
        // First, try explicit task type
        if (taskType != null && !taskType.isEmpty()) {
            String agent = TASK_TYPE_KEYWORDS.get(taskType.toLowerCase());
            if (agent != null) {
                log.debug("Assigned agent based on task type: {} -> {}", taskType, agent);
                return agent;
            }
        }
        
        // Match based on keywords in description
        for (Map.Entry<String, String> entry : TASK_TYPE_KEYWORDS.entrySet()) {
            if (lowerDescription.contains(entry.getKey())) {
                log.debug("Assigned agent based on keyword: {} -> {}", entry.getKey(), entry.getValue());
                return entry.getValue();
            }
        }
        
        // Default to engineer agent for technical tasks
        if (containsTechnicalKeywords(lowerDescription)) {
            log.debug("Assigned default technical agent: engineer_agent");
            return "engineer_agent";
        }
        
        // Default to product manager for business tasks
        if (containsBusinessKeywords(lowerDescription)) {
            log.debug("Assigned default business agent: product_manager_agent");
            return "product_manager_agent";
        }
        
        // Fallback to engineer agent
        log.debug("Assigned fallback agent: engineer_agent");
        return "engineer_agent";
    }
    
    /**
     * Assigns agents to multiple tasks with workload balancing.
     * 
     * @param tasks List of task descriptions
     * @return Map of task index to assigned agent
     */
    public Map<Integer, String> assignAgents(List<String> tasks) {
        Map<Integer, String> assignments = new HashMap<>();
        Map<String, Integer> agentWorkload = new HashMap<>();
        
        // Initialize workload counters
        for (String agent : AGENT_CAPABILITIES.keySet()) {
            agentWorkload.put(agent, 0);
        }
        
        // Assign tasks
        for (int i = 0; i < tasks.size(); i++) {
            String task = tasks.get(i);
            String assignedAgent = assignAgent(task, null);
            
            // Consider workload for balancing
            String balancedAgent = balanceWorkload(assignedAgent, agentWorkload);
            assignments.put(i, balancedAgent);
            agentWorkload.put(balancedAgent, agentWorkload.get(balancedAgent) + 1);
        }
        
        log.info("Assigned {} tasks to agents", assignments.size());
        return assignments;
    }
    
    /**
     * Balances workload by considering current agent assignments.
     */
    private String balanceWorkload(String preferredAgent, Map<String, Integer> workload) {
        int preferredWorkload = workload.getOrDefault(preferredAgent, 0);
        int minWorkload = workload.values().stream().mapToInt(Integer::intValue).min().orElse(0);
        
        // If preferred agent is not overloaded, use it
        if (preferredWorkload <= minWorkload + 1) {
            return preferredAgent;
        }
        
        // Find agent with minimum workload that has similar capabilities
        return workload.entrySet().stream()
            .filter(e -> e.getValue() == minWorkload)
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(preferredAgent);
    }
    
    /**
     * Checks if description contains technical keywords.
     */
    private boolean containsTechnicalKeywords(String description) {
        String[] technicalKeywords = {
            "code", "implement", "develop", "build", "create", "write",
            "service", "api", "endpoint", "database", "schema", "class",
            "method", "function", "algorithm", "logic", "technical"
        };
        
        return Arrays.stream(technicalKeywords)
            .anyMatch(description::contains);
    }
    
    /**
     * Checks if description contains business keywords.
     */
    private boolean containsBusinessKeywords(String description) {
        String[] businessKeywords = {
            "feature", "requirement", "user", "stakeholder", "business",
            "product", "market", "customer", "value", "goal", "objective"
        };
        
        return Arrays.stream(businessKeywords)
            .anyMatch(description::contains);
    }
    
    /**
     * Gets available agents for a specific capability.
     * 
     * @param capability The required capability
     * @return List of agent names with the capability
     */
    public List<String> getAgentsForCapability(String capability) {
        return AGENT_CAPABILITIES.entrySet().stream()
            .filter(e -> e.getValue().contains(capability.toLowerCase()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all available agent names.
     * 
     * @return Set of agent names
     */
    public Set<String> getAvailableAgents() {
        return AGENT_CAPABILITIES.keySet();
    }
}

