package com.ai.company.observability;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Log entry for workflow execution.
 * 
 * Tracks:
 * - Workflow metadata (name, UUID, timestamp)
 * - Execution steps (agent executions)
 * - Overall status and metrics
 * - Warnings and errors across workflow
 */
public class WorkflowExecutionLog {
    
    private UUID workflowExecutionId;
    private Instant startTimestamp;
    private Instant endTimestamp;
    private String workflowName;
    private String sessionId;
    private WorkflowStatus status;
    
    // Execution steps
    private final List<AgentExecutionLog> agentExecutions;
    private int totalSteps;
    private int completedSteps;
    private int failedSteps;
    
    // Input/Output
    private String inputSummary;
    private String outputSummary;
    
    // Performance
    private Long totalDurationMs;
    private Integer totalTokenCount;
    
    // Issues
    private final List<String> warnings;
    private final List<String> errors;
    
    // Metadata
    private String initiatedBy;
    private java.util.Map<String, Object> metadata;
    
    public WorkflowExecutionLog() {
        this.workflowExecutionId = UUID.randomUUID();
        this.startTimestamp = Instant.now();
        this.status = WorkflowStatus.PENDING;
        this.agentExecutions = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.totalSteps = 0;
        this.completedSteps = 0;
        this.failedSteps = 0;
    }
    
    public WorkflowExecutionLog(String workflowName, String sessionId) {
        this();
        this.workflowName = workflowName;
        this.sessionId = sessionId;
    }
    
    // Getters and Setters
    public UUID getWorkflowExecutionId() {
        return workflowExecutionId;
    }
    
    public void setWorkflowExecutionId(UUID workflowExecutionId) {
        this.workflowExecutionId = workflowExecutionId;
    }
    
    public Instant getStartTimestamp() {
        return startTimestamp;
    }
    
    public void setStartTimestamp(Instant startTimestamp) {
        this.startTimestamp = startTimestamp;
    }
    
    public Instant getEndTimestamp() {
        return endTimestamp;
    }
    
    public void setEndTimestamp(Instant endTimestamp) {
        this.endTimestamp = endTimestamp;
        if (this.startTimestamp != null && this.endTimestamp != null) {
            this.totalDurationMs = java.time.Duration.between(startTimestamp, endTimestamp).toMillis();
        }
    }
    
    public String getWorkflowName() {
        return workflowName;
    }
    
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public WorkflowStatus getStatus() {
        return status;
    }
    
    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }
    
    public List<AgentExecutionLog> getAgentExecutions() {
        return agentExecutions;
    }
    
    public void addAgentExecution(AgentExecutionLog execution) {
        this.agentExecutions.add(execution);
        this.totalSteps++;
        
        // Update status based on agent execution
        if (execution.getStatus() == AgentExecutionLog.ExecutionStatus.SUCCESS) {
            this.completedSteps++;
        } else if (execution.getStatus() == AgentExecutionLog.ExecutionStatus.ERROR) {
            this.failedSteps++;
            this.status = WorkflowStatus.ERROR;
        } else if (execution.getStatus() == AgentExecutionLog.ExecutionStatus.WARNING) {
            this.completedSteps++;
            if (this.status == WorkflowStatus.SUCCESS) {
                this.status = WorkflowStatus.WARNING;
            }
        }
        
        // Aggregate warnings and errors
        if (execution.hasWarnings()) {
            this.warnings.addAll(execution.getWarnings());
        }
        if (execution.hasErrors()) {
            this.errors.addAll(execution.getErrors());
        }
        
        // Aggregate token count
        if (execution.getTokenCount() != null) {
            if (this.totalTokenCount == null) {
                this.totalTokenCount = 0;
            }
            this.totalTokenCount += execution.getTokenCount();
        }
    }
    
    public int getTotalSteps() {
        return totalSteps;
    }
    
    public int getCompletedSteps() {
        return completedSteps;
    }
    
    public int getFailedSteps() {
        return failedSteps;
    }
    
    public String getInputSummary() {
        return inputSummary;
    }
    
    public void setInputSummary(String inputSummary) {
        this.inputSummary = inputSummary;
    }
    
    public String getOutputSummary() {
        return outputSummary;
    }
    
    public void setOutputSummary(String outputSummary) {
        this.outputSummary = outputSummary;
    }
    
    public Long getTotalDurationMs() {
        return totalDurationMs;
    }
    
    public void setTotalDurationMs(Long totalDurationMs) {
        this.totalDurationMs = totalDurationMs;
    }
    
    public Integer getTotalTokenCount() {
        return totalTokenCount;
    }
    
    public void setTotalTokenCount(Integer totalTokenCount) {
        this.totalTokenCount = totalTokenCount;
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
    
    public void addWarning(String warning) {
        this.warnings.add(warning);
        if (this.status == WorkflowStatus.SUCCESS) {
            this.status = WorkflowStatus.WARNING;
        }
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void addError(String error) {
        this.errors.add(error);
        this.status = WorkflowStatus.ERROR;
    }
    
    public String getInitiatedBy() {
        return initiatedBy;
    }
    
    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }
    
    public java.util.Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(java.util.Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    /**
     * Marks workflow as started.
     */
    public void markStarted() {
        this.status = WorkflowStatus.RUNNING;
        this.startTimestamp = Instant.now();
    }
    
    /**
     * Marks workflow as completed successfully.
     */
    public void markCompleted() {
        this.status = WorkflowStatus.SUCCESS;
        this.endTimestamp = Instant.now();
    }
    
    /**
     * Marks workflow as failed.
     */
    public void markFailed(String error) {
        this.status = WorkflowStatus.ERROR;
        this.endTimestamp = Instant.now();
        addError(error);
    }
    
    /**
     * Marks workflow as cancelled.
     */
    public void markCancelled() {
        this.status = WorkflowStatus.CANCELLED;
        this.endTimestamp = Instant.now();
    }
    
    /**
     * Checks if workflow has warnings.
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * Checks if workflow has errors.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Gets completion percentage.
     */
    public double getCompletionPercentage() {
        if (totalSteps == 0) {
            return 0.0;
        }
        return (double) completedSteps / totalSteps * 100.0;
    }
    
    /**
     * Workflow status enumeration.
     */
    public enum WorkflowStatus {
        PENDING,
        RUNNING,
        SUCCESS,
        WARNING,
        ERROR,
        CANCELLED
    }
    
    @Override
    public String toString() {
        return String.format("WorkflowExecutionLog[id=%s, workflow=%s, status=%s, steps=%d/%d]",
            workflowExecutionId, workflowName, status, completedSteps, totalSteps);
    }
}



