package com.ai.company.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a validation operation.
 * 
 * Contains:
 * - Validation status (valid/invalid)
 * - List of validation errors
 * - List of validation warnings
 * - Compliance checks
 */
public class ValidationResult {
    
    private boolean valid;
    private final List<ValidationError> errors;
    private final List<ValidationWarning> warnings;
    private ComplianceCheck complianceCheck;
    
    public ValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    /**
     * Creates a validation result with initial status.
     */
    public ValidationResult(boolean valid) {
        this.valid = valid;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    // Getters
    public boolean isValid() {
        return valid && errors.isEmpty();
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public List<ValidationError> getErrors() {
        return errors;
    }
    
    public List<ValidationWarning> getWarnings() {
        return warnings;
    }
    
    public ComplianceCheck getComplianceCheck() {
        return complianceCheck;
    }
    
    public void setComplianceCheck(ComplianceCheck complianceCheck) {
        this.complianceCheck = complianceCheck;
    }
    
    /**
     * Adds a validation error.
     */
    public void addError(String field, String message) {
        this.valid = false;
        errors.add(new ValidationError(field, message));
    }
    
    /**
     * Adds a validation warning.
     */
    public void addWarning(String field, String message) {
        warnings.add(new ValidationWarning(field, message));
    }
    
    /**
     * Checks if there are any errors.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Checks if there are any warnings.
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * Gets a summary message.
     */
    public String getSummary() {
        if (isValid()) {
            return String.format("Validation passed. Warnings: %d", warnings.size());
        } else {
            return String.format("Validation failed. Errors: %d, Warnings: %d", 
                errors.size(), warnings.size());
        }
    }
    
    /**
     * Represents a validation error.
     */
    public static class ValidationError {
        private final String field;
        private final String message;
        
        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }
        
        public String getField() {
            return field;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("Error[field=%s, message=%s]", field, message);
        }
    }
    
    /**
     * Represents a validation warning.
     */
    public static class ValidationWarning {
        private final String field;
        private final String message;
        
        public ValidationWarning(String field, String message) {
            this.field = field;
            this.message = message;
        }
        
        public String getField() {
            return field;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("Warning[field=%s, message=%s]", field, message);
        }
    }
    
    /**
     * Compliance check results.
     */
    public static class ComplianceCheck {
        private boolean zeroImpactCompliant;
        private boolean noCodeModifications;
        private boolean structuredOutput;
        private String complianceMessage;
        
        public ComplianceCheck() {
            this.zeroImpactCompliant = true;
            this.noCodeModifications = true;
            this.structuredOutput = true;
        }
        
        // Getters and Setters
        public boolean isZeroImpactCompliant() {
            return zeroImpactCompliant;
        }
        
        public void setZeroImpactCompliant(boolean zeroImpactCompliant) {
            this.zeroImpactCompliant = zeroImpactCompliant;
        }
        
        public boolean isNoCodeModifications() {
            return noCodeModifications;
        }
        
        public void setNoCodeModifications(boolean noCodeModifications) {
            this.noCodeModifications = noCodeModifications;
        }
        
        public boolean isStructuredOutput() {
            return structuredOutput;
        }
        
        public void setStructuredOutput(boolean structuredOutput) {
            this.structuredOutput = structuredOutput;
        }
        
        public String getComplianceMessage() {
            return complianceMessage;
        }
        
        public void setComplianceMessage(String complianceMessage) {
            this.complianceMessage = complianceMessage;
        }
        
        /**
         * Checks if all compliance checks pass.
         */
        public boolean isCompliant() {
            return zeroImpactCompliant && noCodeModifications && structuredOutput;
        }
    }
}



