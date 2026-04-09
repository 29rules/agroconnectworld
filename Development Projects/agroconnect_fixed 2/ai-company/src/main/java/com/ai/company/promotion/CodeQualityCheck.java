package com.ai.company.promotion;

import java.util.ArrayList;
import java.util.List;

public class CodeQualityCheck {
    private String buildId;
    private boolean lintingPassed;
    private boolean coveragePassed;
    private boolean staticAnalysisPassed;
    private boolean passed;
    private int score; // 0-100
    private List<String> issues = new ArrayList<>();
    
    public void addIssue(String issue) {
        this.issues.add(issue);
    }
    
    // Getters and Setters
    public String getBuildId() { return buildId; }
    public void setBuildId(String buildId) { this.buildId = buildId; }
    
    public boolean isLintingPassed() { return lintingPassed; }
    public void setLintingPassed(boolean lintingPassed) { this.lintingPassed = lintingPassed; }
    
    public boolean isCoveragePassed() { return coveragePassed; }
    public void setCoveragePassed(boolean coveragePassed) { this.coveragePassed = coveragePassed; }
    
    public boolean isStaticAnalysisPassed() { return staticAnalysisPassed; }
    public void setStaticAnalysisPassed(boolean staticAnalysisPassed) { this.staticAnalysisPassed = staticAnalysisPassed; }
    
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public List<String> getIssues() { return issues; }
    public void setIssues(List<String> issues) { this.issues = issues; }
}



