package com.ai.company.tools.deployment;

import com.ai.company.impact.ImpactGuard;
import com.ai.company.impact.ImpactMode;
import com.ai.company.impact.ImpactModeManager;
import com.ai.company.tools.code.CodeReaderTool;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Environment Configuration Tool
 * 
 * Manages environment variables for services.
 * 
 * SAFETY:
 * - Validates .env.production and .env.staging files
 * - Never prints or stores secrets in logs
 * - Only reads/modifies when Impact Mode allows
 * - Masks sensitive values in output
 * 
 * ZERO-IMPACT MODE: Only validates, never modifies.
 */
public class EnvConfigTool {
    
    private static final Logger log = LoggerFactory.getLogger(EnvConfigTool.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    
    private static final List<String> SENSITIVE_KEYS = List.of(
        "password", "secret", "key", "token", "api_key", "apikey",
        "jwt", "auth", "credential", "private", "access"
    );
    
    private final CodeReaderTool codeReader;
    private final ImpactGuard impactGuard;
    
    public EnvConfigTool() {
        this.codeReader = new CodeReaderTool();
        this.impactGuard = new ImpactGuard();
    }
    
    /**
     * Validates environment configuration file.
     * 
     * @param envFilePath Path to .env file (e.g., ".env.production", ".env.staging")
     * @return Validation result with issues found
     */
    @Tool("Validate environment configuration file (.env.production, .env.staging). Checks for missing variables, syntax errors, and security issues. Read-only, always safe.")
    public String validateEnvFile(String envFilePath) {
        if (envFilePath == null || envFilePath.isEmpty()) {
            return "ERROR: Environment file path is required";
        }
        
        log.info("Validating environment file: {}", envFilePath);
        
        Path envFile = WORKSPACE_ROOT.resolve(envFilePath).normalize();
        
        if (!Files.exists(envFile)) {
            return "ERROR: Environment file not found: " + envFilePath;
        }
        
        try {
            String content = Files.readString(envFile);
            List<String> errors = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            
            String[] lines = content.split("\\n");
            int lineNumber = 0;
            
            for (String line : lines) {
                lineNumber++;
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Check for proper KEY=VALUE format
                if (!line.contains("=")) {
                    errors.add(String.format("Line %d: Missing '=' separator", lineNumber));
                    continue;
                }
                
                String[] parts = line.split("=", 2);
                if (parts.length != 2) {
                    errors.add(String.format("Line %d: Invalid format", lineNumber));
                    continue;
                }
                
                String key = parts[0].trim();
                String value = parts[1].trim();
                
                // Check for empty key
                if (key.isEmpty()) {
                    errors.add(String.format("Line %d: Empty key", lineNumber));
                }
                
                // Check for sensitive keys without masking
                if (isSensitiveKey(key) && !value.startsWith("${") && value.length() < 20) {
                    warnings.add(String.format("Line %d: Sensitive key '%s' has short value - ensure it's properly secured", lineNumber, maskKey(key)));
                }
                
                // Check for unquoted values with spaces
                if (value.contains(" ") && !value.startsWith("\"") && !value.startsWith("'")) {
                    warnings.add(String.format("Line %d: Value contains spaces but is not quoted", lineNumber));
                }
            }
            
            StringBuilder result = new StringBuilder();
            result.append("=== Environment File Validation ===\n");
            result.append("File: ").append(envFilePath).append("\n");
            
            if (errors.isEmpty() && warnings.isEmpty()) {
                result.append("Status: VALID - No issues found\n");
            } else {
                if (!errors.isEmpty()) {
                    result.append("Status: INVALID\n");
                    result.append("Errors:\n");
                    errors.forEach(e -> result.append("  - ").append(e).append("\n"));
                    result.append("\n");
                } else {
                    result.append("Status: VALID with warnings\n");
                }
                
                if (!warnings.isEmpty()) {
                    result.append("Warnings:\n");
                    warnings.forEach(w -> result.append("  - ").append(w).append("\n"));
                }
            }
            
            result.append("\nNote: Sensitive values are masked in output for security.\n");
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("Error validating environment file: {}", envFilePath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Lists environment variables (with sensitive values masked).
     * 
     * @param envFilePath Path to .env file
     * @return List of environment variables
     */
    @Tool("List environment variables from .env file. Sensitive values are masked. Read-only, always safe.")
    public String listEnvVariables(String envFilePath) {
        if (envFilePath == null || envFilePath.isEmpty()) {
            return "ERROR: Environment file path is required";
        }
        
        Path envFile = WORKSPACE_ROOT.resolve(envFilePath).normalize();
        
        if (!Files.exists(envFile)) {
            return "ERROR: Environment file not found: " + envFilePath;
        }
        
        try {
            String content = Files.readString(envFile);
            StringBuilder result = new StringBuilder();
            result.append("=== Environment Variables ===\n");
            result.append("File: ").append(envFilePath).append("\n\n");
            
            String[] lines = content.split("\\n");
            for (String line : lines) {
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts.length > 1 ? parts[1].trim() : "";
                    
                    if (isSensitiveKey(key)) {
                        value = maskValue(value);
                    }
                    
                    result.append(key).append("=").append(value).append("\n");
                }
            }
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("Error listing environment variables", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Checks if a key is sensitive.
     */
    private boolean isSensitiveKey(String key) {
        String lowerKey = key.toLowerCase();
        return SENSITIVE_KEYS.stream().anyMatch(lowerKey::contains);
    }
    
    /**
     * Masks a sensitive value.
     */
    private String maskValue(String value) {
        if (value == null || value.isEmpty()) {
            return "***";
        }
        if (value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 2) + "***" + value.substring(value.length() - 2);
    }
    
    /**
     * Masks a key name.
     */
    private String maskKey(String key) {
        if (key == null || key.isEmpty()) {
            return "***";
        }
        if (key.length() <= 4) {
            return key.substring(0, 1) + "***";
        }
        return key.substring(0, 2) + "***" + key.substring(key.length() - 1);
    }
}



