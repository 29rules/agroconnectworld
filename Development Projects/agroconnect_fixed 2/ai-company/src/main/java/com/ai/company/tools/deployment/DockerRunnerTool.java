package com.ai.company.tools.deployment;

import com.ai.company.impact.ImpactGuard;
import com.ai.company.impact.ImpactMode;
import com.ai.company.impact.ImpactModeManager;
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
 * Docker Runner Tool
 * 
 * Runs Docker containers and docker-compose operations.
 * 
 * SAFETY:
 * - Default: DRY-RUN mode only
 * - Only runs containers when ImpactMode = FULL_IMPACT
 * - All operations require ImpactGuard validation
 * - Supervisor approval required for destructive operations
 * 
 * ZERO-IMPACT MODE: Only simulates, never actually runs containers.
 */
public class DockerRunnerTool {
    
    private static final Logger log = LoggerFactory.getLogger(DockerRunnerTool.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    
    private final ImpactGuard impactGuard;
    
    public DockerRunnerTool() {
        this.impactGuard = new ImpactGuard();
    }
    
    /**
     * Runs docker compose up (only in FULL_IMPACT mode, otherwise dry-run).
     * 
     * @param composeFilePath Path to docker-compose.yml
     * @param profile Docker Compose profile (optional)
     * @return Execution result
     */
    @Tool("Run 'docker compose up'. Only actually runs when ImpactMode = FULL_IMPACT. Default behavior is dry-run simulation.")
    public String composeUp(String composeFilePath, String profile) {
        if (composeFilePath == null || composeFilePath.isEmpty()) {
            composeFilePath = "ops/docker-compose.yml";
        }
        
        ImpactModeManager modeManager = ImpactModeManager.getInstance();
        ImpactMode currentMode = modeManager.getMode();
        
        // Validate operation
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("DOCKER_COMPOSE_UP", composeFilePath);
        
        if (currentMode != ImpactMode.FULL_IMPACT || !validation.isAllowed()) {
            // Dry-run mode
            log.info("DRY-RUN: Simulating docker compose up");
            return simulateComposeUp(composeFilePath, profile);
        }
        
        // Actual execution (FULL_IMPACT only)
        log.warn("EXECUTING docker compose up in FULL_IMPACT mode");
        
        try {
            Path composeFile = WORKSPACE_ROOT.resolve(composeFilePath).normalize();
            if (!Files.exists(composeFile)) {
                return "ERROR: docker-compose.yml not found: " + composeFilePath;
            }
            
            List<String> command = new ArrayList<>();
            command.add("docker");
            command.add("compose");
            command.add("-f");
            command.add(composeFile.toString());
            
            if (profile != null && !profile.isEmpty()) {
                command.add("--profile");
                command.add(profile);
            }
            
            command.add("up");
            command.add("-d"); // Detached mode
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(WORKSPACE_ROOT.toFile());
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            List<String> outputLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(line -> {
                    outputLines.add(line);
                    log.info("Docker compose: {}", line);
                });
            }
            
            int exitCode = process.waitFor();
            
            StringBuilder result = new StringBuilder();
            result.append("=== Docker Compose Up Result ===\n");
            result.append("File: ").append(composeFilePath).append("\n");
            result.append("Profile: ").append(profile != null ? profile : "default").append("\n");
            result.append("Exit Code: ").append(exitCode).append("\n\n");
            
            if (exitCode == 0) {
                result.append("Status: SUCCESS - Containers started\n");
            } else {
                result.append("Status: FAILED\n");
            }
            
            result.append("\nOutput:\n");
            outputLines.forEach(line -> result.append(line).append("\n"));
            
            return result.toString();
            
        } catch (IOException | InterruptedException e) {
            log.error("Error running docker compose up", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Runs docker compose down (only in FULL_IMPACT mode, otherwise dry-run).
     * 
     * @param composeFilePath Path to docker-compose.yml
     * @return Execution result
     */
    @Tool("Run 'docker compose down'. Only actually runs when ImpactMode = FULL_IMPACT. Default behavior is dry-run simulation.")
    public String composeDown(String composeFilePath) {
        if (composeFilePath == null || composeFilePath.isEmpty()) {
            composeFilePath = "ops/docker-compose.yml";
        }
        
        ImpactModeManager modeManager = ImpactModeManager.getInstance();
        ImpactMode currentMode = modeManager.getMode();
        
        // Validate operation
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("DOCKER_COMPOSE_DOWN", composeFilePath);
        
        if (currentMode != ImpactMode.FULL_IMPACT || !validation.isAllowed()) {
            // Dry-run mode
            log.info("DRY-RUN: Simulating docker compose down");
            return simulateComposeDown(composeFilePath);
        }
        
        // Actual execution (FULL_IMPACT only)
        log.warn("EXECUTING docker compose down in FULL_IMPACT mode");
        
        try {
            Path composeFile = WORKSPACE_ROOT.resolve(composeFilePath).normalize();
            if (!Files.exists(composeFile)) {
                return "ERROR: docker-compose.yml not found: " + composeFilePath;
            }
            
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "compose",
                "-f", composeFile.toString(),
                "down"
            );
            
            pb.directory(WORKSPACE_ROOT.toFile());
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            List<String> outputLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(line -> {
                    outputLines.add(line);
                    log.info("Docker compose: {}", line);
                });
            }
            
            int exitCode = process.waitFor();
            
            StringBuilder result = new StringBuilder();
            result.append("=== Docker Compose Down Result ===\n");
            result.append("File: ").append(composeFilePath).append("\n");
            result.append("Exit Code: ").append(exitCode).append("\n\n");
            
            if (exitCode == 0) {
                result.append("Status: SUCCESS - Containers stopped\n");
            } else {
                result.append("Status: FAILED\n");
            }
            
            result.append("\nOutput:\n");
            outputLines.forEach(line -> result.append(line).append("\n"));
            
            return result.toString();
            
        } catch (IOException | InterruptedException e) {
            log.error("Error running docker compose down", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Simulates docker compose up.
     */
    private String simulateComposeUp(String composeFilePath, String profile) {
        StringBuilder result = new StringBuilder();
        result.append("=== Docker Compose Up Simulation ===\n");
        result.append("File: ").append(composeFilePath).append("\n");
        result.append("Profile: ").append(profile != null ? profile : "default").append("\n");
        result.append("Mode: DRY-RUN (no containers will be started)\n\n");
        
        result.append("Would execute: docker compose -f ").append(composeFilePath);
        if (profile != null && !profile.isEmpty()) {
            result.append(" --profile ").append(profile);
        }
        result.append(" up -d\n\n");
        
        result.append("Note: This is a simulation. No containers were actually started.\n");
        result.append("To actually start containers, enable FULL_IMPACT mode.\n");
        
        return result.toString();
    }
    
    /**
     * Simulates docker compose down.
     */
    private String simulateComposeDown(String composeFilePath) {
        StringBuilder result = new StringBuilder();
        result.append("=== Docker Compose Down Simulation ===\n");
        result.append("File: ").append(composeFilePath).append("\n");
        result.append("Mode: DRY-RUN (no containers will be stopped)\n\n");
        
        result.append("Would execute: docker compose -f ").append(composeFilePath).append(" down\n\n");
        result.append("Would stop and remove:\n");
        result.append("  - All running containers\n");
        result.append("  - Networks\n");
        result.append("  - Volumes (if --volumes flag used)\n\n");
        
        result.append("Note: This is a simulation. No containers were actually stopped.\n");
        result.append("To actually stop containers, enable FULL_IMPACT mode.\n");
        
        return result.toString();
    }
}



