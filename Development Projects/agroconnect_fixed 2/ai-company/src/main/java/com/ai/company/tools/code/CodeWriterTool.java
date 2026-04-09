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

/**
 * Code Writer Tool - Write new files with Supervisor approval.
 * 
 * This tool allows creating new files, but only:
 * - Inside /backend or /frontend folders
 * - After Supervisor approval
 * - When Impact Mode allows modifications
 * 
 * SAFETY:
 * - Requires ImpactGuard validation
 * - Requires Supervisor approval before writing
 * - Only creates new files (does not overwrite existing)
 * - Path validation ensures files are in permitted directories
 * - Emits warnings for unauthorized paths
 */
public class CodeWriterTool {
    
    private static final Logger log = LoggerFactory.getLogger(CodeWriterTool.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    private static final Path BACKEND_DIR = WORKSPACE_ROOT.resolve("backend").normalize();
    private static final Path FRONTEND_DIR = WORKSPACE_ROOT.resolve("frontend").normalize();
    
    private final SupervisorAgent supervisorAgent;
    private final ImpactGuard impactGuard;
    
    public CodeWriterTool(ChatLanguageModel chatModel) {
        this.supervisorAgent = new SupervisorAgent(chatModel);
        this.impactGuard = new ImpactGuard();
    }
    
    /**
     * Writes a new file with the given content.
     * 
     * @param filePath Relative path from workspace root (e.g., "backend/new-service/pom.xml")
     * @param content File content to write
     * @param sessionId Session ID for Supervisor approval
     * @return Success message or error/warning
     */
    @Tool("Write a new file. Creates file if missing. Only works inside /backend or /frontend folders. Requires Supervisor approval and Impact Mode that allows modifications.")
    public String writeFile(String filePath, String content, String sessionId) {
        // Check Impact Mode via ImpactGuard
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("WRITE", filePath);
        if (!validation.isAllowed()) {
            log.warn("Write operation rejected: {}", validation.getMessage());
            return "ERROR: " + validation.getMessage();
        }
        
        try {
            Path fullPath = WORKSPACE_ROOT.resolve(filePath).normalize();
            
            // Validate path is within permitted directories
            if (!isPathPermitted(fullPath)) {
                String warning = String.format(
                    "WARNING: File path '%s' is outside permitted directories (backend/, frontend/). Write operation denied.",
                    filePath
                );
                log.warn(warning);
                return warning;
            }
            
            // Check if file already exists
            if (Files.exists(fullPath)) {
                return "ERROR: File already exists: " + filePath + ". Use CodeModifierTool to modify existing files.";
            }
            
            // Request Supervisor approval
            String approvalRequest = String.format(
                "Request to create new file: %s\nContent preview (first 500 chars): %s",
                filePath,
                content.length() > 500 ? content.substring(0, 500) + "..." : content
            );
            
            SupervisorAgent.ConstraintEnforcement enforcement = supervisorAgent.enforceConstraints(
                approvalRequest, 
                sessionId != null ? sessionId : "write-" + System.currentTimeMillis()
            );
            
            if (!enforcement.isCompliant() &&
                enforcement.getViolations() != null &&
                !enforcement.getViolations().isEmpty()) {
                String error = "ERROR: Supervisor rejected file creation. Violations: " + 
                              String.join(", ", enforcement.getViolations());
                log.warn(error);
                return error;
            }
            
            // Create parent directories if needed
            Files.createDirectories(fullPath.getParent());
            
            // Write file
            Files.writeString(fullPath, content);
            
            log.info("File created successfully: {}", filePath);
            return "SUCCESS: File created: " + filePath;
            
        } catch (IOException e) {
            log.error("Error writing file: {}", filePath, e);
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

