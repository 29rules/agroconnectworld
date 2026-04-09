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
 * Docker Build Tool
 * 
 * Builds Docker images for backend microservices, frontend, and gateway.
 * 
 * SAFETY:
 * - Default: DRY-RUN mode (validates Dockerfiles, doesn't build)
 * - Only builds when Impact Mode allows (CONTROLLED_IMPACT or FULL_IMPACT)
 * - Requires ImpactGuard validation
 * - Returns detailed logs and warnings
 * 
 * ZERO-IMPACT MODE: Only validates Dockerfiles, never builds.
 */
public class DockerBuildTool {
    
    private static final Logger log = LoggerFactory.getLogger(DockerBuildTool.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    
    private final ImpactGuard impactGuard;
    
    public DockerBuildTool() {
        this.impactGuard = new ImpactGuard();
    }
    
    /**
     * Builds a Docker image for a backend microservice.
     * 
     * @param serviceName Service name (e.g., "auth-service", "product-service")
     * @param tag Docker image tag (default: "latest")
     * @return Build result with logs and status
     */
    @Tool("Build Docker image for a backend microservice. Service name (e.g., auth-service, product-service). Tag defaults to 'latest'. Only builds when Impact Mode allows.")
    public String buildBackendService(String serviceName, String tag) {
        if (tag == null || tag.isEmpty()) {
            tag = "latest";
        }
        
        String imageName = "agroconnectworld/" + serviceName + ":" + tag;
        Path dockerfilePath = WORKSPACE_ROOT.resolve("backend").resolve(serviceName).resolve("Dockerfile");
        
        return buildImage(serviceName, dockerfilePath, imageName, "backend/" + serviceName);
    }
    
    /**
     * Builds the frontend Docker image.
     * 
     * @param tag Docker image tag (default: "latest")
     * @return Build result with logs and status
     */
    @Tool("Build Docker image for the frontend. Tag defaults to 'latest'. Only builds when Impact Mode allows.")
    public String buildFrontend(String tag) {
        if (tag == null || tag.isEmpty()) {
            tag = "latest";
        }
        
        String imageName = "agroconnectworld/frontend:" + tag;
        Path dockerfilePath = WORKSPACE_ROOT.resolve("frontend").resolve("Dockerfile");
        
        return buildImage("frontend", dockerfilePath, imageName, "frontend");
    }
    
    /**
     * Builds the gateway Docker image.
     * 
     * @param tag Docker image tag (default: "latest")
     * @return Build result with logs and status
     */
    @Tool("Build Docker image for the gateway. Tag defaults to 'latest'. Only builds when Impact Mode allows.")
    public String buildGateway(String tag) {
        if (tag == null || tag.isEmpty()) {
            tag = "latest";
        }
        
        String imageName = "agroconnectworld/gateway:" + tag;
        Path dockerfilePath = WORKSPACE_ROOT.resolve("backend").resolve("gateway").resolve("Dockerfile");
        
        return buildImage("gateway", dockerfilePath, imageName, "backend/gateway");
    }
    
    /**
     * Validates a Dockerfile without building.
     * 
     * @param dockerfilePath Path to Dockerfile
     * @return Validation result
     */
    @Tool("Validate a Dockerfile for syntax and best practices. Read-only operation, always safe.")
    public String validateDockerfile(String dockerfilePath) {
        Path fullPath = WORKSPACE_ROOT.resolve(dockerfilePath).normalize();
        
        if (!Files.exists(fullPath)) {
            return "ERROR: Dockerfile not found: " + dockerfilePath;
        }
        
        try {
            String content = Files.readString(fullPath);
            List<String> warnings = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            // Basic validation checks
            if (!content.contains("FROM")) {
                errors.add("Missing FROM instruction");
            }
            
            if (content.contains("RUN apt-get") && !content.contains("apt-get clean")) {
                warnings.add("Consider adding 'apt-get clean' to reduce image size");
            }
            
            if (content.contains("COPY .") && !content.contains(".dockerignore")) {
                warnings.add("Consider using .dockerignore to exclude unnecessary files");
            }
            
            if (content.contains("USER root") && !content.contains("USER")) {
                warnings.add("Consider running as non-root user for security");
            }
            
            StringBuilder result = new StringBuilder();
            result.append("=== Dockerfile Validation ===\n");
            result.append("File: ").append(dockerfilePath).append("\n");
            
            if (errors.isEmpty() && warnings.isEmpty()) {
                result.append("Status: VALID - No issues found\n");
            } else {
                if (!errors.isEmpty()) {
                    result.append("Status: INVALID\n");
                    result.append("Errors:\n");
                    errors.forEach(e -> result.append("  - ").append(e).append("\n"));
                } else {
                    result.append("Status: VALID with warnings\n");
                }
                
                if (!warnings.isEmpty()) {
                    result.append("Warnings:\n");
                    warnings.forEach(w -> result.append("  - ").append(w).append("\n"));
                }
            }
            
            return result.toString();
            
        } catch (IOException e) {
            log.error("Error validating Dockerfile: {}", dockerfilePath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Builds a Docker image (internal method).
     */
    private String buildImage(String serviceName, Path dockerfilePath, String imageName, String buildContext) {
        // Check Impact Mode
        ImpactModeManager modeManager = ImpactModeManager.getInstance();
        ImpactMode currentMode = modeManager.getMode();
        
        // Validate operation
        ImpactGuard.ValidationResult validation = impactGuard.validateOperation("DOCKER_BUILD", dockerfilePath.toString());
        
        if (currentMode == ImpactMode.ZERO_IMPACT || !validation.isAllowed()) {
            // Dry-run mode: validate Dockerfile only
            log.info("DRY-RUN: Validating Dockerfile for {}", serviceName);
            String validationResult = validateDockerfile(dockerfilePath.toString().replace(WORKSPACE_ROOT.toString() + "/", ""));
            
            return "DRY-RUN MODE (ZERO_IMPACT):\n" +
                   "Would build image: " + imageName + "\n" +
                   "Build context: " + buildContext + "\n\n" +
                   validationResult + "\n\n" +
                   "To actually build, enable CONTROLLED_IMPACT or FULL_IMPACT mode.";
        }
        
        // Actual build (CONTROLLED_IMPACT or FULL_IMPACT)
        log.info("Building Docker image: {} for service: {}", imageName, serviceName);
        
        if (!Files.exists(dockerfilePath)) {
            return "ERROR: Dockerfile not found: " + dockerfilePath;
        }
        
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "build",
                "-t", imageName,
                "-f", dockerfilePath.toString(),
                WORKSPACE_ROOT.resolve(buildContext).toString()
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            List<String> outputLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(line -> {
                    outputLines.add(line);
                    log.debug("Docker build: {}", line);
                });
            }
            
            int exitCode = process.waitFor();
            
            StringBuilder result = new StringBuilder();
            result.append("=== Docker Build Result ===\n");
            result.append("Service: ").append(serviceName).append("\n");
            result.append("Image: ").append(imageName).append("\n");
            result.append("Exit Code: ").append(exitCode).append("\n\n");
            
            if (exitCode == 0) {
                result.append("Status: SUCCESS\n");
            } else {
                result.append("Status: FAILED\n");
            }
            
            result.append("\nBuild Logs:\n");
            outputLines.forEach(line -> result.append(line).append("\n"));
            
            return result.toString();
            
        } catch (IOException | InterruptedException e) {
            log.error("Error building Docker image: {}", imageName, e);
            return "ERROR: Build failed: " + e.getMessage();
        }
    }
}



