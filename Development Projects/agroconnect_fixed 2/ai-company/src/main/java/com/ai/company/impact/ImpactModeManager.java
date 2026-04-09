package com.ai.company.impact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Impact Mode Manager
 * 
 * Centralized manager for controlling the impact mode of the AI Company system.
 * 
 * Features:
 * - Singleton pattern for global state
 * - Only SupervisorAgent can change mode
 * - Default mode: ZERO_IMPACT
 * - Thread-safe mode changes
 * - Audit logging of mode changes
 * 
 * SAFETY: This manager ensures that impact mode can only be changed by
 * authorized agents (SupervisorAgent), preventing unauthorized escalation.
 */
public class ImpactModeManager {
    
    private static final Logger log = LoggerFactory.getLogger(ImpactModeManager.class);
    
    private static volatile ImpactModeManager instance;
    private static final ReentrantLock lock = new ReentrantLock();
    
    private volatile ImpactMode currentMode = ImpactMode.ZERO_IMPACT;
    private String lastChangedBy = "SYSTEM";
    private long lastChangedAt = System.currentTimeMillis();
    
    /**
     * Private constructor for singleton pattern.
     */
    private ImpactModeManager() {
        log.info("ImpactModeManager initialized with default mode: {}", currentMode);
    }
    
    /**
     * Gets the singleton instance.
     */
    public static ImpactModeManager getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new ImpactModeManager();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }
    
    /**
     * Gets the current impact mode.
     * 
     * @return Current ImpactMode
     */
    public ImpactMode getMode() {
        return currentMode;
    }
    
    /**
     * Sets the impact mode.
     * 
     * SECURITY: Only SupervisorAgent can change the mode.
     * This is enforced by checking the caller's class name.
     * 
     * @param newMode The new impact mode
     * @param requestedBy The agent requesting the change (must be SupervisorAgent)
     * @return true if mode was changed, false if unauthorized
     */
    public boolean setMode(ImpactMode newMode, String requestedBy) {
        lock.lock();
        try {
            // Security check: Only SupervisorAgent can change mode
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            boolean authorized = false;
            
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                if (className.contains("SupervisorAgent") || 
                    className.contains("supervisor") ||
                    requestedBy != null && requestedBy.contains("Supervisor")) {
                    authorized = true;
                    break;
                }
            }
            
            if (!authorized) {
                log.warn("Unauthorized attempt to change impact mode from {} to {} by {}", 
                    currentMode, newMode, requestedBy);
                return false;
            }
            
            if (currentMode == newMode) {
                log.debug("Impact mode already set to: {}", newMode);
                return true;
            }
            
            ImpactMode oldMode = currentMode;
            currentMode = newMode;
            lastChangedBy = requestedBy != null ? requestedBy : "SupervisorAgent";
            lastChangedAt = System.currentTimeMillis();
            
            log.warn("IMPACT MODE CHANGED: {} -> {} by {}", oldMode, newMode, lastChangedBy);
            log.warn("New mode description: {}", newMode.getDescription());
            
            return true;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Gets information about the last mode change.
     * 
     * @return String with last change information
     */
    public String getLastChangeInfo() {
        return String.format("Mode: %s | Changed by: %s | Changed at: %d", 
            currentMode, lastChangedBy, lastChangedAt);
    }
    
    /**
     * Checks if the current mode allows modifications.
     * 
     * @return true if modifications are allowed
     */
    public boolean allowsModifications() {
        return currentMode.allowsModifications();
    }
    
    /**
     * Checks if patch validation is required.
     * 
     * @return true if patch validation is required
     */
    public boolean requiresPatchValidation() {
        return currentMode.requiresPatchValidation();
    }
    
    /**
     * Checks if test validation is required.
     * 
     * @return true if test validation is required
     */
    public boolean requiresTestValidation() {
        return currentMode.requiresTestValidation();
    }
    
    /**
     * Checks if Supervisor approval is required.
     * 
     * @return true if Supervisor approval is required
     */
    public boolean requiresSupervisorApproval() {
        return currentMode.requiresSupervisorApproval();
    }
    
    /**
     * Resets to ZERO_IMPACT mode.
     * Only SupervisorAgent can call this.
     * 
     * @param requestedBy The agent requesting the reset
     * @return true if reset successful
     */
    public boolean resetToZeroImpact(String requestedBy) {
        return setMode(ImpactMode.ZERO_IMPACT, requestedBy);
    }
}



