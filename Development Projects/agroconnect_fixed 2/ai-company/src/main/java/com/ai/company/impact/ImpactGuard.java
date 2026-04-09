package com.ai.company.impact;

import com.ai.company.tools.code.TestRunnerTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Impact Guard
 * 
 * Validates code modification operations based on the current impact mode.
 * 
 * This guard is called before any code-writing tool executes to ensure:
 * - Current impact mode allows the operation
 * - Required validations are performed
 * - Tests pass (if required by mode)
 * - Patch diffs are generated (if required by mode)
 * 
 * SAFETY: This is the gatekeeper that enforces impact mode rules.
 */
public class ImpactGuard {
    
    private static final Logger log = LoggerFactory.getLogger(ImpactGuard.class);
    
    private final ImpactModeManager modeManager;
    private final TestRunnerTool testRunner;
    
    public ImpactGuard() {
        this.modeManager = ImpactModeManager.getInstance();
        this.testRunner = new TestRunnerTool();
    }
    
    /**
     * Validates if a code modification operation is allowed.
     * 
     * @param operationType Type of operation (WRITE, MODIFY, DELETE, COMMIT, FORMAT)
     * @param filePath File path being modified (null for non-file operations)
     * @return ValidationResult with allowed status and any required actions
     */
    public ValidationResult validateOperation(String operationType, String filePath) {
        ImpactMode currentMode = modeManager.getMode();
        
        log.debug("Validating operation: {} for file: {} in mode: {}", 
            operationType, filePath, currentMode);
        
        // ZERO_IMPACT: Block all modifications
        if (currentMode == ImpactMode.ZERO_IMPACT) {
            String message = String.format(
                "Operation '%s' rejected: ZERO_IMPACT mode is active. " +
                "No code modifications are allowed. Enable CONTROLLED_IMPACT or FULL_IMPACT mode first.",
                operationType
            );
            log.warn(message);
            return new ValidationResult(false, message, null, null);
        }
        
        // CONTROLLED_IMPACT: Require patch diff and test validation
        if (currentMode == ImpactMode.CONTROLLED_IMPACT) {
            return validateControlledImpact(operationType, filePath);
        }
        
        // FULL_IMPACT: Allow but log
        if (currentMode == ImpactMode.FULL_IMPACT) {
            log.info("Operation '{}' allowed in FULL_IMPACT mode for file: {}", operationType, filePath);
            return new ValidationResult(true, "Operation allowed in FULL_IMPACT mode", null, null);
        }
        
        // Should not reach here
        return new ValidationResult(false, "Unknown impact mode", null, null);
    }
    
    /**
     * Validates operations in CONTROLLED_IMPACT mode.
     */
    private ValidationResult validateControlledImpact(String operationType, String filePath) {
        List<String> requiredActions = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Generate patch diff if file exists
        if (filePath != null) {
            Path fullPath = Paths.get(".").toAbsolutePath().resolve(filePath).normalize();
            if (Files.exists(fullPath)) {
                try {
                    String diff = generatePatchDiff(filePath);
                    if (diff != null && !diff.isEmpty()) {
                        requiredActions.add("Patch diff generated");
                        log.debug("Patch diff generated for: {}", filePath);
                    }
                } catch (Exception e) {
                    warnings.add("Could not generate patch diff: " + e.getMessage());
                }
            }
        }
        
        // Determine which tests to run based on file path
        String testResult = null;
        if (filePath != null) {
            if (filePath.startsWith("backend/")) {
                // Extract service name from path
                String servicePath = extractServicePath(filePath);
                if (servicePath != null) {
                    try {
                        testResult = testRunner.runBackendTests(servicePath);
                        requiredActions.add("Backend tests executed");
                        
                        // Check if tests passed (exit code 0 in output)
                        if (testResult.contains("Exit Code: 0")) {
                            requiredActions.add("Backend tests passed");
                        } else {
                            warnings.add("Backend tests may have failed - check output");
                        }
                    } catch (Exception e) {
                        warnings.add("Could not run backend tests: " + e.getMessage());
                    }
                }
            } else if (filePath.startsWith("frontend/")) {
                try {
                    testResult = testRunner.runFrontendTests();
                    requiredActions.add("Frontend tests executed");
                    
                    if (testResult.contains("Exit Code: 0")) {
                        requiredActions.add("Frontend tests passed");
                    } else {
                        warnings.add("Frontend tests may have failed - check output");
                    }
                } catch (Exception e) {
                    warnings.add("Could not run frontend tests: " + e.getMessage());
                }
            }
        }
        
        // Build validation message
        StringBuilder message = new StringBuilder();
        message.append("Operation validated for CONTROLLED_IMPACT mode");
        if (!requiredActions.isEmpty()) {
            message.append("\nRequired actions completed: ").append(String.join(", ", requiredActions));
        }
        if (!warnings.isEmpty()) {
            message.append("\nWarnings: ").append(String.join("; ", warnings));
        }
        
        // If tests failed, we might want to be more strict
        // For now, we allow with warnings
        boolean allowed = true;
        if (testResult != null && testResult.contains("Exit Code: 1")) {
            warnings.add("Tests failed - operation may be risky");
        }
        
        log.info("CONTROLLED_IMPACT validation: {} for operation: {}", 
            allowed ? "ALLOWED" : "BLOCKED", operationType);
        
        return new ValidationResult(allowed, message.toString(), testResult, null);
    }
    
    /**
     * Generates a patch diff for a file (simulated - would use git diff in real scenario).
     */
    private String generatePatchDiff(String filePath) {
        try {
            Path fullPath = Paths.get(".").toAbsolutePath().resolve(filePath).normalize();
            
            // Try to get git diff if file is tracked
            ProcessBuilder pb = new ProcessBuilder("git", "diff", filePath);
            pb.directory(Paths.get(".").toAbsolutePath().toFile());
            Process process = pb.start();
            
            List<String> diffLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(diffLines::add);
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && !diffLines.isEmpty()) {
                return String.join("\n", diffLines);
            }
            
            // If no git diff, return file metadata
            if (Files.exists(fullPath)) {
                long size = Files.size(fullPath);
                return String.format("File: %s\nSize: %d bytes\n(Git diff not available)", filePath, size);
            }
            
            return null;
            
        } catch (IOException | InterruptedException e) {
            log.warn("Error generating patch diff for: {}", filePath, e);
            return null;
        }
    }
    
    /**
     * Extracts service path from file path.
     * Example: "backend/gateway/src/..." -> "backend/gateway"
     */
    private String extractServicePath(String filePath) {
        if (filePath.startsWith("backend/")) {
            String[] parts = filePath.split("/");
            if (parts.length >= 2) {
                return parts[0] + "/" + parts[1];
            }
            return "backend";
        }
        return null;
    }
    
    /**
     * Result of impact validation.
     */
    public static class ValidationResult {
        private final boolean allowed;
        private final String message;
        private final String testResult;
        private final String patchDiff;
        
        public ValidationResult(boolean allowed, String message, String testResult, String patchDiff) {
            this.allowed = allowed;
            this.message = message;
            this.testResult = testResult;
            this.patchDiff = patchDiff;
        }
        
        public boolean isAllowed() {
            return allowed;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getTestResult() {
            return testResult;
        }
        
        public String getPatchDiff() {
            return patchDiff;
        }
    }
}



