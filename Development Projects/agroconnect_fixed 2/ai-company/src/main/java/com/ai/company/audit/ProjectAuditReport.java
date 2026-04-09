package com.ai.company.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Comprehensive project audit report
 */
public class ProjectAuditReport {
    
    private String auditId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditDate;
    
    private List<AuditFinding> bugs = new ArrayList<>();
    private List<AuditFinding> uiIssues = new ArrayList<>();
    private List<AuditFinding> backendErrors = new ArrayList<>();
    private List<AuditFinding> brokenApiLinks = new ArrayList<>();
    private List<AuditFinding> missingTests = new ArrayList<>();
    private List<AuditFinding> performanceProblems = new ArrayList<>();
    private List<AuditFinding> securityVulnerabilities = new ArrayList<>();
    
    private AuditSummary summary;
    private List<String> errors = new ArrayList<>();
    
    public void generateSummary() {
        summary = new AuditSummary();
        summary.setTotalIssues(getTotalIssues());
        summary.setCriticalIssues(countBySeverity("CRITICAL"));
        summary.setHighIssues(countBySeverity("HIGH"));
        summary.setMediumIssues(countBySeverity("MEDIUM"));
        summary.setLowIssues(countBySeverity("LOW"));
        summary.setBugsCount(bugs.size());
        summary.setUiIssuesCount(uiIssues.size());
        summary.setBackendErrorsCount(backendErrors.size());
        summary.setBrokenApiLinksCount(brokenApiLinks.size());
        summary.setMissingTestsCount(missingTests.size());
        summary.setPerformanceProblemsCount(performanceProblems.size());
        summary.setSecurityVulnerabilitiesCount(securityVulnerabilities.size());
    }
    
    @JsonIgnore
    public int getTotalIssues() {
        return bugs.size() + uiIssues.size() + backendErrors.size() + 
               brokenApiLinks.size() + missingTests.size() + 
               performanceProblems.size() + securityVulnerabilities.size();
    }
    
    private int countBySeverity(String severity) {
        return getAllFindings().stream()
            .filter(f -> severity.equals(f.getSeverity()))
            .collect(Collectors.toList())
            .size();
    }
    
    @JsonIgnore
    public List<AuditFinding> getAllFindings() {
        List<AuditFinding> all = new ArrayList<>();
        all.addAll(bugs);
        all.addAll(uiIssues);
        all.addAll(backendErrors);
        all.addAll(brokenApiLinks);
        all.addAll(missingTests);
        all.addAll(performanceProblems);
        all.addAll(securityVulnerabilities);
        return all;
    }
    
    public void addError(String error) {
        this.errors.add(error);
    }
    
    // Getters and Setters
    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }
    
    public LocalDateTime getAuditDate() { return auditDate; }
    public void setAuditDate(LocalDateTime auditDate) { this.auditDate = auditDate; }
    
    public List<AuditFinding> getBugs() { return bugs; }
    public void setBugs(List<AuditFinding> bugs) { this.bugs = bugs; }
    
    public List<AuditFinding> getUiIssues() { return uiIssues; }
    public void setUiIssues(List<AuditFinding> uiIssues) { this.uiIssues = uiIssues; }
    
    public List<AuditFinding> getBackendErrors() { return backendErrors; }
    public void setBackendErrors(List<AuditFinding> backendErrors) { this.backendErrors = backendErrors; }
    
    public List<AuditFinding> getBrokenApiLinks() { return brokenApiLinks; }
    public void setBrokenApiLinks(List<AuditFinding> brokenApiLinks) { this.brokenApiLinks = brokenApiLinks; }
    
    public List<AuditFinding> getMissingTests() { return missingTests; }
    public void setMissingTests(List<AuditFinding> missingTests) { this.missingTests = missingTests; }
    
    public List<AuditFinding> getPerformanceProblems() { return performanceProblems; }
    public void setPerformanceProblems(List<AuditFinding> performanceProblems) { this.performanceProblems = performanceProblems; }
    
    public List<AuditFinding> getSecurityVulnerabilities() { return securityVulnerabilities; }
    public void setSecurityVulnerabilities(List<AuditFinding> securityVulnerabilities) { this.securityVulnerabilities = securityVulnerabilities; }
    
    public AuditSummary getSummary() { return summary; }
    public void setSummary(AuditSummary summary) { this.summary = summary; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}



