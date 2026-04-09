package com.ai.company.devops;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DevOpsValidationReport {
    
    private String validationId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validationDate;
    
    private ValidationResult dockerValidation;
    private ValidationResult cicdValidation;
    private ValidationResult environmentValidation;
    private ValidationResult nginxValidation;
    private ValidationResult certificateValidation;
    private ValidationResult readinessValidation;
    
    private ValidationSummary summary;
    private List<String> errors = new ArrayList<>();
    
    public void generateSummary() {
        summary = new ValidationSummary();
        summary.setDockerValid(dockerValidation != null && dockerValidation.isValid());
        summary.setCicdValid(cicdValidation != null && cicdValidation.isValid());
        summary.setEnvironmentValid(environmentValidation != null && environmentValidation.isValid());
        summary.setNginxValid(nginxValidation != null && nginxValidation.isValid());
        summary.setCertificatesValid(certificateValidation != null && certificateValidation.isValid());
        summary.setReadinessValid(readinessValidation != null && readinessValidation.isValid());
        
        int totalScore = (dockerValidation != null ? dockerValidation.getScore() : 0) +
                        (cicdValidation != null ? cicdValidation.getScore() : 0) +
                        (environmentValidation != null ? environmentValidation.getScore() : 0) +
                        (nginxValidation != null ? nginxValidation.getScore() : 0) +
                        (certificateValidation != null ? certificateValidation.getScore() : 0) +
                        (readinessValidation != null ? readinessValidation.getScore() : 0);
        summary.setOverallScore(totalScore / 6);
    }
    
    public void addError(String error) {
        this.errors.add(error);
    }
    
    // Getters and Setters
    public String getValidationId() { return validationId; }
    public void setValidationId(String validationId) { this.validationId = validationId; }
    
    public LocalDateTime getValidationDate() { return validationDate; }
    public void setValidationDate(LocalDateTime validationDate) { this.validationDate = validationDate; }
    
    public ValidationResult getDockerValidation() { return dockerValidation; }
    public void setDockerValidation(ValidationResult dockerValidation) { this.dockerValidation = dockerValidation; }
    
    public ValidationResult getCicdValidation() { return cicdValidation; }
    public void setCicdValidation(ValidationResult cicdValidation) { this.cicdValidation = cicdValidation; }
    
    public ValidationResult getEnvironmentValidation() { return environmentValidation; }
    public void setEnvironmentValidation(ValidationResult environmentValidation) { this.environmentValidation = environmentValidation; }
    
    public ValidationResult getNginxValidation() { return nginxValidation; }
    public void setNginxValidation(ValidationResult nginxValidation) { this.nginxValidation = nginxValidation; }
    
    public ValidationResult getCertificateValidation() { return certificateValidation; }
    public void setCertificateValidation(ValidationResult certificateValidation) { this.certificateValidation = certificateValidation; }
    
    public ValidationResult getReadinessValidation() { return readinessValidation; }
    public void setReadinessValidation(ValidationResult readinessValidation) { this.readinessValidation = readinessValidation; }
    
    public ValidationSummary getSummary() { return summary; }
    public void setSummary(ValidationSummary summary) { this.summary = summary; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}



