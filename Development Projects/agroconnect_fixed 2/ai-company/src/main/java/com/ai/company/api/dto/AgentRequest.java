package com.ai.company.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for agent execution.
 */
public class AgentRequest {
    
    @JsonProperty("input")
    private String input;
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("parameters")
    private java.util.Map<String, Object> parameters;
    
    public AgentRequest() {
    }
    
    public AgentRequest(String input, String sessionId) {
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
    
    public java.util.Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(java.util.Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}



