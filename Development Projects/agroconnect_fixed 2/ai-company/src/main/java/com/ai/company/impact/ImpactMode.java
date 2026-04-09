package com.ai.company.impact;

/**
 * Impact Mode Enumeration
 * 
 * Defines the three levels of impact allowed for code modifications:
 * 
 * - ZERO_IMPACT: No code modifications allowed (read-only mode)
 * - CONTROLLED_IMPACT: Limited modifications with strict validation
 * - FULL_IMPACT: Unrestricted modifications (still logged and monitored)
 * 
 * The impact mode determines what operations are permitted by code-writing tools.
 */
public enum ImpactMode {
    
    /**
     * Zero Impact Mode (Default)
     * 
     * - All code modifications are blocked
     * - Only read operations are allowed
     * - All write/modify/commit tools are disabled
     * - This is the safest mode and default state
     */
    ZERO_IMPACT("Zero Impact", "No code modifications allowed. Read-only operations only."),
    
    /**
     * Controlled Impact Mode
     * 
     * - Limited code modifications allowed
     * - Requires patch diff validation
     * - Requires tests to pass before changes
     * - Requires Supervisor approval for all changes
     * - Only file modifications (no infrastructure changes)
     */
    CONTROLLED_IMPACT("Controlled Impact", 
        "Limited modifications allowed with strict validation: patch diff required, tests must pass, Supervisor approval required."),
    
    /**
     * Full Impact Mode
     * 
     * - Unrestricted code modifications
     * - All operations allowed
     * - Still requires Supervisor approval
     * - All changes are logged and audited
     * - Use with extreme caution
     */
    FULL_IMPACT("Full Impact", 
        "Unrestricted modifications allowed. Supervisor approval and full audit logging still required.");
    
    private final String displayName;
    private final String description;
    
    ImpactMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if code modifications are allowed in this mode.
     */
    public boolean allowsModifications() {
        return this != ZERO_IMPACT;
    }
    
    /**
     * Checks if patch diff validation is required.
     */
    public boolean requiresPatchValidation() {
        return this == CONTROLLED_IMPACT;
    }
    
    /**
     * Checks if test validation is required.
     */
    public boolean requiresTestValidation() {
        return this == CONTROLLED_IMPACT;
    }
    
    /**
     * Checks if Supervisor approval is required.
     */
    public boolean requiresSupervisorApproval() {
        return this != ZERO_IMPACT; // All modification modes require approval
    }
}



