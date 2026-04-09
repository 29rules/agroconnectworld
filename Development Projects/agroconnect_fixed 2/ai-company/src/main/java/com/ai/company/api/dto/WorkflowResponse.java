package com.ai.company.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for workflow execution.
 */
public class WorkflowResponse {
    
    @JsonProperty("workflow_name")
    private String workflowName;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("timestamp")
    private Instant timestamp;
    
    @JsonProperty("results")
    private Map<String, Object> results;
    
    @JsonProperty("execution_steps")
    private List<ExecutionStep> executionSteps;
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
    
    public WorkflowResponse() {
        this.timestamp = Instant.now();
        this.status = "completed";
    }
    
    public WorkflowResponse(String workflowName, String sessionId) {
        this.workflowName = workflowName;
        this.sessionId = sessionId;
        this.timestamp = Instant.now();
        this.status = "completed";
    }
    
    // Getters and Setters
    public String getWorkflowName() {
        return workflowName;
    }
    
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public Map<String, Object> getResults() {
        return results;
    }
    
    public void setResults(Map<String, Object> results) {
        this.results = results;
    }
    
    public List<ExecutionStep> getExecutionSteps() {
        return executionSteps;
    }
    
    public void setExecutionSteps(List<ExecutionStep> executionSteps) {
        this.executionSteps = executionSteps;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    /**
     * Execution step information.
     */
    public static class ExecutionStep {
        @JsonProperty("step_name")
        private String stepName;
        
        @JsonProperty("agent")
        private String agent;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("output")
        private String output;
        
        // Getters and Setters
        public String getStepName() {
            return stepName;
        }
        
        public void setStepName(String stepName) {
            this.stepName = stepName;
        }
        
        public String getAgent() {
            return agent;
        }
        
        public void setAgent(String agent) {
            this.agent = agent;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getOutput() {
            return output;
        }
        
        public void setOutput(String output) {
            this.output = output;
        }
    }
}



