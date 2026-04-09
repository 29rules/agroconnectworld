package com.ai.company.devops;

public class ValidationSummary {
    private boolean dockerValid;
    private boolean cicdValid;
    private boolean environmentValid;
    private boolean nginxValid;
    private boolean certificatesValid;
    private boolean readinessValid;
    private int overallScore; // 0-100
    
    // Getters and Setters
    public boolean isDockerValid() { return dockerValid; }
    public void setDockerValid(boolean dockerValid) { this.dockerValid = dockerValid; }
    
    public boolean isCicdValid() { return cicdValid; }
    public void setCicdValid(boolean cicdValid) { this.cicdValid = cicdValid; }
    
    public boolean isEnvironmentValid() { return environmentValid; }
    public void setEnvironmentValid(boolean environmentValid) { this.environmentValid = environmentValid; }
    
    public boolean isNginxValid() { return nginxValid; }
    public void setNginxValid(boolean nginxValid) { this.nginxValid = nginxValid; }
    
    public boolean isCertificatesValid() { return certificatesValid; }
    public void setCertificatesValid(boolean certificatesValid) { this.certificatesValid = certificatesValid; }
    
    public boolean isReadinessValid() { return readinessValid; }
    public void setReadinessValid(boolean readinessValid) { this.readinessValid = readinessValid; }
    
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
}



