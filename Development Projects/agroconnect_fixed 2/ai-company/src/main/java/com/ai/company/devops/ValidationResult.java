package com.ai.company.devops;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private boolean valid;
    private List<String> issues = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();
    private int score; // 0-100
    
    public void addIssue(String issue) {
        this.issues.add(issue);
    }
    
    public void addRecommendation(String recommendation) {
        this.recommendations.add(recommendation);
    }
    
    // Getters and Setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public List<String> getIssues() { return issues; }
    public void setIssues(List<String> issues) { this.issues = issues; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}



