package com.ai.company.deployment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Deployment Summary
 * 
 * JSON summary for final deployment report.
 * 
 * Contains:
 * - Deployment metadata
 * - Stage results
 * - Service status
 * - Health check results
 * - Errors and warnings
 * - Duration and timing
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeploymentSummary {
    
    private String deploymentId;
    private String environment;
    private List<String> services;
    private String status; // COMPLETED, FAILED, REJECTED, IN_PROGRESS
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String duration;
    private String errorMessage;
    
    private Map<String, String> stageResults;
    private Map<String, ServiceSummary> serviceSummaries;
    private Map<String, HealthCheckSummary> healthChecks;
    private List<String> errors;
    private List<String> warnings;
    
    private DeploymentState state;
    
    public DeploymentSummary() {
        this.stageResults = new HashMap<>();
        this.serviceSummaries = new HashMap<>();
        this.healthChecks = new HashMap<>();
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    /**
     * Adds a stage result.
     */
    public void addStageResult(String stage, String result) {
        this.stageResults.put(stage, result);
    }
    
    /**
     * Adds a service summary.
     */
    public void addServiceSummary(String serviceName, ServiceSummary summary) {
        this.serviceSummaries.put(serviceName, summary);
    }
    
    /**
     * Adds a health check summary.
     */
    public void addHealthCheck(String serviceName, HealthCheckSummary summary) {
        this.healthChecks.put(serviceName, summary);
    }
    
    /**
     * Calculates and sets duration.
     */
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            Duration duration = Duration.between(startTime, endTime);
            long minutes = duration.toMinutes();
            long seconds = duration.minusMinutes(minutes).getSeconds();
            this.duration = String.format("%d minutes %d seconds", minutes, seconds);
        }
    }
    
    /**
     * Converts to JSON string.
     */
    public String toJson() {
        calculateDuration();
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "ERROR: Failed to serialize deployment summary: " + e.getMessage();
        }
    }
    
    // Getters and Setters
    public String getDeploymentId() { return deploymentId; }
    public void setDeploymentId(String deploymentId) { this.deploymentId = deploymentId; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public List<String> getServices() { return services; }
    public void setServices(List<String> services) { this.services = services; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Map<String, String> getStageResults() { return stageResults; }
    public void setStageResults(Map<String, String> stageResults) { this.stageResults = stageResults; }
    
    public Map<String, ServiceSummary> getServiceSummaries() { return serviceSummaries; }
    public void setServiceSummaries(Map<String, ServiceSummary> serviceSummaries) { this.serviceSummaries = serviceSummaries; }
    
    public Map<String, HealthCheckSummary> getHealthChecks() { return healthChecks; }
    public void setHealthChecks(Map<String, HealthCheckSummary> healthChecks) { this.healthChecks = healthChecks; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public DeploymentState getState() { return state; }
    public void setState(DeploymentState state) { 
        this.state = state;
        if (state != null) {
            this.errors = state.getErrors();
            this.warnings = state.getWarnings();
        }
    }
    
    /**
     * Service summary.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ServiceSummary {
        private String serviceName;
        private String buildStatus;
        private String testStatus;
        private String deploymentStatus;
        private String buildLog;
        private String testLog;
        
        // Getters and Setters
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public String getBuildStatus() { return buildStatus; }
        public void setBuildStatus(String buildStatus) { this.buildStatus = buildStatus; }
        public String getTestStatus() { return testStatus; }
        public void setTestStatus(String testStatus) { this.testStatus = testStatus; }
        public String getDeploymentStatus() { return deploymentStatus; }
        public void setDeploymentStatus(String deploymentStatus) { this.deploymentStatus = deploymentStatus; }
        public String getBuildLog() { return buildLog; }
        public void setBuildLog(String buildLog) { this.buildLog = buildLog; }
        public String getTestLog() { return testLog; }
        public void setTestLog(String testLog) { this.testLog = testLog; }
    }
    
    /**
     * Health check summary.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HealthCheckSummary {
        private String serviceName;
        private boolean healthy;
        private int statusCode;
        private long latencyMs;
        private LocalDateTime checkedAt;
        
        // Getters and Setters
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        public long getLatencyMs() { return latencyMs; }
        public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
        public LocalDateTime getCheckedAt() { return checkedAt; }
        public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
    }
}



