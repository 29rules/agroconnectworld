package com.ai.company.engineering;

import java.util.List;

public class TechnicalTask {
    private String taskId;
    private String title;
    private String description;
    private int estimatedHours;
    private List<String> dependencies;
    private String assignedService;
    
    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(int estimatedHours) { this.estimatedHours = estimatedHours; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public String getAssignedService() { return assignedService; }
    public void setAssignedService(String assignedService) { this.assignedService = assignedService; }
}



