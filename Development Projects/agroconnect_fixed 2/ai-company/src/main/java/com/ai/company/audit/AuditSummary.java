package com.ai.company.audit;

/**
 * Summary of audit findings
 */
public class AuditSummary {
    
    private int totalIssues;
    private int criticalIssues;
    private int highIssues;
    private int mediumIssues;
    private int lowIssues;
    
    private int bugsCount;
    private int uiIssuesCount;
    private int backendErrorsCount;
    private int brokenApiLinksCount;
    private int missingTestsCount;
    private int performanceProblemsCount;
    private int securityVulnerabilitiesCount;
    
    // Getters and Setters
    public int getTotalIssues() { return totalIssues; }
    public void setTotalIssues(int totalIssues) { this.totalIssues = totalIssues; }
    
    public int getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(int criticalIssues) { this.criticalIssues = criticalIssues; }
    
    public int getHighIssues() { return highIssues; }
    public void setHighIssues(int highIssues) { this.highIssues = highIssues; }
    
    public int getMediumIssues() { return mediumIssues; }
    public void setMediumIssues(int mediumIssues) { this.mediumIssues = mediumIssues; }
    
    public int getLowIssues() { return lowIssues; }
    public void setLowIssues(int lowIssues) { this.lowIssues = lowIssues; }
    
    public int getBugsCount() { return bugsCount; }
    public void setBugsCount(int bugsCount) { this.bugsCount = bugsCount; }
    
    public int getUiIssuesCount() { return uiIssuesCount; }
    public void setUiIssuesCount(int uiIssuesCount) { this.uiIssuesCount = uiIssuesCount; }
    
    public int getBackendErrorsCount() { return backendErrorsCount; }
    public void setBackendErrorsCount(int backendErrorsCount) { this.backendErrorsCount = backendErrorsCount; }
    
    public int getBrokenApiLinksCount() { return brokenApiLinksCount; }
    public void setBrokenApiLinksCount(int brokenApiLinksCount) { this.brokenApiLinksCount = brokenApiLinksCount; }
    
    public int getMissingTestsCount() { return missingTestsCount; }
    public void setMissingTestsCount(int missingTestsCount) { this.missingTestsCount = missingTestsCount; }
    
    public int getPerformanceProblemsCount() { return performanceProblemsCount; }
    public void setPerformanceProblemsCount(int performanceProblemsCount) { this.performanceProblemsCount = performanceProblemsCount; }
    
    public int getSecurityVulnerabilitiesCount() { return securityVulnerabilitiesCount; }
    public void setSecurityVulnerabilitiesCount(int securityVulnerabilitiesCount) { this.securityVulnerabilitiesCount = securityVulnerabilitiesCount; }
}



