package com.ai.company.planner;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents the output of the task planning process.
 * 
 * This is a structured JSON output containing:
 * - Planning metadata
 * - Task graph structure
 * - Execution plan
 * - Agent assignments
 * - Estimated timeline
 */
public class PlanningOutput {
    
    @JsonProperty("plan_id")
    private String planId;
    
    @JsonProperty("timestamp")
    private Instant timestamp;
    
    @JsonProperty("original_request")
    private String originalRequest;
    
    @JsonProperty("tasks")
    private List<TaskInfo> tasks;
    
    @JsonProperty("execution_order")
    private List<String> executionOrder;
    
    @JsonProperty("agent_assignments")
    private Map<String, List<String>> agentAssignments;
    
    @JsonProperty("estimated_timeline")
    private TimelineInfo estimatedTimeline;
    
    @JsonProperty("dependencies")
    private Map<String, List<String>> dependencies;
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
    
    /**
     * Creates a new planning output.
     */
    public PlanningOutput() {
        this.timestamp = Instant.now();
    }
    
    // Getters and Setters
    public String getPlanId() {
        return planId;
    }
    
    public void setPlanId(String planId) {
        this.planId = planId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getOriginalRequest() {
        return originalRequest;
    }
    
    public void setOriginalRequest(String originalRequest) {
        this.originalRequest = originalRequest;
    }
    
    public List<TaskInfo> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<TaskInfo> tasks) {
        this.tasks = tasks;
    }
    
    public List<String> getExecutionOrder() {
        return executionOrder;
    }
    
    public void setExecutionOrder(List<String> executionOrder) {
        this.executionOrder = executionOrder;
    }
    
    public Map<String, List<String>> getAgentAssignments() {
        return agentAssignments;
    }
    
    public void setAgentAssignments(Map<String, List<String>> agentAssignments) {
        this.agentAssignments = agentAssignments;
    }
    
    public TimelineInfo getEstimatedTimeline() {
        return estimatedTimeline;
    }
    
    public void setEstimatedTimeline(TimelineInfo estimatedTimeline) {
        this.estimatedTimeline = estimatedTimeline;
    }
    
    public Map<String, List<String>> getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(Map<String, List<String>> dependencies) {
        this.dependencies = dependencies;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    /**
     * Task information for JSON output.
     */
    public static class TaskInfo {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("assigned_agent")
        private String assignedAgent;
        
        @JsonProperty("dependencies")
        private List<String> dependencies;
        
        @JsonProperty("priority")
        private String priority;
        
        @JsonProperty("estimated_effort")
        private Integer estimatedEffort;
        
        @JsonProperty("status")
        private String status;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getAssignedAgent() {
            return assignedAgent;
        }
        
        public void setAssignedAgent(String assignedAgent) {
            this.assignedAgent = assignedAgent;
        }
        
        public List<String> getDependencies() {
            return dependencies;
        }
        
        public void setDependencies(List<String> dependencies) {
            this.dependencies = dependencies;
        }
        
        public String getPriority() {
            return priority;
        }
        
        public void setPriority(String priority) {
            this.priority = priority;
        }
        
        public Integer getEstimatedEffort() {
            return estimatedEffort;
        }
        
        public void setEstimatedEffort(Integer estimatedEffort) {
            this.estimatedEffort = estimatedEffort;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
    
    /**
     * Timeline information for JSON output.
     */
    public static class TimelineInfo {
        @JsonProperty("estimated_total_effort")
        private Integer estimatedTotalEffort;
        
        @JsonProperty("critical_path_length")
        private Integer criticalPathLength;
        
        @JsonProperty("parallel_execution_possible")
        private Boolean parallelExecutionPossible;
        
        @JsonProperty("estimated_duration_hours")
        private Integer estimatedDurationHours;
        
        // Getters and Setters
        public Integer getEstimatedTotalEffort() {
            return estimatedTotalEffort;
        }
        
        public void setEstimatedTotalEffort(Integer estimatedTotalEffort) {
            this.estimatedTotalEffort = estimatedTotalEffort;
        }
        
        public Integer getCriticalPathLength() {
            return criticalPathLength;
        }
        
        public void setCriticalPathLength(Integer criticalPathLength) {
            this.criticalPathLength = criticalPathLength;
        }
        
        public Boolean getParallelExecutionPossible() {
            return parallelExecutionPossible;
        }
        
        public void setParallelExecutionPossible(Boolean parallelExecutionPossible) {
            this.parallelExecutionPossible = parallelExecutionPossible;
        }
        
        public Integer getEstimatedDurationHours() {
            return estimatedDurationHours;
        }
        
        public void setEstimatedDurationHours(Integer estimatedDurationHours) {
            this.estimatedDurationHours = estimatedDurationHours;
        }
    }
}



