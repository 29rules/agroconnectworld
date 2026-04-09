package com.ai.company.sprint;

import com.ai.company.backlog.BacklogItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sprint Plan
 * 
 * Represents a complete sprint plan with all committed work.
 * 
 * Fields:
 * - sprintId: Unique sprint identifier
 * - startDate: Sprint start date
 * - endDate: Sprint end date
 * - sprintGoal: Clear sprint goal
 * - committedStories: List of committed user stories
 * - storyPointTotal: Total story points committed
 * - velocityForecast: Predicted velocity for this sprint
 */
public class SprintPlan {
    
    private String sprintId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String sprintGoal;
    private List<CommittedStory> committedStories;
    private int storyPointTotal;
    private int velocityForecast;
    private List<String> risks;
    private List<String> dependencies;
    private List<String> blockers;
    private String capacitySummary;
    
    public SprintPlan() {
        this.sprintId = UUID.randomUUID().toString();
        this.committedStories = new ArrayList<>();
        this.risks = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.blockers = new ArrayList<>();
    }
    
    /**
     * Adds a committed story to the sprint.
     */
    public void addCommittedStory(CommittedStory story) {
        this.committedStories.add(story);
        this.storyPointTotal = committedStories.stream()
            .filter(s -> s.getStoryPoints() != null)
            .mapToInt(CommittedStory::getStoryPoints)
            .sum();
    }
    
    /**
     * Adds a risk.
     */
    public void addRisk(String risk) {
        this.risks.add(risk);
    }
    
    /**
     * Adds a dependency.
     */
    public void addDependency(String dependency) {
        this.dependencies.add(dependency);
    }
    
    /**
     * Adds a blocker.
     */
    public void addBlocker(String blocker) {
        this.blockers.add(blocker);
    }
    
    /**
     * Calculates sprint duration in days.
     */
    public int getDurationDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    /**
     * Checks if sprint is on track (committed points <= forecast).
     */
    public boolean isOnTrack() {
        return storyPointTotal <= velocityForecast;
    }
    
    // Getters and Setters
    public String getSprintId() { return sprintId; }
    public void setSprintId(String sprintId) { this.sprintId = sprintId; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public String getSprintGoal() { return sprintGoal; }
    public void setSprintGoal(String sprintGoal) { this.sprintGoal = sprintGoal; }
    
    public List<CommittedStory> getCommittedStories() { return committedStories; }
    public void setCommittedStories(List<CommittedStory> committedStories) { 
        this.committedStories = committedStories;
        this.storyPointTotal = committedStories.stream()
            .filter(s -> s.getStoryPoints() != null)
            .mapToInt(CommittedStory::getStoryPoints)
            .sum();
    }
    
    public int getStoryPointTotal() { return storyPointTotal; }
    public void setStoryPointTotal(int storyPointTotal) { this.storyPointTotal = storyPointTotal; }
    
    public int getVelocityForecast() { return velocityForecast; }
    public void setVelocityForecast(int velocityForecast) { this.velocityForecast = velocityForecast; }
    
    public List<String> getRisks() { return risks; }
    public void setRisks(List<String> risks) { this.risks = risks; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public List<String> getBlockers() { return blockers; }
    public void setBlockers(List<String> blockers) { this.blockers = blockers; }
    
    public String getCapacitySummary() { return capacitySummary; }
    public void setCapacitySummary(String capacitySummary) { this.capacitySummary = capacitySummary; }
    
    /**
     * Committed story in sprint.
     */
    public static class CommittedStory {
        private String storyId;
        private String title;
        private String description;
        private Integer storyPoints;
        private String assignedAgent;
        private List<String> tasks;
        private List<String> acceptanceCriteria;
        private BacklogItem.Priority priority;
        
        public CommittedStory() {
            this.tasks = new ArrayList<>();
            this.acceptanceCriteria = new ArrayList<>();
        }
        
        // Getters and Setters
        public String getStoryId() { return storyId; }
        public void setStoryId(String storyId) { this.storyId = storyId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getStoryPoints() { return storyPoints; }
        public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }
        public String getAssignedAgent() { return assignedAgent; }
        public void setAssignedAgent(String assignedAgent) { this.assignedAgent = assignedAgent; }
        public List<String> getTasks() { return tasks; }
        public void setTasks(List<String> tasks) { this.tasks = tasks; }
        public List<String> getAcceptanceCriteria() { return acceptanceCriteria; }
        public void setAcceptanceCriteria(List<String> acceptanceCriteria) { this.acceptanceCriteria = acceptanceCriteria; }
        public BacklogItem.Priority getPriority() { return priority; }
        public void setPriority(BacklogItem.Priority priority) { this.priority = priority; }
    }
}



