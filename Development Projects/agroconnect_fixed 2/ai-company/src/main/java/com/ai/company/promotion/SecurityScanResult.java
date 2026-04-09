package com.ai.company.promotion;

import java.util.ArrayList;
import java.util.List;

public class SecurityScanResult {
    private String buildId;
    private int criticalVulnerabilities;
    private int highVulnerabilities;
    private int mediumVulnerabilities;
    private int lowVulnerabilities;
    private boolean passed;
    private int score; // 0-100
    private List<String> issues = new ArrayList<>();
    
    public void addIssue(String issue) {
        this.issues.add(issue);
    }
    
    // Getters and Setters
    public String getBuildId() { return buildId; }
    public void setBuildId(String buildId) { this.buildId = buildId; }
    
    public int getCriticalVulnerabilities() { return criticalVulnerabilities; }
    public void setCriticalVulnerabilities(int criticalVulnerabilities) { this.criticalVulnerabilities = criticalVulnerabilities; }
    
    public int getHighVulnerabilities() { return highVulnerabilities; }
    public void setHighVulnerabilities(int highVulnerabilities) { this.highVulnerabilities = highVulnerabilities; }
    
    public int getMediumVulnerabilities() { return mediumVulnerabilities; }
    public void setMediumVulnerabilities(int mediumVulnerabilities) { this.mediumVulnerabilities = mediumVulnerabilities; }
    
    public int getLowVulnerabilities() { return lowVulnerabilities; }
    public void setLowVulnerabilities(int lowVulnerabilities) { this.lowVulnerabilities = lowVulnerabilities; }
    
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public List<String> getIssues() { return issues; }
    public void setIssues(List<String> issues) { this.issues = issues; }
}



