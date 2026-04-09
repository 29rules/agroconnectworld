package com.ai.company.tools.code;

import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.impact.ImpactGuard;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Git Commit Tool - Stage and commit changes with Supervisor approval.
 * 
 * This tool allows:
 * - Staging changes
 * - Committing with message
 * - DO NOT push (safety measure)
 * 
 * SAFETY:
 * - Requires ImpactGuard validation
 * - Requires Supervisor approval
 * - Only commits, never pushes
 * - Validates changes before committing
 */
public class GitCommitTool {
    
    private static final Logger log = LoggerFactory.getLogger(GitCommitTool.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    
    private final SupervisorAgent supervisorAgent;
    private final ImpactGuard impactGuard;
    
    public GitCommitTool(ChatLanguageModel chatModel) {
        this.supervisorAgent = new SupervisorAgent(chatModel);
        this.impactGuard = new ImpactGuard();
    }
    
    /**
     * Stages all changes in the repository.
     * 
     * @return Success message or error
     */
    @Tool("Stage all changes in git repository. Requires Impact Mode that allows modifications.")
    public String stageChanges() {
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("STAGE", null);
        if (!validation.isAllowed()) {
            return "ERROR: " + validation.getMessage();
        }
        
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "add", ".");
            pb.directory(WORKSPACE_ROOT.toFile());
            Process process = pb.start();
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log.info("Changes staged successfully");
                return "SUCCESS: All changes staged";
            } else {
                // Read error output
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                    String error = reader.lines().collect(java.util.stream.Collectors.joining("\n"));
                    return "ERROR: Staging failed: " + error;
                }
            }
            
        } catch (IOException | InterruptedException e) {
            log.error("Error staging changes", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Stages specific files.
     * 
     * @param filePaths Comma-separated list of file paths
     * @return Success message or error
     */
    @Tool("Stage specific files. Provide comma-separated file paths. Requires Impact Mode that allows modifications.")
    public String stageFiles(String filePaths) {
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("STAGE", filePaths);
        if (!validation.isAllowed()) {
            return "ERROR: " + validation.getMessage();
        }
        
        try {
            String[] files = filePaths.split(",");
            List<String> command = new ArrayList<>();
            command.add("git");
            command.add("add");
            for (String file : files) {
                command.add(file.trim());
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(WORKSPACE_ROOT.toFile());
            Process process = pb.start();
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log.info("Files staged successfully: {}", filePaths);
                return "SUCCESS: Files staged: " + filePaths;
            } else {
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                    String error = reader.lines().collect(java.util.stream.Collectors.joining("\n"));
                    return "ERROR: Staging failed: " + error;
                }
            }
            
        } catch (IOException | InterruptedException e) {
            log.error("Error staging files", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Commits staged changes with a message.
     * Requires Supervisor approval.
     * 
     * @param commitMessage Commit message
     * @param sessionId Session ID for Supervisor approval
     * @return Success message or error
     */
    @Tool("Commit staged changes with a message. Requires Supervisor approval and Impact Mode that allows modifications. DO NOT push.")
    public String commitChanges(String commitMessage, String sessionId) {
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("COMMIT", null);
        if (!validation.isAllowed()) {
            return "ERROR: " + validation.getMessage();
        }
        
        try {
            // Check if there are staged changes
            ProcessBuilder statusPb = new ProcessBuilder("git", "diff", "--cached", "--quiet");
            statusPb.directory(WORKSPACE_ROOT.toFile());
            Process statusProcess = statusPb.start();
            int statusExitCode = statusProcess.waitFor();
            
            if (statusExitCode == 0) {
                return "ERROR: No staged changes to commit. Stage changes first using stageChanges() or stageFiles().";
            }
            
            // Get list of staged changes for Supervisor review
            ProcessBuilder diffPb = new ProcessBuilder("git", "diff", "--cached", "--stat");
            diffPb.directory(WORKSPACE_ROOT.toFile());
            Process diffProcess = diffPb.start();
            
            List<String> diffOutput = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(diffProcess.getInputStream()))) {
                reader.lines().forEach(diffOutput::add);
            }
            
            String changesSummary = String.join("\n", diffOutput);
            
            // Request Supervisor approval
            String approvalRequest = String.format(
                "Request to commit changes:\nCommit Message: %s\n\nStaged Changes:\n%s",
                commitMessage,
                changesSummary.length() > 1000 ? changesSummary.substring(0, 1000) + "..." : changesSummary
            );
            
            SupervisorAgent.ConstraintEnforcement enforcement = supervisorAgent.enforceConstraints(
                approvalRequest,
                sessionId != null ? sessionId : "commit-" + System.currentTimeMillis()
            );
            
            if (!enforcement.isCompliant() &&
                enforcement.getViolations() != null &&
                !enforcement.getViolations().isEmpty()) {
                String error = "ERROR: Supervisor rejected commit. Violations: " + 
                              String.join(", ", enforcement.getViolations());
                log.warn(error);
                return error;
            }
            
            // Perform commit
            ProcessBuilder commitPb = new ProcessBuilder("git", "commit", "-m", commitMessage);
            commitPb.directory(WORKSPACE_ROOT.toFile());
            Process commitProcess = commitPb.start();
            
            int exitCode = commitProcess.waitFor();
            
            if (exitCode == 0) {
                log.info("Changes committed successfully with message: {}", commitMessage);
                return "SUCCESS: Changes committed with message: " + commitMessage + 
                       "\nNOTE: Changes are NOT pushed. Push manually if needed.";
            } else {
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(commitProcess.getErrorStream()))) {
                    String error = reader.lines().collect(java.util.stream.Collectors.joining("\n"));
                    return "ERROR: Commit failed: " + error;
                }
            }
            
        } catch (IOException | InterruptedException e) {
            log.error("Error committing changes", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Gets the status of the git repository.
     * 
     * @return Git status output
     */
    @Tool("Get git repository status. Shows staged, modified, and untracked files. Read-only operation.")
    public String getGitStatus() {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "status", "--short");
            pb.directory(WORKSPACE_ROOT.toFile());
            Process process = pb.start();
            
            List<String> output = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(output::add);
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                return output.isEmpty() ? "No changes in repository" : String.join("\n", output);
            } else {
                return "ERROR: Failed to get git status";
            }
            
        } catch (IOException | InterruptedException e) {
            log.error("Error getting git status", e);
            return "ERROR: " + e.getMessage();
        }
    }
}

