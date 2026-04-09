package com.ai.company.api;

import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.api.dto.ApprovalRequest;
import com.ai.company.api.dto.ApprovalResponse;
import com.ai.company.api.dto.CtoDebugResponse;
import com.ai.company.registry.AgentRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cto")
public class CtoController {

    private static final Logger log = LoggerFactory.getLogger(CtoController.class);

    private final AgentRegistry agentRegistry;
    private final ObjectMapper objectMapper;

    public CtoController(AgentRegistry agentRegistry) {
        this.agentRegistry = agentRegistry;
        this.objectMapper = new ObjectMapper();
    }

    // -----------------------------------------------------------------------
    @PostMapping("/debug")
    public ResponseEntity<CtoDebugResponse> debug() {
        return ResponseEntity.ok(agentRegistry.getCtoDebugInfo());
    }

    // -----------------------------------------------------------------------
    @PostMapping("/approve")
    public ResponseEntity<ApprovalResponse> approve(@RequestBody ApprovalRequest request) {

        log.info("CTO approval request | session={} | type={}",
                request.getSessionId(), request.getRequestType());

        try {
            String sessionId = request.getSessionId() != null ? request.getSessionId() : "default-session";
            String decision = request.getDecision();

            CTOAgent cto = agentRegistry.getCTOAgent();

            String ctoResponse;
            String type = request.getRequestType() != null ? request.getRequestType().toLowerCase() : "approval";

            switch (type) {
                case "approval" -> ctoResponse = cto.approveDecision(decision, sessionId);
                case "review" -> ctoResponse = cto.reviewArchitecture(decision, sessionId);
                case "risk_assessment" -> ctoResponse = cto.assessRisk(decision, sessionId);
                default -> ctoResponse = cto.approveDecision(decision, sessionId);
            }

            return ResponseEntity.ok(parseCTOResponse(ctoResponse, sessionId));

        } catch (Exception e) {
            log.error("Error processing CTO approval", e);

            ApprovalResponse error = new ApprovalResponse();
            error.setSessionId(request.getSessionId());
            error.setDecisionType("error");
            error.setApproved(false);
            error.setReviewSummary("Error: " + e.getMessage());
            error.setTimestamp(Instant.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // -----------------------------------------------------------------------
    @PostMapping("/review")
    public ResponseEntity<ApprovalResponse> review(@RequestBody ApprovalRequest request) {
        request.setRequestType("review");
        return approve(request);
    }

    // -----------------------------------------------------------------------
    @PostMapping("/assess-risk")
    public ResponseEntity<ApprovalResponse> assessRisk(@RequestBody ApprovalRequest request) {
        request.setRequestType("risk_assessment");
        return approve(request);
    }

    // -----------------------------------------------------------------------
    @GetMapping("/test")
    public Map<String, Object> testCtoAgent() {
        Map<String, Object> result = new HashMap<>();
        try {
            CTOAgent cto = agentRegistry.getCTOAgent();
            result.put("status", "ok");
            result.put("message", "CTO Agent controller endpoint is working");
            result.put("ctoAgentLoaded", cto != null);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    // ===================================================================
    // SAFE PARSER — ALWAYS RETURNS A CLEAN ApprovalResponse
    // ===================================================================
    private ApprovalResponse parseCTOResponse(String raw, String sessionId) {

        ApprovalResponse resp = new ApprovalResponse();
        resp.setSessionId(sessionId);
        resp.setTimestamp(Instant.now());

        if (raw == null || raw.isBlank()) {
            resp.setDecisionType("error");
            resp.setApproved(false);
            resp.setReviewSummary("Empty response from CTO Agent.");
            resp.setTechnicalAssessment(defaultAssessment());
            return resp;
        }

        Map<String, Object> parsed = null;

        try {
            parsed = objectMapper.readValue(raw, Map.class);
        } catch (Exception ignore) {
            // model returned plain text → fallback
        }

        if (parsed == null) {
            resp.setDecisionType("free_text");
            resp.setApproved(false);
            resp.setReviewSummary(raw.trim());
            resp.setTechnicalAssessment(defaultAssessment());
            return resp;
        }

        // Map JSON safely
        resp.setDecisionType(stringVal(parsed.get("type"), "review"));
        resp.setApproved(boolVal(parsed.get("approved"), false));
        resp.setReviewSummary(stringVal(parsed.get("summary"), raw));
        resp.setRiskLevel(stringVal(parsed.get("risk_level"), null));
        resp.setTechnicalAssessment(mapToAssessment(parsed));

        return resp;
    }

    // ===================================================================
    // Map TechnicalAssessment (THIS MATCHES YOUR CURRENT DTO EXACTLY)
    // ===================================================================
    private ApprovalResponse.TechnicalAssessment mapToAssessment(Map<String, Object> map) {

        ApprovalResponse.TechnicalAssessment ta = new ApprovalResponse.TechnicalAssessment();

        ta.setImpactAnalysis(
                stringVal(map.get("impact_analysis"), "")
        );

        ta.setComplianceCheck(
                stringVal(map.get("compliance_check"), "")
        );

        ta.setBreakingChanges(
                boolVal(map.get("breaking_changes"), false)
        );

        return ta;
    }

    private ApprovalResponse.TechnicalAssessment defaultAssessment() {
        ApprovalResponse.TechnicalAssessment ta = new ApprovalResponse.TechnicalAssessment();
        ta.setImpactAnalysis("");
        ta.setComplianceCheck("");
        ta.setBreakingChanges(false);
        return ta;
    }

    // ===================================================================
    // UTILS
    // ===================================================================
    private String stringVal(Object obj, String def) {
        return obj == null ? def : String.valueOf(obj);
    }

    private boolean boolVal(Object obj, boolean def) {
        if (obj == null) return def;
        if (obj instanceof Boolean b) return b;
        return Boolean.parseBoolean(String.valueOf(obj));
    }
}
