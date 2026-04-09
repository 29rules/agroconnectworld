package com.ai.company.planner;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Multi-agent task planner that breaks down requests into subtasks using LLM.
 * 
 * The planner:
 * - Takes high-level requests
 * - Uses LLM to decompose into subtasks
 * - Creates task graph with dependencies
 * - Assigns tasks to appropriate agents
 * - Produces structured planning output
 * 
 * ZERO-IMPACT MODE: Only produces plans, never modifies code.
 */
public class TaskPlanner {
    
    private static final Logger log = LoggerFactory.getLogger(TaskPlanner.class);
    
    private final TaskPlannerService plannerService;
    private final TaskAssignmentEngine assignmentEngine;
    private final ObjectMapper objectMapper;
    
    /**
     * Creates a new task planner.
     * 
     * @param chatModel The chat language model to use
     * @param assignmentEngine The task assignment engine
     */
    public TaskPlanner(ChatLanguageModel chatModel, TaskAssignmentEngine assignmentEngine) {
        this.assignmentEngine = assignmentEngine;
        this.objectMapper = new ObjectMapper();
        // Configure to ignore unknown properties and be lenient with naming
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        
        this.plannerService = AiServices.builder(TaskPlannerService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(20))
            .build();
        
        log.info("Initialized TaskPlanner");
    }
    
    /**
     * Plans tasks for a given request.
     * 
     * @param request The high-level request
     * @param sessionId The session ID
     * @return Planning output with task graph and execution plan
     */
    public PlanningOutput plan(String request, String sessionId) {
        log.info("Planning tasks for request: {}", request);
        
        // Step 1: Use LLM to break down request into subtasks
        String subtasksJson = plannerService.breakDownIntoTasks(request, sessionId);
        log.debug("LLM generated subtasks: {}", subtasksJson);
        
        // Step 2: Parse subtasks from JSON
        List<SubtaskInfo> subtasks = parseSubtasks(subtasksJson);
        log.info("Parsed {} subtasks", subtasks.size());
        
        // Step 3: Create task graph
        TaskGraph taskGraph = createTaskGraph(subtasks);
        
        // Step 4: Validate graph (check for cycles)
        if (!taskGraph.isAcyclic()) {
            List<String> cycle = taskGraph.findCycle();
            log.error("Task graph contains cycle: {}", cycle);
            throw new IllegalStateException("Task graph contains cycle: " + cycle);
        }
        
        // Step 5: Get topological sort for execution order
        List<String> executionOrder = taskGraph.topologicalSort();
        log.info("Execution order determined: {} tasks", executionOrder.size());
        
        // Step 6: Build planning output
        PlanningOutput output = buildPlanningOutput(request, taskGraph, executionOrder, sessionId);
        
        log.info("Planning completed: {} tasks, {} agents", 
            output.getTasks().size(), 
            output.getAgentAssignments().size());
        
        return output;
    }
    
    /**
     * Parses subtasks from JSON response.
     */
    private List<SubtaskInfo> parseSubtasks(String json) {
        try {
            // Try to parse as JSON array
            SubtaskListResponse response = objectMapper.readValue(json, SubtaskListResponse.class);
            return response.getSubtasks();
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse JSON, attempting text extraction", e);
            // Fallback: extract from text
            return extractSubtasksFromText(json);
        }
    }
    
    /**
     * Extracts subtasks from text if JSON parsing fails.
     */
    private List<SubtaskInfo> extractSubtasksFromText(String text) {
        List<SubtaskInfo> subtasks = new ArrayList<>();
        
        // Simple extraction: look for numbered items or bullet points
        String[] lines = text.split("\n");
        int taskId = 1;
        
        for (String line : lines) {
            line = line.trim();
            if (line.matches("^\\d+[.)]\\s+.+") || line.matches("^[-*]\\s+.+")) {
                String description = line.replaceFirst("^\\d+[.)]\\s+", "")
                    .replaceFirst("^[-*]\\s+", "");
                
                if (!description.isEmpty()) {
                    SubtaskInfo subtask = new SubtaskInfo();
                    subtask.setId("task-" + taskId++);
                    subtask.setDescription(description);
                    subtask.setDependencies(new ArrayList<>());
                    subtasks.add(subtask);
                }
            }
        }
        
        return subtasks;
    }
    
    /**
     * Creates task graph from subtasks.
     */
    private TaskGraph createTaskGraph(List<SubtaskInfo> subtasks) {
        TaskGraph graph = new TaskGraph();
        
        // First pass: create all tasks
        Map<String, TaskNode> taskMap = new HashMap<>();
        for (SubtaskInfo subtask : subtasks) {
            String agent = assignmentEngine.assignAgent(subtask.getDescription(), subtask.getType());
            
            Set<String> dependencies = subtask.getDependencies() != null
                ? new HashSet<>(subtask.getDependencies())
                : new HashSet<>();
            
            TaskNode task = new TaskNode(
                subtask.getId(),
                subtask.getDescription(),
                agent,
                dependencies
            );
            
            if (subtask.getPriority() != null) {
                try {
                    task.setPriority(TaskNode.TaskPriority.valueOf(subtask.getPriority().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid priority: {}, using MEDIUM", subtask.getPriority());
                }
            }
            
            if (subtask.getEstimatedEffort() != null) {
                task.setEstimatedEffort(subtask.getEstimatedEffort());
            }
            
            taskMap.put(subtask.getId(), task);
        }
        
        // Second pass: add tasks to graph
        for (TaskNode task : taskMap.values()) {
            graph.addTask(task);
        }
        
        return graph;
    }
    
    /**
     * Builds planning output from task graph.
     */
    private PlanningOutput buildPlanningOutput(String request, TaskGraph taskGraph,
                                               List<String> executionOrder, String sessionId) {
        PlanningOutput output = new PlanningOutput();
        output.setPlanId(UUID.randomUUID().toString());
        output.setOriginalRequest(request);
        output.setExecutionOrder(executionOrder);
        
        // Build task info list
        List<PlanningOutput.TaskInfo> taskInfos = new ArrayList<>();
        Map<String, List<String>> dependencies = new HashMap<>();
        Map<String, List<String>> agentAssignments = new HashMap<>();
        
        for (TaskNode task : taskGraph.getAllTasks()) {
            PlanningOutput.TaskInfo taskInfo = new PlanningOutput.TaskInfo();
            taskInfo.setId(task.getId());
            taskInfo.setDescription(task.getDescription());
            taskInfo.setAssignedAgent(task.getAssignedAgent());
            taskInfo.setDependencies(new ArrayList<>(task.getDependencies()));
            taskInfo.setPriority(task.getPriority().name());
            taskInfo.setEstimatedEffort(task.getEstimatedEffort());
            taskInfo.setStatus(task.getStatus().name());
            taskInfos.add(taskInfo);
            
            dependencies.put(task.getId(), new ArrayList<>(task.getDependencies()));
            
            agentAssignments.computeIfAbsent(task.getAssignedAgent(), k -> new ArrayList<>())
                .add(task.getId());
        }
        
        output.setTasks(taskInfos);
        output.setDependencies(dependencies);
        output.setAgentAssignments(agentAssignments);
        
        // Calculate timeline
        PlanningOutput.TimelineInfo timeline = new PlanningOutput.TimelineInfo();
        int totalEffort = taskInfos.stream()
            .mapToInt(t -> t.getEstimatedEffort() != null ? t.getEstimatedEffort() : 1)
            .sum();
        timeline.setEstimatedTotalEffort(totalEffort);
        timeline.setCriticalPathLength(executionOrder.size());
        timeline.setParallelExecutionPossible(executionOrder.size() > 1);
        timeline.setEstimatedDurationHours(totalEffort); // Simple: 1 effort point = 1 hour
        output.setEstimatedTimeline(timeline);
        
        // Add metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("total_tasks", taskGraph.size());
        metadata.put("session_id", sessionId);
        metadata.put("planning_timestamp", output.getTimestamp().toString());
        output.setMetadata(metadata);
        
        return output;
    }
    
    /**
     * LangChain4j AI Service interface for task planning.
     */
    interface TaskPlannerService {
        
        @SystemMessage("""
            You are a multi-agent task planner for AgroConnectWorld AI Company.
            
            Your role:
            - Break down high-level requests into detailed subtasks
            - Identify task dependencies
            - Suggest task priorities
            - Estimate effort for each task
            - Ensure tasks are specific and actionable
            
            ZERO-IMPACT MODE:
            - You only produce plans, never modify code
            - All tasks are specifications only
            - No code changes are allowed
            
            Output Format: JSON
            {
              "subtasks": [
                {
                  "id": "task-1",
                  "description": "Clear task description",
                  "dependencies": ["task-0"],
                  "priority": "high|medium|low",
                  "estimated_effort": 5,
                  "type": "architecture|implementation|testing|etc"
                }
              ]
            }
            
            Guidelines:
            - Break down complex tasks into smaller, manageable subtasks
            - Identify clear dependencies between tasks
            - Assign appropriate priorities
            - Estimate effort in story points (1-13 scale)
            - Ensure tasks are specific and measurable
            """)
        String breakDownIntoTasks(@UserMessage("Break down this request into subtasks: {{request}}") 
                                  String request, @MemoryId String sessionId);
    }
    
    /**
     * Helper class for parsing subtask JSON.
     */
    private static class SubtaskListResponse {
        private List<SubtaskInfo> subtasks;
        
        public List<SubtaskInfo> getSubtasks() {
            return subtasks;
        }
        
        public void setSubtasks(List<SubtaskInfo> subtasks) {
            this.subtasks = subtasks;
        }
    }
    
    /**
     * Helper class for subtask information.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SubtaskInfo {
        private String id;
        private String description;
        private List<String> dependencies;
        private String priority;
        
        @JsonAlias({"estimated_effort", "estimatedEffort"})
        private Integer estimatedEffort;
        
        private String type;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public Integer getEstimatedEffort() { return estimatedEffort; }
        public void setEstimatedEffort(Integer estimatedEffort) { this.estimatedEffort = estimatedEffort; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}

