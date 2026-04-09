package com.ai.company.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Performs semantic validation on agent outputs.
 * 
 * Validates:
 * - Zero-impact mode compliance
 * - No code modification attempts
 * - Structured output requirements
 * - Empty fields and missing sections
 * - Content quality checks
 */
public class AgentOutputValidator {
    
    private static final Logger log = LoggerFactory.getLogger(AgentOutputValidator.class);
    
    private final ObjectMapper objectMapper;
    private final JsonStructureValidator structureValidator;
    
    // Patterns that indicate code modification attempts
    private static final List<Pattern> CODE_MODIFICATION_PATTERNS = List.of(
        Pattern.compile("(?i).*\\b(modify|change|update|edit|delete|remove|alter)\\s+(existing|production|current)\\s+(code|file|class|method|service|config).*"),
        Pattern.compile("(?i).*\\b(commit|push|deploy|migrate|schema|database)\\s+(change|modification|update).*"),
        Pattern.compile("(?i).*\\b(rewrite|refactor|replace)\\s+(existing|production).*"),
        Pattern.compile("(?i).*\\b(docker|nginx|postgres|redis)\\s+(config|modify|change).*")
    );
    
    // Patterns that indicate zero-impact violations
    private static final List<Pattern> ZERO_IMPACT_VIOLATION_PATTERNS = List.of(
        Pattern.compile("(?i).*\\b(breaking\\s+change|backward\\s+incompatible|migration\\s+required).*"),
        Pattern.compile("(?i).*\\b(modify|change)\\s+(microservice|backend|frontend|database).*"),
        Pattern.compile("(?i).*\\b(update|alter)\\s+(existing|production)\\s+(api|endpoint|contract).*")
    );
    
    public AgentOutputValidator() {
        this.objectMapper = new ObjectMapper();
        this.structureValidator = new JsonStructureValidator();
    }
    
    /**
     * Validates agent output comprehensively.
     * 
     * @param output The agent output (JSON string)
     * @param agentName The agent name
     * @return Validation result
     */
    public ValidationResult validate(String output, String agentName) {
        ValidationResult result = new ValidationResult();
        
        if (output == null || output.trim().isEmpty()) {
            result.addError("output", "Agent output is null or empty");
            return result;
        }
        
        // Step 1: JSON structure validation
        ValidationResult structureResult = structureValidator.validateAgentOutput(output, agentName);
        result.getErrors().addAll(structureResult.getErrors());
        result.getWarnings().addAll(structureResult.getWarnings());
        
        // Step 2: Semantic validation
        validateSemantics(output, agentName, result);
        
        // Step 3: Zero-impact compliance check
        validateZeroImpactCompliance(output, result);
        
        // Step 4: Code modification check
        validateNoCodeModifications(output, result);
        
        // Step 5: Structured output check
        validateStructuredOutput(output, agentName, result);
        
        // Step 6: Content quality checks
        validateContentQuality(output, agentName, result);
        
        // Build compliance check summary
        ValidationResult.ComplianceCheck compliance = new ValidationResult.ComplianceCheck();
        compliance.setZeroImpactCompliant(!hasZeroImpactViolations(result));
        compliance.setNoCodeModifications(!hasCodeModificationAttempts(result));
        compliance.setStructuredOutput(structureResult.isValid());
        compliance.setComplianceMessage(buildComplianceMessage(result));
        result.setComplianceCheck(compliance);
        
        // Final validation status
        result.setValid(result.isValid() && compliance.isCompliant());
        
        log.debug("Agent output validation completed for {}: {}", agentName, result.isValid());
        
        return result;
    }
    
    /**
     * Validates semantic content.
     */
    private void validateSemantics(String output, String agentName, ValidationResult result) {
        try {
            JsonNode rootNode = objectMapper.readTree(output);
            
            // Check for empty required fields
            checkEmptyFields(rootNode, agentName, result);
            
            // Check for missing sections
            checkMissingSections(rootNode, agentName, result);
            
        } catch (Exception e) {
            log.warn("Could not parse JSON for semantic validation", e);
            // Continue with text-based validation
        }
        
        // Text-based semantic checks
        if (output.trim().length() < 50) {
            result.addWarning("output", "Output seems too short, may be incomplete");
        }
        
        if (output.contains("TODO") || output.contains("FIXME")) {
            result.addWarning("output", "Output contains TODO/FIXME markers");
        }
    }
    
    /**
     * Validates zero-impact mode compliance.
     */
    private void validateZeroImpactCompliance(String output, ValidationResult result) {
        String lowerOutput = output.toLowerCase();
        
        for (Pattern pattern : ZERO_IMPACT_VIOLATION_PATTERNS) {
            if (pattern.matcher(lowerOutput).find()) {
                result.addError("zero_impact_compliance", 
                    "Output suggests zero-impact mode violation: " + pattern.pattern());
            }
        }
        
        // Check for explicit zero-impact statements
        if (!lowerOutput.contains("zero-impact") && !lowerOutput.contains("specification") && 
            !lowerOutput.contains("read-only")) {
            result.addWarning("zero_impact_compliance", 
                "Output does not explicitly mention zero-impact mode or specification-only");
        }
    }
    
    /**
     * Validates that output does not attempt code modifications.
     */
    private void validateNoCodeModifications(String output, ValidationResult result) {
        String lowerOutput = output.toLowerCase();
        
        for (Pattern pattern : CODE_MODIFICATION_PATTERNS) {
            if (pattern.matcher(lowerOutput).find()) {
                result.addError("code_modification", 
                    "Output suggests code modification attempt: " + pattern.pattern());
            }
        }
        
        // Check for forbidden keywords in context
        String[] forbiddenContexts = {
            "modify existing code",
            "change production",
            "update backend",
            "edit frontend",
            "delete file",
            "commit changes"
        };
        
        for (String context : forbiddenContexts) {
            if (lowerOutput.contains(context)) {
                result.addError("code_modification", 
                    "Output contains forbidden modification context: " + context);
            }
        }
    }
    
    /**
     * Validates structured output requirements.
     */
    private void validateStructuredOutput(String output, String agentName, ValidationResult result) {
        try {
            JsonNode rootNode = objectMapper.readTree(output);
            
            // Must have agent field
            if (!rootNode.has("agent")) {
                result.addError("structured_output", "Missing required 'agent' field");
            } else {
                String outputAgent = rootNode.get("agent").asText();
                if (!outputAgent.equalsIgnoreCase(agentName)) {
                    result.addWarning("structured_output", 
                        String.format("Agent name mismatch: expected %s, got %s", agentName, outputAgent));
                }
            }
            
            // Must have timestamp
            if (!rootNode.has("timestamp")) {
                result.addWarning("structured_output", "Missing 'timestamp' field");
            }
            
            // Check for proper JSON structure
            if (!rootNode.isObject()) {
                result.addError("structured_output", "Output must be a JSON object");
            }
            
        } catch (Exception e) {
            result.addError("structured_output", "Output is not valid JSON: " + e.getMessage());
        }
    }
    
    /**
     * Validates content quality.
     */
    private void validateContentQuality(String output, String agentName, ValidationResult result) {
        // Check for placeholder text
        if (output.contains("{{") || output.contains("TODO") || output.contains("FIXME")) {
            result.addWarning("content_quality", "Output contains placeholder or incomplete content");
        }
        
        // Check for minimum content length
        if (output.trim().length() < 100) {
            result.addWarning("content_quality", "Output seems too short for meaningful content");
        }
        
        // Check for agent-specific content requirements
        validateAgentSpecificContent(output, agentName, result);
    }
    
    /**
     * Validates agent-specific content requirements.
     */
    private void validateAgentSpecificContent(String output, String agentName, ValidationResult result) {
        String lowerOutput = output.toLowerCase();
        
        switch (agentName.toLowerCase()) {
            case "cto_agent", "supervisor_agent":
                if (!lowerOutput.contains("approval") && !lowerOutput.contains("review") && 
                    !lowerOutput.contains("decision")) {
                    result.addWarning("content_quality", "CTO output should contain approval/review/decision");
                }
                break;
                
            case "architect_agent":
                if (!lowerOutput.contains("architecture") && !lowerOutput.contains("design")) {
                    result.addWarning("content_quality", "Architect output should contain architecture/design");
                }
                break;
                
            case "engineer_agent":
                if (!lowerOutput.contains("implementation") && !lowerOutput.contains("spec")) {
                    result.addWarning("content_quality", "Engineer output should contain implementation specs");
                }
                break;
                
            case "qa_agent":
                if (!lowerOutput.contains("test") && !lowerOutput.contains("testing")) {
                    result.addWarning("content_quality", "QA output should contain test-related content");
                }
                break;
        }
    }
    
    /**
     * Checks for empty required fields.
     */
    private void checkEmptyFields(JsonNode rootNode, String agentName, ValidationResult result) {
        List<String> requiredFields = getRequiredFieldsForAgent(agentName);
        
        for (String field : requiredFields) {
            if (rootNode.has(field)) {
                JsonNode fieldNode = rootNode.get(field);
                if (fieldNode.isTextual() && fieldNode.asText().trim().isEmpty()) {
                    result.addError(field, "Required field is empty");
                } else if (fieldNode.isObject() && fieldNode.size() == 0) {
                    result.addError(field, "Required field object is empty");
                } else if (fieldNode.isArray() && fieldNode.size() == 0) {
                    result.addWarning(field, "Required field array is empty");
                }
            }
        }
    }
    
    /**
     * Checks for missing sections.
     */
    private void checkMissingSections(JsonNode rootNode, String agentName, ValidationResult result) {
        List<String> requiredSections = getRequiredSectionsForAgent(agentName);
        
        for (String section : requiredSections) {
            if (!rootNode.has(section)) {
                result.addWarning(section, "Recommended section is missing: " + section);
            }
        }
    }
    
    /**
     * Gets required fields for an agent.
     */
    private List<String> getRequiredFieldsForAgent(String agentName) {
        return switch (agentName.toLowerCase()) {
            case "cto_agent", "supervisor_agent" -> List.of("decision_type", "review_summary");
            case "architect_agent" -> List.of("architecture_proposal");
            case "engineer_agent" -> List.of("implementation_spec");
            case "devops_agent" -> List.of("infrastructure_analysis");
            case "fullstack_agent" -> List.of("implementation_suggestion");
            case "product_manager_agent" -> List.of("product_spec");
            case "qa_agent" -> List.of("test_plan");
            default -> List.of();
        };
    }
    
    /**
     * Gets recommended sections for an agent.
     */
    private List<String> getRequiredSectionsForAgent(String agentName) {
        return switch (agentName.toLowerCase()) {
            case "cto_agent", "supervisor_agent" -> List.of("technical_assessment", "recommendations");
            case "architect_agent" -> List.of("diagrams", "compatibility_check");
            case "engineer_agent" -> List.of("code_structure", "testing_specs");
            case "devops_agent" -> List.of("recommendations", "diagrams");
            case "fullstack_agent" -> List.of("integration_specs");
            case "product_manager_agent" -> List.of("roadmap");
            case "qa_agent" -> List.of("test_coverage");
            default -> List.of();
        };
    }
    
    /**
     * Checks if result has zero-impact violations.
     */
    private boolean hasZeroImpactViolations(ValidationResult result) {
        return result.getErrors().stream()
            .anyMatch(e -> e.getField().contains("zero_impact") || 
                          e.getMessage().toLowerCase().contains("zero-impact"));
    }
    
    /**
     * Checks if result has code modification attempts.
     */
    private boolean hasCodeModificationAttempts(ValidationResult result) {
        return result.getErrors().stream()
            .anyMatch(e -> e.getField().contains("code_modification") ||
                          e.getMessage().toLowerCase().contains("modification"));
    }
    
    /**
     * Builds compliance message.
     */
    private String buildComplianceMessage(ValidationResult result) {
        List<String> messages = new ArrayList<>();
        
        ValidationResult.ComplianceCheck compliance = result.getComplianceCheck();
        if (compliance != null) {
            if (!compliance.isZeroImpactCompliant()) {
                messages.add("Zero-impact mode violation detected");
            }
            if (!compliance.isNoCodeModifications()) {
                messages.add("Code modification attempt detected");
            }
            if (!compliance.isStructuredOutput()) {
                messages.add("Structured output requirements not met");
            }
        }
        
        if (messages.isEmpty()) {
            return "All compliance checks passed";
        }
        
        return String.join("; ", messages);
    }
}



