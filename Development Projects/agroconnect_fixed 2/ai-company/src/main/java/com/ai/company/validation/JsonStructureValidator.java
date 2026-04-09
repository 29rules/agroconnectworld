package com.ai.company.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validates JSON structure of agent outputs.
 * 
 * Ensures:
 * - Valid JSON syntax
 * - Required fields are present
 * - Field types are correct
 * - JSON structure matches expected schema
 */
public class JsonStructureValidator {
    
    private static final Logger log = LoggerFactory.getLogger(JsonStructureValidator.class);
    
    private final ObjectMapper objectMapper;
    
    public JsonStructureValidator() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Validates JSON structure.
     * 
     * @param jsonString The JSON string to validate
     * @param requiredFields Set of required field names
     * @return Validation result
     */
    public ValidationResult validate(String jsonString, Set<String> requiredFields) {
        ValidationResult result = new ValidationResult();
        
        if (jsonString == null || jsonString.trim().isEmpty()) {
            result.addError("json", "JSON string is null or empty");
            return result;
        }
        
        try {
            // Parse JSON
            JsonNode rootNode = objectMapper.readTree(jsonString);
            
            if (!rootNode.isObject()) {
                result.addError("json", "Root element must be a JSON object");
                return result;
            }
            
            // Check required fields
            if (requiredFields != null) {
                for (String field : requiredFields) {
                    if (!rootNode.has(field)) {
                        result.addError(field, "Required field is missing: " + field);
                    }
                }
            }
            
            // Validate agent field if present
            if (rootNode.has("agent")) {
                JsonNode agentNode = rootNode.get("agent");
                if (!agentNode.isTextual()) {
                    result.addError("agent", "Field 'agent' must be a string");
                }
            }
            
            // Validate timestamp field if present
            if (rootNode.has("timestamp")) {
                JsonNode timestampNode = rootNode.get("timestamp");
                if (!timestampNode.isTextual()) {
                    result.addError("timestamp", "Field 'timestamp' must be a string (ISO8601)");
                }
            }
            
            log.debug("JSON structure validation completed: {}", result.isValid());
            
        } catch (Exception e) {
            log.error("JSON parsing error", e);
            result.addError("json", "Invalid JSON syntax: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Validates JSON structure for agent-specific schemas.
     * 
     * @param jsonString The JSON string to validate
     * @param agentName The agent name to determine schema
     * @return Validation result
     */
    public ValidationResult validateAgentOutput(String jsonString, String agentName) {
        ValidationResult result = new ValidationResult();
        
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            
            // Common required fields for all agents
            Set<String> commonFields = Set.of("agent", "timestamp");
            
            // Agent-specific required fields
            Set<String> agentFields = getRequiredFieldsForAgent(agentName);
            Set<String> allRequiredFields = new java.util.HashSet<>(commonFields);
            allRequiredFields.addAll(agentFields);
            
            // Validate common structure
            ValidationResult commonResult = validate(jsonString, allRequiredFields);
            result.getErrors().addAll(commonResult.getErrors());
            result.getWarnings().addAll(commonResult.getWarnings());
            
            // Agent-specific validation
            validateAgentSpecificFields(rootNode, agentName, result);
            
        } catch (Exception e) {
            log.error("Error validating agent output", e);
            result.addError("json", "Validation error: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Gets required fields for a specific agent.
     */
    private Set<String> getRequiredFieldsForAgent(String agentName) {
        return switch (agentName.toLowerCase()) {
            case "cto_agent", "supervisor_agent" -> Set.of("decision_type", "review_summary", "technical_assessment");
            case "architect_agent" -> Set.of("architecture_proposal", "compatibility_check");
            case "engineer_agent" -> Set.of("implementation_spec", "code_structure");
            case "devops_agent" -> Set.of("infrastructure_analysis", "recommendations");
            case "fullstack_agent" -> Set.of("implementation_suggestion", "integration_specs");
            case "product_manager_agent" -> Set.of("product_spec", "roadmap");
            case "qa_agent" -> Set.of("test_plan", "test_coverage");
            default -> Set.of();
        };
    }
    
    /**
     * Validates agent-specific fields.
     */
    private void validateAgentSpecificFields(JsonNode rootNode, String agentName, ValidationResult result) {
        switch (agentName.toLowerCase()) {
            case "cto_agent", "supervisor_agent":
                validateCTOFields(rootNode, result);
                break;
            case "architect_agent":
                validateArchitectFields(rootNode, result);
                break;
            case "engineer_agent":
                validateEngineerFields(rootNode, result);
                break;
            case "devops_agent":
                validateDevOpsFields(rootNode, result);
                break;
            case "fullstack_agent":
                validateFullStackFields(rootNode, result);
                break;
            case "product_manager_agent":
                validateProductManagerFields(rootNode, result);
                break;
            case "qa_agent":
                validateQAFields(rootNode, result);
                break;
        }
    }
    
    private void validateCTOFields(JsonNode rootNode, ValidationResult result) {
        if (rootNode.has("technical_assessment")) {
            JsonNode assessment = rootNode.get("technical_assessment");
            if (!assessment.isObject()) {
                result.addError("technical_assessment", "Must be an object");
            } else {
                if (!assessment.has("risk_level")) {
                    result.addError("technical_assessment.risk_level", "Required field missing");
                }
                if (!assessment.has("compliance_check")) {
                    result.addError("technical_assessment.compliance_check", "Required field missing");
                }
            }
        }
    }
    
    private void validateArchitectFields(JsonNode rootNode, ValidationResult result) {
        if (rootNode.has("architecture_proposal")) {
            JsonNode proposal = rootNode.get("architecture_proposal");
            if (!proposal.isObject()) {
                result.addError("architecture_proposal", "Must be an object");
            }
        }
        if (rootNode.has("compatibility_check")) {
            JsonNode check = rootNode.get("compatibility_check");
            if (check.isObject() && check.has("breaking_changes")) {
                JsonNode breakingChanges = check.get("breaking_changes");
                if (breakingChanges.isBoolean() && breakingChanges.asBoolean()) {
                    result.addError("compatibility_check.breaking_changes", 
                        "Breaking changes detected - violates zero-impact mode");
                }
            }
        }
    }
    
    private void validateEngineerFields(JsonNode rootNode, ValidationResult result) {
        if (rootNode.has("implementation_spec")) {
            JsonNode spec = rootNode.get("implementation_spec");
            if (!spec.isObject()) {
                result.addError("implementation_spec", "Must be an object");
            }
        }
    }
    
    private void validateDevOpsFields(JsonNode rootNode, ValidationResult result) {
        if (rootNode.has("infrastructure_analysis")) {
            JsonNode analysis = rootNode.get("infrastructure_analysis");
            if (!analysis.isObject()) {
                result.addError("infrastructure_analysis", "Must be an object");
            }
        }
    }
    
    private void validateFullStackFields(JsonNode rootNode, ValidationResult result) {
        if (rootNode.has("implementation_suggestion")) {
            JsonNode suggestion = rootNode.get("implementation_suggestion");
            if (!suggestion.isObject()) {
                result.addError("implementation_suggestion", "Must be an object");
            }
        }
    }
    
    private void validateProductManagerFields(JsonNode rootNode, ValidationResult result) {
        if (rootNode.has("product_spec")) {
            JsonNode spec = rootNode.get("product_spec");
            if (!spec.isObject()) {
                result.addError("product_spec", "Must be an object");
            }
        }
    }
    
    private void validateQAFields(JsonNode rootNode, ValidationResult result) {
        if (rootNode.has("test_plan")) {
            JsonNode plan = rootNode.get("test_plan");
            if (!plan.isObject()) {
                result.addError("test_plan", "Must be an object");
            }
        }
    }
}



