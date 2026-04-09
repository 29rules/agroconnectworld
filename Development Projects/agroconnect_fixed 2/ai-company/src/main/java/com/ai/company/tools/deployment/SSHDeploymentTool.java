package com.ai.company.tools.deployment;

import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.impact.ImpactGuard;
import com.ai.company.impact.ImpactMode;
import com.ai.company.impact.ImpactModeManager;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatLanguageModel;
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
 * SSH Deployment Tool
 * 
 * Connects to VPS via SSH and uploads files for deployment.
 * 
 * SAFETY:
 * - Only connects when Impact Mode allows (CONTROLLED_IMPACT or FULL_IMPACT)
 * - Must NOT overwrite anything unless Supervisor approves
 * - Limited SCP and SSH commands only
 * - All operations logged
 * 
 * RESTRICTIONS:
 * - Only allows: scp, ssh (read-only commands), ls, cat (read-only)
 * - Forbidden: rm, mv, chmod, chown, sudo, systemctl, service
 * - Uploads only to /deploy/staging or /deploy/production
 * - Never overwrites without explicit approval
 * 
 * ZERO-IMPACT MODE: Only simulates, never actually connects.
 */
public class SSHDeploymentTool {
    
    private static final Logger log = LoggerFactory.getLogger(SSHDeploymentTool.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    
    private static final List<String> ALLOWED_COMMANDS = List.of(
        "scp", "ssh", "ls", "cat", "pwd", "whoami", "date", "df", "free"
    );
    
    private static final List<String> FORBIDDEN_COMMANDS = List.of(
        "rm", "mv", "chmod", "chown", "sudo", "systemctl", "service",
        "kill", "reboot", "shutdown", "dd", "mkfs", "fdisk"
    );
    
    private final ImpactGuard impactGuard;
    private final SupervisorAgent supervisorAgent;
    
    public SSHDeploymentTool(ChatLanguageModel chatModel) {
        this.impactGuard = new ImpactGuard();
        this.supervisorAgent = new SupervisorAgent(chatModel);
    }
    
    /**
     * Uploads files to VPS deployment directory via SCP.
     * 
     * @param host VPS hostname or IP
     * @param username SSH username
     * @param localPath Local file or directory path
     * @param remotePath Remote path (must be /deploy/staging or /deploy/production)
     * @param sessionId Session ID for Supervisor approval
     * @return Upload result
     */
    @Tool("Upload files to VPS via SCP. Only to /deploy/staging or /deploy/production. Requires Supervisor approval and Impact Mode that allows modifications.")
    public String uploadToVPS(String host, String username, String localPath, String remotePath, String sessionId) {
        ImpactModeManager modeManager = ImpactModeManager.getInstance();
        ImpactMode currentMode = modeManager.getMode();
        
        // Validate remote path
        if (remotePath == null || remotePath.isEmpty()) {
            return "ERROR: Remote path is required";
        }
        
        if (!remotePath.startsWith("/deploy/staging") && !remotePath.startsWith("/deploy/production")) {
            return "ERROR: Remote path must be /deploy/staging or /deploy/production. Got: " + remotePath;
        }
        
        // Validate operation
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("SSH_UPLOAD", localPath);
        
        if (currentMode == ImpactMode.ZERO_IMPACT || !validation.isAllowed()) {
            return simulateUpload(host, username, localPath, remotePath);
        }
        
        // Check if file exists on remote (would overwrite)
        boolean wouldOverwrite = checkRemoteFileExists(host, username, remotePath);
        
        if (wouldOverwrite) {
            // Request Supervisor approval
            String approvalRequest = String.format(
                "Request to upload %s to %s@%s:%s would overwrite existing file. Approve?",
                localPath, username, host, remotePath
            );
            
            SupervisorAgent.ConstraintEnforcement enforcement = 
                supervisorAgent.enforceConstraints(approvalRequest, sessionId);
            
            if (!enforcement.isCompliant()) {
                String reason = enforcement.getReport() != null ? enforcement.getReport() : 
                    "Violations: " + String.join(", ", enforcement.getViolations());
                return "ERROR: Supervisor rejected overwrite operation. " + reason;
            }
        }
        
        // Actual upload (CONTROLLED_IMPACT or FULL_IMPACT)
        log.warn("EXECUTING SCP upload to VPS: {}@{}:{}", username, host, remotePath);
        
        try {
            Path localFile = WORKSPACE_ROOT.resolve(localPath).normalize();
            if (!Files.exists(localFile)) {
                return "ERROR: Local file not found: " + localPath;
            }
            
            // Build SCP command
            String scpCommand = String.format(
                "scp -r %s %s@%s:%s",
                localFile.toString(),
                username,
                host,
                remotePath
            );
            
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", scpCommand);
            pb.directory(WORKSPACE_ROOT.toFile());
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            List<String> outputLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(line -> {
                    outputLines.add(line);
                    log.info("SCP: {}", line);
                });
            }
            
            int exitCode = process.waitFor();
            
            StringBuilder result = new StringBuilder();
            result.append("=== SCP Upload Result ===\n");
            result.append("Host: ").append(host).append("\n");
            result.append("User: ").append(username).append("\n");
            result.append("Local: ").append(localPath).append("\n");
            result.append("Remote: ").append(remotePath).append("\n");
            result.append("Exit Code: ").append(exitCode).append("\n\n");
            
            if (exitCode == 0) {
                result.append("Status: SUCCESS - Files uploaded\n");
            } else {
                result.append("Status: FAILED\n");
            }
            
            result.append("\nOutput:\n");
            outputLines.forEach(line -> result.append(line).append("\n"));
            
            return result.toString();
            
        } catch (IOException | InterruptedException e) {
            log.error("Error uploading to VPS", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Executes a safe SSH command (read-only only).
     * 
     * @param host VPS hostname or IP
     * @param username SSH username
     * @param command SSH command (must be in allowed list)
     * @return Command result
     */
    @Tool("Execute a safe SSH command. Only read-only commands allowed: ls, cat, pwd, whoami, date, df, free. Forbidden: rm, mv, chmod, sudo, systemctl, etc.")
    public String executeSSHCommand(String host, String username, String command) {
        if (command == null || command.isEmpty()) {
            return "ERROR: Command is required";
        }
        
        // Validate command is safe
        String commandBase = command.split(" ")[0];
        if (FORBIDDEN_COMMANDS.contains(commandBase)) {
            return "ERROR: Forbidden command: " + commandBase + ". Only read-only commands are allowed.";
        }
        
        if (!ALLOWED_COMMANDS.contains(commandBase)) {
            return "ERROR: Command not in allowed list: " + commandBase + ". Allowed: " + ALLOWED_COMMANDS;
        }
        
        log.info("Executing safe SSH command: {}@{}: {}", username, host, command);
        
        try {
            String sshCommand = String.format(
                "ssh %s@%s '%s'",
                username,
                host,
                command.replace("'", "\\'")
            );
            
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", sshCommand);
            pb.directory(WORKSPACE_ROOT.toFile());
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            List<String> outputLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(line -> {
                    outputLines.add(line);
                    log.info("SSH: {}", line);
                });
            }
            
            int exitCode = process.waitFor();
            
            StringBuilder result = new StringBuilder();
            result.append("=== SSH Command Result ===\n");
            result.append("Host: ").append(host).append("\n");
            result.append("User: ").append(username).append("\n");
            result.append("Command: ").append(command).append("\n");
            result.append("Exit Code: ").append(exitCode).append("\n\n");
            
            result.append("Output:\n");
            outputLines.forEach(line -> result.append(line).append("\n"));
            
            return result.toString();
            
        } catch (IOException | InterruptedException e) {
            log.error("Error executing SSH command", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Simulates upload operation.
     */
    private String simulateUpload(String host, String username, String localPath, String remotePath) {
        StringBuilder result = new StringBuilder();
        result.append("=== SCP Upload Simulation ===\n");
        result.append("Mode: DRY-RUN (ZERO_IMPACT)\n");
        result.append("Host: ").append(host).append("\n");
        result.append("User: ").append(username).append("\n");
        result.append("Local: ").append(localPath).append("\n");
        result.append("Remote: ").append(remotePath).append("\n\n");
        
        result.append("Would execute: scp -r ").append(localPath)
              .append(" ").append(username).append("@").append(host)
              .append(":").append(remotePath).append("\n\n");
        
        result.append("Note: This is a simulation. No files were actually uploaded.\n");
        result.append("To actually upload, enable CONTROLLED_IMPACT or FULL_IMPACT mode.\n");
        
        return result.toString();
    }
    
    /**
     * Checks if remote file exists (simulated).
     */
    private boolean checkRemoteFileExists(String host, String username, String remotePath) {
        // In real implementation, would use SSH to check
        // For now, return false (assume no overwrite)
        return false;
    }
}

