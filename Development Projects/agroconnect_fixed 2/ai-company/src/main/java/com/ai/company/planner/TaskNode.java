package com.ai.company.planner;

import java.util.*;

/**
 * Represents a single task node in the task graph.
 * 
 * Each task node contains:
 * - Unique identifier
 * - Task description
 * - Assigned agent name
 * - Dependencies (other task IDs that must complete first)
 * - Status (pending, in_progress, completed, failed)
 * - Priority level
 * - Estimated effort
 */
public class TaskNode {
    
    private final String id;
    private final String description;
    private String assignedAgent;
    private final Set<String> dependencies;
    private TaskStatus status;
    private TaskPriority priority;
    private Integer estimatedEffort; // Story points or hours
    
    /**
     * Creates a new task node.
     * 
     * @param id Unique task identifier
     * @param description Task description
     * @param assignedAgent Agent assigned to this task
     * @param dependencies Set of task IDs this task depends on
     */
    public TaskNode(String id, String description, String assignedAgent, Set<String> dependencies) {
        this.id = id;
        this.description = description;
        this.assignedAgent = assignedAgent;
        this.dependencies = dependencies != null ? new HashSet<>(dependencies) : new HashSet<>();
        this.status = TaskStatus.PENDING;
        this.priority = TaskPriority.MEDIUM;
        this.estimatedEffort = null;
    }
    
    /**
     * Creates a new task node without dependencies.
     */
    public TaskNode(String id, String description, String assignedAgent) {
        this(id, description, assignedAgent, null);
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getAssignedAgent() {
        return assignedAgent;
    }
    
    public Set<String> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public TaskPriority getPriority() {
        return priority;
    }
    
    public Integer getEstimatedEffort() {
        return estimatedEffort;
    }
    
    // Setters
    public void setAssignedAgent(String assignedAgent) {
        this.assignedAgent = assignedAgent;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }
    
    public void setEstimatedEffort(Integer estimatedEffort) {
        this.estimatedEffort = estimatedEffort;
    }
    
    /**
     * Adds a dependency to this task.
     * 
     * @param taskId The ID of the task this depends on
     */
    public void addDependency(String taskId) {
        dependencies.add(taskId);
    }
    
    /**
     * Checks if this task has all dependencies satisfied.
     * 
     * @param completedTasks Set of completed task IDs
     * @return true if all dependencies are completed
     */
    public boolean areDependenciesSatisfied(Set<String> completedTasks) {
        return completedTasks.containsAll(dependencies);
    }
    
    /**
     * Checks if this task is ready to start (all dependencies completed).
     * 
     * @param completedTasks Set of completed task IDs
     * @return true if task can start
     */
    public boolean isReady(Set<String> completedTasks) {
        return status == TaskStatus.PENDING && areDependenciesSatisfied(completedTasks);
    }
    
    @Override
    public String toString() {
        return String.format("TaskNode[id=%s, agent=%s, status=%s, dependencies=%d]",
            id, assignedAgent, status, dependencies.size());
    }
    
    /**
     * Task status enumeration.
     */
    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        BLOCKED
    }
    
    /**
     * Task priority enumeration.
     */
    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}



