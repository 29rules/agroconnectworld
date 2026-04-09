package com.ai.company.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Response DTO for agent execution.
 */
public class AgentResponse {
    
    @JsonProperty("agent_name")
    private String agentName;
    
    @JsonProperty("response")
    private String response;
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("timestamp")
    private Instant timestamp;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("metadata")
    private java.util.Map<String, Object> metadata;
    
    public AgentResponse() {
        this.timestamp = Instant.now();
        this.status = "success";
    }
    
    public AgentResponse(String agentName, String response, String sessionId) {
        this.agentName = agentName;
        this.response = response;
        this.sessionId = sessionId;
        this.timestamp = Instant.now();
        this.status = "success";
    }
    
    // Getters and Setters
    public String getAgentName() {
        return agentName;
    }
    
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public java.util.Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(java.util.Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}



