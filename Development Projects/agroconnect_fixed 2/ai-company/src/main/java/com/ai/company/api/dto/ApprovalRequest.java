package com.ai.company.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApprovalRequest {

    @JsonProperty("decision")
    private String decision;

    @JsonProperty("context")
    private String context;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("request_type")
    private String requestType;

    public ApprovalRequest() {}

    public ApprovalRequest(String decision, String context, String sessionId) {
        this.decision = decision;
        this.context = context;
        this.sessionId = sessionId;
        this.requestType = "approval";
    }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
}


