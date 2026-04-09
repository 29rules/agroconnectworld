package com.ai.company.tools.code;

import com.ai.company.impact.ImpactGuard;
import dev.langchain4j.agent.tool.Tool;
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
 * Code Formatter Tool - Format code files using standard formatters.
 * 
 * This tool formats code files:
 * - Java files: Google Java Format
 * - JS/TS/React files: Prettier (via CLI)
 * 
 * SAFETY:
 * - Only formats files in /backend or /frontend
 * - Requires Impact Mode that allows modifications
 * - Emits warnings for unauthorized paths
 */
public class CodeFormatterTool {
    
    private static final Logger log = LoggerFactory.getLogger(CodeFormatterTool.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    private static final Path BACKEND_DIR = WORKSPACE_ROOT.resolve("backend").normalize();
    private static final Path FRONTEND_DIR = WORKSPACE_ROOT.resolve("frontend").normalize();
    
    private final ImpactGuard impactGuard;
    
    public CodeFormatterTool() {
        this.impactGuard = new ImpactGuard();
    }
    
    /**
     * Formats a Java file using Google Java Format.
     * 
     * @param filePath Relative path from workspace root
     * @return Success message or error
     */
    @Tool("Format a Java file using Google Java Format. Requires Impact Mode that allows modifications.")
    public String formatJavaFile(String filePath) {
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("FORMAT", filePath);
        if (!validation.isAllowed()) {
            return "ERROR: " + validation.getMessage();
        }
        
        try {
            Path fullPath = WORKSPACE_ROOT.resolve(filePath).normalize();
            
            if (!isPathPermitted(fullPath)) {
                return String.format(
                    "WARNING: File path '%s' is outside permitted directories (backend/, frontend/). Formatting denied.",
                    filePath
                );
            }
            
            if (!filePath.endsWith(".java")) {
                return "ERROR: File is not a Java file (.java extension required)";
            }
            
            // Check if google-java-format is available
            ProcessBuilder pb = new ProcessBuilder("which", "google-java-format");
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                return "ERROR: google-java-format not found. Please install it: " +
                       "https://github.com/google/google-java-format";
            }
            
            // Format the file
            pb = new ProcessBuilder("google-java-format", "-i", fullPath.toString());
            process = pb.start();
            exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log.info("Java file formatted: {}", filePath);
                return "SUCCESS: Java file formatted: " + filePath;
            } else {
                // Read error output
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                    String error = reader.lines().collect(java.util.stream.Collectors.joining("\n"));
                    return "ERROR: Formatting failed: " + error;
                }
            }
            
        } catch (IOException | InterruptedException e) {
            log.error("Error formatting Java file: {}", filePath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Formats JS/TS/React files using Prettier.
     * 
     * @param filePath Relative path from workspace root
     * @return Success message or error
     */
    @Tool("Format a JS/TS/React file using Prettier. Requires Impact Mode that allows modifications.")
    public String formatJsFile(String filePath) {
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("FORMAT", filePath);
        if (!validation.isAllowed()) {
            return "ERROR: " + validation.getMessage();
        }
        
        try {
            Path fullPath = WORKSPACE_ROOT.resolve(filePath).normalize();
            
            if (!isPathPermitted(fullPath)) {
                return String.format(
                    "WARNING: File path '%s' is outside permitted directories (backend/, frontend/). Formatting denied.",
                    filePath
                );
            }
            
            // Check if file is JS/TS/JSX/TSX
            String lowerPath = filePath.toLowerCase();
            if (!lowerPath.endsWith(".js") && !lowerPath.endsWith(".ts") && 
                !lowerPath.endsWith(".jsx") && !lowerPath.endsWith(".tsx")) {
                return "ERROR: File is not a JS/TS/React file (.js, .ts, .jsx, .tsx extension required)";
            }
            
            // Check if prettier is available
            ProcessBuilder pb = new ProcessBuilder("which", "prettier");
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                return "ERROR: prettier not found. Please install it: npm install -g prettier";
            }
            
            // Format the file
            pb = new ProcessBuilder("prettier", "--write", fullPath.toString());
            process = pb.start();
            exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log.info("JS/TS file formatted: {}", filePath);
                return "SUCCESS: JS/TS file formatted: " + filePath;
            } else {
                // Read error output
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                    String error = reader.lines().collect(java.util.stream.Collectors.joining("\n"));
                    return "ERROR: Formatting failed: " + error;
                }
            }
            
        } catch (IOException | InterruptedException e) {
            log.error("Error formatting JS/TS file: {}", filePath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Checks if a path is within permitted directories.
     */
    private boolean isPathPermitted(Path path) {
        return path.startsWith(BACKEND_DIR) || path.startsWith(FRONTEND_DIR);
    }
}

