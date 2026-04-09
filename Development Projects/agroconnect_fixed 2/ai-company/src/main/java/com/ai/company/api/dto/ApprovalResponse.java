package com.ai.company.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for CTO approval.
 */
public class ApprovalResponse {

    @JsonProperty("decision_type")
    private String decisionType; // "approval", "rejection", "modification_request"

    @JsonProperty("approved")
    private Boolean approved;

    @JsonProperty("review_summary")
    private String reviewSummary;

    @JsonProperty("risk_level")
    private String riskLevel; // "low", "medium", "high", "critical"

    @JsonProperty("recommendations")
    private List<String> recommendations;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("technical_assessment")
    private TechnicalAssessment technicalAssessment;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    public ApprovalResponse() {
        this.timestamp = Instant.now();
    }

    // -------------------------------
    // Getters and Setters
    // -------------------------------

    public String getDecisionType() { return decisionType; }
    public void setDecisionType(String decisionType) { this.decisionType = decisionType; }

    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }

    public String getReviewSummary() { return reviewSummary; }
    public void setReviewSummary(String reviewSummary) { this.reviewSummary = reviewSummary; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public TechnicalAssessment getTechnicalAssessment() { return technicalAssessment; }
    public void setTechnicalAssessment(TechnicalAssessment technicalAssessment) { this.technicalAssessment = technicalAssessment; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    // ==========================================================
    //                   INNER CLASS (FIXED)
    // ==========================================================
    public static class TechnicalAssessment {

        // --- Your original fields ---
        @JsonProperty("impact_analysis")
        private String impactAnalysis;

        @JsonProperty("compliance_check")
        private String complianceCheck;

        @JsonProperty("breaking_changes")
        private Boolean breakingChanges;

        // --- Fields CONTROLLER expects ---
        private String summary;
        private String impact;
        private String risks;
        private String recommendation;
        private boolean approved;

        // ----------- ORIGINAL GETTERS/SETTERS -----------
        public String getImpactAnalysis() { return impactAnalysis; }
        public void setImpactAnalysis(String impactAnalysis) { this.impactAnalysis = impactAnalysis; }

        public String getComplianceCheck() { return complianceCheck; }
        public void setComplianceCheck(String complianceCheck) { this.complianceCheck = complianceCheck; }

        public Boolean getBreakingChanges() { return breakingChanges; }
        public void setBreakingChanges(Boolean breakingChanges) { this.breakingChanges = breakingChanges; }

        // ----------- NEW REQUIRED GETTERS/SETTERS -----------

        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }

        public String getImpact() { return impact; }
        public void setImpact(String impact) { this.impact = impact; }

        public String getRisks() { return risks; }
        public void setRisks(String risks) { this.risks = risks; }

        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

        public boolean isApproved() { return approved; }
        public void setApproved(boolean approved) { this.approved = approved; }
    }

    // ==========================================================
    // Helper Factory Methods
    // ==========================================================
    public static ApprovalResponse error(String msg) {
        ApprovalResponse r = new ApprovalResponse();
        r.setApproved(false);
        r.setDecisionType("error");
        r.setReviewSummary(msg);
        return r;
    }

    public static ApprovalResponse success(String sessionId, String type, String summary) {
        ApprovalResponse r = new ApprovalResponse();
        r.setSessionId(sessionId);
        r.setDecisionType(type);
        r.setApproved(true);
        r.setReviewSummary(summary);
        return r;
    }
}


