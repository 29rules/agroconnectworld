package com.ai.company.promotion;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Result of build promotion process
 */
public class BuildPromotionResult {
    
    private String buildId;
    private String fromEnvironment;
    private String toEnvironment;
    private String branch;
    private String commit;
    private String status; // PROMOTED, REJECTED, DEPLOYMENT_FAILED, ERROR
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime promotionDate;
    
    private CodeQualityCheck codeQuality;
    private TestResults testResults;
    private SecurityScanResult securityScan;
    private BuildArtifacts artifacts;
    private DeploymentResult deployment;
    private String aiAnalysis;
    
    private List<String> issues = new ArrayList<>();
    
    public void addIssue(String issue) {
        this.issues.add(issue);
    }
    
    // Getters and Setters
    public String getBuildId() { return buildId; }
    public void setBuildId(String buildId) { this.buildId = buildId; }
    
    public String getFromEnvironment() { return fromEnvironment; }
    public void setFromEnvironment(String fromEnvironment) { this.fromEnvironment = fromEnvironment; }
    
    public String getToEnvironment() { return toEnvironment; }
    public void setToEnvironment(String toEnvironment) { this.toEnvironment = toEnvironment; }
    
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    
    public String getCommit() { return commit; }
    public void setCommit(String commit) { this.commit = commit; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getPromotionDate() { return promotionDate; }
    public void setPromotionDate(LocalDateTime promotionDate) { this.promotionDate = promotionDate; }
    
    public CodeQualityCheck getCodeQuality() { return codeQuality; }
    public void setCodeQuality(CodeQualityCheck codeQuality) { this.codeQuality = codeQuality; }
    
    public TestResults getTestResults() { return testResults; }
    public void setTestResults(TestResults testResults) { this.testResults = testResults; }
    
    public SecurityScanResult getSecurityScan() { return securityScan; }
    public void setSecurityScan(SecurityScanResult securityScan) { this.securityScan = securityScan; }
    
    public BuildArtifacts getArtifacts() { return artifacts; }
    public void setArtifacts(BuildArtifacts artifacts) { this.artifacts = artifacts; }
    
    public DeploymentResult getDeployment() { return deployment; }
    public void setDeployment(DeploymentResult deployment) { this.deployment = deployment; }
    
    public String getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(String aiAnalysis) { this.aiAnalysis = aiAnalysis; }
    
    public List<String> getIssues() { return issues; }
    public void setIssues(List<String> issues) { this.issues = issues; }
}



