package com.ai.company.engineering;

import java.util.List;

public class UserStory {
    private String storyId;
    private String title;
    private String description;
    private List<String> acceptanceCriteria;
    private int storyPoints;
    private String priority;
    private List<TechnicalTask> technicalTasks;
    
    // Getters and Setters
    public String getStoryId() { return storyId; }
    public void setStoryId(String storyId) { this.storyId = storyId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getAcceptanceCriteria() { return acceptanceCriteria; }
    public void setAcceptanceCriteria(List<String> acceptanceCriteria) { this.acceptanceCriteria = acceptanceCriteria; }
    
    public int getStoryPoints() { return storyPoints; }
    public void setStoryPoints(int storyPoints) { this.storyPoints = storyPoints; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public List<TechnicalTask> getTechnicalTasks() { return technicalTasks; }
    public void setTechnicalTasks(List<TechnicalTask> technicalTasks) { this.technicalTasks = technicalTasks; }
}



