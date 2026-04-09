package com.ai.company.tools.code;

import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.impact.ImpactGuard;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Code Modifier Tool - Apply patch-style changes with validation.
 * 
 * This tool allows modifying existing files using patch-style operations:
 * - Insert blocks at specific line numbers
 * - Replace text blocks
 * - Delete text blocks
 * 
 * SAFETY:
 * - Requires ImpactGuard validation
 * - Requires Supervisor approval
 * - Runs change validation before applying
 * - Only modifies files in /backend or /frontend
 * - Respects Impact Mode rules
 */
public class CodeModifierTool {
    
    private static final Logger log = LoggerFactory.getLogger(CodeModifierTool.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    private static final Path BACKEND_DIR = WORKSPACE_ROOT.resolve("backend").normalize();
    private static final Path FRONTEND_DIR = WORKSPACE_ROOT.resolve("frontend").normalize();
    
    private final SupervisorAgent supervisorAgent;
    private final ImpactGuard impactGuard;
    
    public CodeModifierTool(ChatLanguageModel chatModel) {
        this.supervisorAgent = new SupervisorAgent(chatModel);
        this.impactGuard = new ImpactGuard();
    }
    
    /**
     * Inserts text at a specific line number in a file.
     * 
     * @param filePath Relative path from workspace root
     * @param lineNumber Line number to insert at (1-based)
     * @param text Text to insert
     * @param sessionId Session ID for Supervisor approval
     * @return Success message or error
     */
    @Tool("Insert text at a specific line number in a file. Requires Supervisor approval and Controlled-Impact Mode ON.")
    public String insertAtLine(String filePath, int lineNumber, String text, String sessionId) {
        return applyModification(filePath, "INSERT", lineNumber, lineNumber, text, null, sessionId);
    }
    
    /**
     * Replaces text between two line numbers.
     * 
     * @param filePath Relative path from workspace root
     * @param startLine Start line number (1-based, inclusive)
     * @param endLine End line number (1-based, inclusive)
     * @param newText Replacement text
     * @param sessionId Session ID for Supervisor approval
     * @return Success message or error
     */
    @Tool("Replace text between line numbers in a file. Requires Supervisor approval and Controlled-Impact Mode ON.")
    public String replaceLines(String filePath, int startLine, int endLine, String newText, String sessionId) {
        return applyModification(filePath, "REPLACE", startLine, endLine, newText, null, sessionId);
    }
    
    /**
     * Deletes text between two line numbers.
     * 
     * @param filePath Relative path from workspace root
     * @param startLine Start line number (1-based, inclusive)
     * @param endLine End line number (1-based, inclusive)
     * @param sessionId Session ID for Supervisor approval
     * @return Success message or error
     */
    @Tool("Delete text between line numbers in a file. Requires Supervisor approval and Controlled-Impact Mode ON.")
    public String deleteLines(String filePath, int startLine, int endLine, String sessionId) {
        return applyModification(filePath, "DELETE", startLine, endLine, null, null, sessionId);
    }
    
    /**
     * Applies a modification after validation and approval.
     */
    private String applyModification(String filePath, String operation, int startLine, int endLine, 
                                    String newText, String oldText, String sessionId) {
        // Check Impact Mode via ImpactGuard
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("MODIFY", filePath);
        if (!validation.isAllowed()) {
            log.warn("Modify operation rejected: {}", validation.getMessage());
            return "ERROR: " + validation.getMessage();
        }
        
        try {
            Path fullPath = WORKSPACE_ROOT.resolve(filePath).normalize();
            
            // Validate path is within permitted directories
            if (!isPathPermitted(fullPath)) {
                String warning = String.format(
                    "WARNING: File path '%s' is outside permitted directories (backend/, frontend/). Modification denied.",
                    filePath
                );
                log.warn(warning);
                return warning;
            }
            
            if (!Files.exists(fullPath)) {
                return "ERROR: File does not exist: " + filePath;
            }
            
            // Read current file content
            List<String> lines = Files.readAllLines(fullPath);
            
            // Validate line numbers
            if (startLine < 1 || startLine > lines.size() || endLine < startLine || endLine > lines.size()) {
                return String.format("ERROR: Invalid line numbers. File has %d lines, requested range: %d-%d", 
                    lines.size(), startLine, endLine);
            }
            
            // Build change description for Supervisor
            String changeDescription = String.format(
                "Operation: %s\nFile: %s\nLines: %d-%d\n",
                operation, filePath, startLine, endLine
            );
            
            if (operation.equals("REPLACE") || operation.equals("INSERT")) {
                changeDescription += "New text preview: " + 
                    (newText.length() > 200 ? newText.substring(0, 200) + "..." : newText);
            }
            
            // Request Supervisor approval
            SupervisorAgent.ConstraintEnforcement enforcement = supervisorAgent.enforceConstraints(
                changeDescription,
                sessionId != null ? sessionId : "modify-" + System.currentTimeMillis()
            );
            
            if (!enforcement.isCompliant() &&
                enforcement.getViolations() != null &&
                !enforcement.getViolations().isEmpty()) {
                String error = "ERROR: Supervisor rejected modification. Violations: " + 
                              String.join(", ", enforcement.getViolations());
                log.warn(error);
                return error;
            }
            
            // Apply modification
            List<String> newLines = new ArrayList<>(lines);
            
            switch (operation) {
                case "INSERT":
                    newLines.add(startLine - 1, newText);
                    break;
                case "REPLACE":
                    newLines.subList(startLine - 1, endLine).clear();
                    newLines.add(startLine - 1, newText);
                    break;
                case "DELETE":
                    newLines.subList(startLine - 1, endLine).clear();
                    break;
            }
            
            // Write modified content
            Files.write(fullPath, newLines);
            
            log.info("File modified successfully: {} ({} at lines {}-{})", filePath, operation, startLine, endLine);
            return String.format("SUCCESS: File modified: %s (%s at lines %d-%d)", filePath, operation, startLine, endLine);
            
        } catch (IOException e) {
            log.error("Error modifying file: {}", filePath, e);
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

