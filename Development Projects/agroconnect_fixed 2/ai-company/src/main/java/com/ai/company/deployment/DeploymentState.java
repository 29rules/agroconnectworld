package com.ai.company.deployment;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Deployment State
 * 
 * Tracks the current state of a deployment operation.
 * 
 * Tracks:
 * - Service build status
 * - Test status
 * - Deployment environment
 * - Health check results
 * - Stage completion status
 * - Errors and warnings
 */
public class DeploymentState {
    
    private String deploymentId;
    private String environment; // staging, production
    private List<String> services;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private DeploymentStatus overallStatus;
    
    // Stage tracking
    private Map<String, StageStatus> stageStatuses;
    
    // Service-specific tracking
    private Map<String, ServiceState> serviceStates;
    
    // Health check results
    private Map<String, HealthCheckResult> healthChecks;
    
    // Errors and warnings
    private List<String> errors;
    private List<String> warnings;
    
    public DeploymentState() {
        this.deploymentId = UUID.randomUUID().toString();
        this.stageStatuses = new HashMap<>();
        this.serviceStates = new HashMap<>();
        this.healthChecks = new HashMap<>();
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.overallStatus = DeploymentStatus.PENDING;
        this.startTime = LocalDateTime.now();
    }
    
    /**
     * Updates stage status.
     */
    public void updateStageStatus(String stage, StageStatus status) {
        this.stageStatuses.put(stage, status);
        updateOverallStatus();
    }
    
    /**
     * Updates service state.
     */
    public void updateServiceState(String serviceName, ServiceState state) {
        this.serviceStates.put(serviceName, state);
    }
    
    /**
     * Adds health check result.
     */
    public void addHealthCheck(String serviceName, HealthCheckResult result) {
        this.healthChecks.put(serviceName, result);
    }
    
    /**
     * Adds an error.
     */
    public void addError(String error) {
        this.errors.add(LocalDateTime.now() + ": " + error);
        updateOverallStatus();
    }
    
    /**
     * Adds a warning.
     */
    public void addWarning(String warning) {
        this.warnings.add(LocalDateTime.now() + ": " + warning);
    }
    
    /**
     * Updates overall status based on stage statuses.
     */
    private void updateOverallStatus() {
        if (!errors.isEmpty()) {
            this.overallStatus = DeploymentStatus.FAILED;
            return;
        }
        
        boolean allComplete = true;
        boolean anyInProgress = false;
        
        for (StageStatus status : stageStatuses.values()) {
            if (status == StageStatus.IN_PROGRESS) {
                anyInProgress = true;
                allComplete = false;
            } else if (status != StageStatus.COMPLETED) {
                allComplete = false;
            }
        }
        
        if (allComplete && !anyInProgress) {
            this.overallStatus = DeploymentStatus.COMPLETED;
        } else if (anyInProgress) {
            this.overallStatus = DeploymentStatus.IN_PROGRESS;
        } else {
            this.overallStatus = DeploymentStatus.PENDING;
        }
    }
    
    /**
     * Marks deployment as completed.
     */
    public void markCompleted() {
        this.overallStatus = DeploymentStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }
    
    /**
     * Marks deployment as failed.
     */
    public void markFailed() {
        this.overallStatus = DeploymentStatus.FAILED;
        this.endTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getDeploymentId() { return deploymentId; }
    public void setDeploymentId(String deploymentId) { this.deploymentId = deploymentId; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public List<String> getServices() { return services; }
    public void setServices(List<String> services) { this.services = services; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public DeploymentStatus getOverallStatus() { return overallStatus; }
    public void setOverallStatus(DeploymentStatus overallStatus) { this.overallStatus = overallStatus; }
    
    public Map<String, StageStatus> getStageStatuses() { return stageStatuses; }
    public Map<String, ServiceState> getServiceStates() { return serviceStates; }
    public Map<String, HealthCheckResult> getHealthChecks() { return healthChecks; }
    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
    
    /**
     * Deployment status enumeration.
     */
    public enum DeploymentStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        ROLLED_BACK
    }
    
    /**
     * Stage status enumeration.
     */
    public enum StageStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        SKIPPED
    }
    
    /**
     * Service state tracking.
     */
    public static class ServiceState {
        private String serviceName;
        private BuildStatus buildStatus;
        private TestStatus testStatus;
        private DeploymentStatus deploymentStatus;
        private String buildLog;
        private String testLog;
        private LocalDateTime lastUpdated;
        
        public ServiceState(String serviceName) {
            this.serviceName = serviceName;
            this.buildStatus = BuildStatus.PENDING;
            this.testStatus = TestStatus.PENDING;
            this.deploymentStatus = DeploymentStatus.PENDING;
            this.lastUpdated = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getServiceName() { return serviceName; }
        public BuildStatus getBuildStatus() { return buildStatus; }
        public void setBuildStatus(BuildStatus buildStatus) { 
            this.buildStatus = buildStatus; 
            this.lastUpdated = LocalDateTime.now();
        }
        public TestStatus getTestStatus() { return testStatus; }
        public void setTestStatus(TestStatus testStatus) { 
            this.testStatus = testStatus; 
            this.lastUpdated = LocalDateTime.now();
        }
        public DeploymentStatus getDeploymentStatus() { return deploymentStatus; }
        public void setDeploymentStatus(DeploymentStatus deploymentStatus) { 
            this.deploymentStatus = deploymentStatus; 
            this.lastUpdated = LocalDateTime.now();
        }
        public String getBuildLog() { return buildLog; }
        public void setBuildLog(String buildLog) { this.buildLog = buildLog; }
        public String getTestLog() { return testLog; }
        public void setTestLog(String testLog) { this.testLog = testLog; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
    }
    
    /**
     * Build status enumeration.
     */
    public enum BuildStatus {
        PENDING,
        BUILDING,
        SUCCESS,
        FAILED
    }
    
    /**
     * Test status enumeration.
     */
    public enum TestStatus {
        PENDING,
        RUNNING,
        PASSED,
        FAILED
    }
    
    /**
     * Health check result.
     */
    public static class HealthCheckResult {
        private String serviceName;
        private boolean healthy;
        private int statusCode;
        private long latencyMs;
        private String response;
        private LocalDateTime checkedAt;
        
        public HealthCheckResult(String serviceName) {
            this.serviceName = serviceName;
            this.checkedAt = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getServiceName() { return serviceName; }
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        public long getLatencyMs() { return latencyMs; }
        public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
        public LocalDateTime getCheckedAt() { return checkedAt; }
        public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
    }
}



