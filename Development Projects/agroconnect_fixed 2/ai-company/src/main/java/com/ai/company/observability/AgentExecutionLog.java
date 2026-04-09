package com.ai.company.observability;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Log entry for individual agent execution.
 * 
 * Tracks:
 * - Execution metadata (timestamp, UUID, agent name)
 * - Input/output (prompt summary, response summary)
 * - Execution status (success, warnings, errors)
 * - Performance metrics (duration, token usage)
 */
public class AgentExecutionLog {
    
    private UUID executionId;
    private Instant timestamp;
    private String agentName;
    private String sessionId;
    private ExecutionStatus status;
    
    // Input/Output
    private String promptSummary;
    private String responseSummary;
    private int promptLength;
    private int responseLength;
    
    // Execution details
    private Long durationMs;
    private Integer tokenCount;
    private String modelUsed;
    
    // Issues
    private final List<String> warnings;
    private final List<String> errors;
    
    // Metadata
    private String workflowName;
    private String taskId;
    private java.util.Map<String, Object> metadata;
    
    public AgentExecutionLog() {
        this.executionId = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.status = ExecutionStatus.PENDING;
        this.warnings = new ArrayList<>();
        this.errors = new ArrayList<>();
    }
    
    public AgentExecutionLog(String agentName, String sessionId) {
        this();
        this.agentName = agentName;
        this.sessionId = sessionId;
    }
    
    // Getters and Setters
    public UUID getExecutionId() {
        return executionId;
    }
    
    public void setExecutionId(UUID executionId) {
        this.executionId = executionId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getAgentName() {
        return agentName;
    }
    
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public ExecutionStatus getStatus() {
        return status;
    }
    
    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }
    
    public String getPromptSummary() {
        return promptSummary;
    }
    
    public void setPromptSummary(String promptSummary) {
        this.promptSummary = promptSummary;
        this.promptLength = promptSummary != null ? promptSummary.length() : 0;
    }
    
    public String getResponseSummary() {
        return responseSummary;
    }
    
    public void setResponseSummary(String responseSummary) {
        this.responseSummary = responseSummary;
        this.responseLength = responseSummary != null ? responseSummary.length() : 0;
    }
    
    public int getPromptLength() {
        return promptLength;
    }
    
    public int getResponseLength() {
        return responseLength;
    }
    
    public Long getDurationMs() {
        return durationMs;
    }
    
    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
    
    public Integer getTokenCount() {
        return tokenCount;
    }
    
    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
    }
    
    public String getModelUsed() {
        return modelUsed;
    }
    
    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
    
    public void addWarning(String warning) {
        this.warnings.add(warning);
        if (this.status == ExecutionStatus.SUCCESS) {
            this.status = ExecutionStatus.WARNING;
        }
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void addError(String error) {
        this.errors.add(error);
        this.status = ExecutionStatus.ERROR;
    }
    
    public String getWorkflowName() {
        return workflowName;
    }
    
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public java.util.Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(java.util.Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    /**
     * Marks execution as successful.
     */
    public void markSuccess() {
        this.status = ExecutionStatus.SUCCESS;
    }
    
    /**
     * Marks execution as failed.
     */
    public void markFailed(String error) {
        this.status = ExecutionStatus.ERROR;
        addError(error);
    }
    
    /**
     * Checks if execution has warnings.
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * Checks if execution has errors.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Execution status enumeration.
     */
    public enum ExecutionStatus {
        PENDING,
        RUNNING,
        SUCCESS,
        WARNING,
        ERROR,
        CANCELLED
    }
    
    @Override
    public String toString() {
        return String.format("AgentExecutionLog[id=%s, agent=%s, status=%s, timestamp=%s]",
            executionId, agentName, status, timestamp);
    }
}



