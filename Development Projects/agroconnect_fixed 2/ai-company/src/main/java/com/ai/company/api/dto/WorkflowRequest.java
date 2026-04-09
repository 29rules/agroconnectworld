package com.ai.company.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for workflow execution.
 */
public class WorkflowRequest {
    
    @JsonProperty("input")
    private String input;
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("workflow_parameters")
    private java.util.Map<String, Object> workflowParameters;
    
    public WorkflowRequest() {
    }
    
    public WorkflowRequest(String input, String sessionId) {
        this.input = input;
        this.sessionId = sessionId;
    }
    
    // Getters and Setters
    public String getInput() {
        return input;
    }
    
    public void setInput(String input) {
        this.input = input;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public java.util.Map<String, Object> getWorkflowParameters() {
        return workflowParameters;
    }
    
    public void setWorkflowParameters(java.util.Map<String, Object> workflowParameters) {
        this.workflowParameters = workflowParameters;
    }
}



