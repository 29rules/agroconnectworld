package com.ai.company.engineering;

import java.util.ArrayList;
import java.util.List;

public class PRReview {
    private boolean approved;
    private List<String> comments = new ArrayList<>();
    private String codeQuality;
    private List<String> securityConcerns = new ArrayList<>();
    private List<String> performanceImplications = new ArrayList<>();
    private String testCoverage;
    private String architectureImpact;
    private List<String> suggestions = new ArrayList<>();
    
    // Getters and Setters
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    
    public List<String> getComments() { return comments; }
    public void setComments(List<String> comments) { this.comments = comments; }
    
    public String getCodeQuality() { return codeQuality; }
    public void setCodeQuality(String codeQuality) { this.codeQuality = codeQuality; }
    
    public List<String> getSecurityConcerns() { return securityConcerns; }
    public void setSecurityConcerns(List<String> securityConcerns) { this.securityConcerns = securityConcerns; }
    
    public List<String> getPerformanceImplications() { return performanceImplications; }
    public void setPerformanceImplications(List<String> performanceImplications) { this.performanceImplications = performanceImplications; }
    
    public String getTestCoverage() { return testCoverage; }
    public void setTestCoverage(String testCoverage) { this.testCoverage = testCoverage; }
    
    public String getArchitectureImpact() { return architectureImpact; }
    public void setArchitectureImpact(String architectureImpact) { this.architectureImpact = architectureImpact; }
    
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
}



