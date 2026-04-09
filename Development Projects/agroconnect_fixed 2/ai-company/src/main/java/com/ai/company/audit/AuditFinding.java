package com.ai.company.audit;

/**
 * Individual audit finding
 */
public class AuditFinding {
    
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW
    private String category;
    private String title;
    private String description;
    private String location; // File path or service name
    private String recommendation;
    
    public AuditFinding() {}
    
    public AuditFinding(String severity, String category, String title, String description, String location) {
        this.severity = severity;
        this.category = category;
        this.title = title;
        this.description = description;
        this.location = location;
    }
    
    // Getters and Setters
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}



