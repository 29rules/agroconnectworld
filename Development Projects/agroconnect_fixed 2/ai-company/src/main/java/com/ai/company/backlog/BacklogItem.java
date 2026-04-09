package com.ai.company.backlog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Backlog Item
 * 
 * Represents a single item in the product backlog.
 * 
 * Fields:
 * - id: Unique identifier
 * - title: Item title
 * - description: Detailed description
 * - priority: Priority level (HIGH, MEDIUM, LOW)
 * - status: Current status (TODO, IN_PROGRESS, DONE)
 * - storyPoints: Story point estimate
 * - agentOwner: Assigned agent name
 * - createdAt: Creation timestamp
 * - updatedAt: Last update timestamp
 * - tags: Optional tags for categorization
 * - dependencies: List of dependent item IDs
 * - acceptanceCriteria: List of acceptance criteria
 */
public class BacklogItem {
    
    private String id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private Integer storyPoints;
    private String agentOwner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;
    private List<String> dependencies;
    private List<String> acceptanceCriteria;
    
    /**
     * Creates a new backlog item.
     */
    public BacklogItem() {
        this.id = UUID.randomUUID().toString();
        this.status = Status.TODO;
        this.priority = Priority.MEDIUM;
        this.tags = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.acceptanceCriteria = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Creates a new backlog item with title and description.
     */
    public BacklogItem(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }
    
    /**
     * Updates the item and sets updatedAt timestamp.
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Adds a tag.
     */
    public void addTag(String tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
            touch();
        }
    }
    
    /**
     * Adds a dependency.
     */
    public void addDependency(String itemId) {
        if (!this.dependencies.contains(itemId)) {
            this.dependencies.add(itemId);
            touch();
        }
    }
    
    /**
     * Adds acceptance criteria.
     */
    public void addAcceptanceCriteria(String criterion) {
        if (!this.acceptanceCriteria.contains(criterion)) {
            this.acceptanceCriteria.add(criterion);
            touch();
        }
    }
    
    /**
     * Checks if item is ready for work (has acceptance criteria and estimate).
     */
    public boolean isReady() {
        return !acceptanceCriteria.isEmpty() && storyPoints != null && storyPoints > 0;
    }
    
    /**
     * Checks if item is blocked (has dependencies that are not DONE).
     */
    public boolean isBlocked(List<BacklogItem> allItems) {
        if (dependencies.isEmpty()) {
            return false;
        }
        
        for (String depId : dependencies) {
            BacklogItem dep = allItems.stream()
                .filter(item -> item.getId().equals(depId))
                .findFirst()
                .orElse(null);
            
            if (dep == null || dep.getStatus() != Status.DONE) {
                return true;
            }
        }
        
        return false;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title; 
        touch();
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description; 
        touch();
    }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { 
        this.priority = priority; 
        touch();
    }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { 
        this.status = status; 
        touch();
    }
    
    public Integer getStoryPoints() { return storyPoints; }
    public void setStoryPoints(Integer storyPoints) { 
        this.storyPoints = storyPoints; 
        touch();
    }
    
    public String getAgentOwner() { return agentOwner; }
    public void setAgentOwner(String agentOwner) { 
        this.agentOwner = agentOwner; 
        touch();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { 
        this.tags = tags; 
        touch();
    }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { 
        this.dependencies = dependencies; 
        touch();
    }
    
    public List<String> getAcceptanceCriteria() { return acceptanceCriteria; }
    public void setAcceptanceCriteria(List<String> acceptanceCriteria) { 
        this.acceptanceCriteria = acceptanceCriteria; 
        touch();
    }
    
    /**
     * Priority enumeration.
     */
    public enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }
    
    /**
     * Status enumeration.
     */
    public enum Status {
        TODO,
        IN_PROGRESS,
        DONE,
        BLOCKED,
        CANCELLED
    }
}



