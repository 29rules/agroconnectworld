package com.ai.company.tools.deployment;

import com.ai.company.impact.ImpactGuard;
import com.ai.company.impact.ImpactMode;
import com.ai.company.impact.ImpactModeManager;
import com.ai.company.tools.code.CodeReaderTool;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Docker Compose Tool
 * 
 * Reads and validates docker-compose.yml files.
 * 
 * SAFETY:
 * - Default: Read-only operations
 * - Validates structure and configuration
 * - Suggests modifications (spec only) unless CONTROLLED_IMPACT mode
 * - Can simulate docker compose operations without actually running
 * 
 * ZERO-IMPACT MODE: Only reads and validates, never modifies or executes.
 */
public class DockerComposeTool {
    
    private static final Logger log = LoggerFactory.getLogger(DockerComposeTool.class);
    
    private final CodeReaderTool codeReader;
    private final ImpactGuard impactGuard;
    
    public DockerComposeTool() {
        this.codeReader = new CodeReaderTool();
        this.impactGuard = new ImpactGuard();
    }
    
    /**
     * Reads and validates docker-compose.yml structure.
     * 
     * @param composeFilePath Path to docker-compose.yml (default: "ops/docker-compose.yml")
     * @return Validation result with structure analysis
     */
    @Tool("Read and validate docker-compose.yml structure. Path defaults to 'ops/docker-compose.yml'. Read-only operation, always safe.")
    public String validateDockerCompose(String composeFilePath) {
        if (composeFilePath == null || composeFilePath.isEmpty()) {
            composeFilePath = "ops/docker-compose.yml";
        }
        
        log.info("Validating docker-compose.yml: {}", composeFilePath);
        
        String content = codeReader.readFile(composeFilePath);
        
        if (content.startsWith("ERROR") || content.startsWith("WARNING")) {
            return content;
        }
        
        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        
        // Basic validation checks
        if (!content.contains("version:") && !content.contains("services:")) {
            warnings.add("Missing 'version' or 'services' key - may be using v2 format");
        }
        
        if (content.contains("depends_on:") && !content.contains("healthcheck:")) {
            suggestions.add("Consider adding healthchecks when using depends_on");
        }
        
        if (content.contains("ports:") && content.contains("expose:")) {
            warnings.add("Mixing 'ports' and 'expose' - ensure intentional");
        }
        
        if (!content.contains("restart:")) {
            suggestions.add("Consider adding 'restart: unless-stopped' for production");
        }
        
        // Count services
        long serviceCount = content.split("\\n").length - content.replace(":", "").length();
        if (serviceCount < 3) {
            warnings.add("Few services defined - ensure all required services are present");
        }
        
        StringBuilder result = new StringBuilder();
        result.append("=== Docker Compose Validation ===\n");
        result.append("File: ").append(composeFilePath).append("\n");
        result.append("Status: ").append(warnings.isEmpty() ? "VALID" : "VALID with warnings").append("\n\n");
        
        if (!warnings.isEmpty()) {
            result.append("Warnings:\n");
            warnings.forEach(w -> result.append("  - ").append(w).append("\n"));
            result.append("\n");
        }
        
        if (!suggestions.isEmpty()) {
            result.append("Suggestions:\n");
            suggestions.forEach(s -> result.append("  - ").append(s).append("\n"));
            result.append("\n");
        }
        
        result.append("Note: This is a read-only validation. No changes were made.\n");
        
        return result.toString();
    }
    
    /**
     * Simulates docker compose up without actually running it.
     * 
     * @param composeFilePath Path to docker-compose.yml
     * @return Simulation result showing what would happen
     */
    @Tool("Simulate 'docker compose up' operation. Shows what would happen without actually starting containers. Always safe, read-only simulation.")
    public String simulateComposeUp(String composeFilePath) {
        if (composeFilePath == null || composeFilePath.isEmpty()) {
            composeFilePath = "ops/docker-compose.yml";
        }
        
        log.info("Simulating docker compose up for: {}", composeFilePath);
        
        String content = codeReader.readFile(composeFilePath);
        
        if (content.startsWith("ERROR") || content.startsWith("WARNING")) {
            return content;
        }
        
        // Extract service names (simplified parsing)
        List<String> services = new ArrayList<>();
        String[] lines = content.split("\\n");
        boolean inServices = false;
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("services:")) {
                inServices = true;
                continue;
            }
            if (inServices && line.endsWith(":") && !line.startsWith("  ")) {
                String serviceName = line.replace(":", "").trim();
                if (!serviceName.isEmpty() && !serviceName.equals("services")) {
                    services.add(serviceName);
                }
            }
        }
        
        StringBuilder result = new StringBuilder();
        result.append("=== Docker Compose Up Simulation ===\n");
        result.append("File: ").append(composeFilePath).append("\n");
        result.append("Mode: DRY-RUN (no containers will be started)\n\n");
        
        result.append("Would start the following services:\n");
        services.forEach(s -> result.append("  - ").append(s).append("\n"));
        result.append("\n");
        
        result.append("Total services: ").append(services.size()).append("\n");
        result.append("\n");
        result.append("Note: This is a simulation. No containers were actually started.\n");
        result.append("To actually start containers, use DockerRunnerTool with FULL_IMPACT mode.\n");
        
        return result.toString();
    }
    
    /**
     * Simulates docker compose down without actually running it.
     * 
     * @param composeFilePath Path to docker-compose.yml
     * @return Simulation result showing what would happen
     */
    @Tool("Simulate 'docker compose down' operation. Shows what would happen without actually stopping containers. Always safe, read-only simulation.")
    public String simulateComposeDown(String composeFilePath) {
        if (composeFilePath == null || composeFilePath.isEmpty()) {
            composeFilePath = "ops/docker-compose.yml";
        }
        
        log.info("Simulating docker compose down for: {}", composeFilePath);
        
        StringBuilder result = new StringBuilder();
        result.append("=== Docker Compose Down Simulation ===\n");
        result.append("File: ").append(composeFilePath).append("\n");
        result.append("Mode: DRY-RUN (no containers will be stopped)\n\n");
        
        result.append("Would stop and remove:\n");
        result.append("  - All running containers defined in docker-compose.yml\n");
        result.append("  - Networks created by docker-compose\n");
        result.append("  - Volumes (if --volumes flag used)\n");
        result.append("\n");
        result.append("Note: This is a simulation. No containers were actually stopped.\n");
        result.append("To actually stop containers, use DockerRunnerTool with FULL_IMPACT mode.\n");
        
        return result.toString();
    }
    
    /**
     * Suggests modifications to docker-compose.yml (spec only unless CONTROLLED_IMPACT).
     * 
     * @param composeFilePath Path to docker-compose.yml
     * @return Suggestions for improvements
     */
    @Tool("Suggest modifications to docker-compose.yml. Returns suggestions only unless CONTROLLED_IMPACT mode is enabled. Safe read-only operation by default.")
    public String suggestModifications(String composeFilePath) {
        if (composeFilePath == null || composeFilePath.isEmpty()) {
            composeFilePath = "ops/docker-compose.yml";
        }
        
        ImpactModeManager modeManager = ImpactModeManager.getInstance();
        ImpactMode currentMode = modeManager.getMode();
        
        String content = codeReader.readFile(composeFilePath);
        
        if (content.startsWith("ERROR") || content.startsWith("WARNING")) {
            return content;
        }
        
        List<String> suggestions = new ArrayList<>();
        
        // Generate suggestions
        if (!content.contains("healthcheck:")) {
            suggestions.add("Add healthchecks to services for better reliability");
        }
        
        if (!content.contains("restart:")) {
            suggestions.add("Add 'restart: unless-stopped' to services for auto-recovery");
        }
        
        if (!content.contains("logging:")) {
            suggestions.add("Configure logging driver for better log management");
        }
        
        if (!content.contains("networks:")) {
            suggestions.add("Define custom networks for better service isolation");
        }
        
        StringBuilder result = new StringBuilder();
        result.append("=== Docker Compose Modification Suggestions ===\n");
        result.append("File: ").append(composeFilePath).append("\n");
        result.append("Current Mode: ").append(currentMode).append("\n\n");
        
        if (suggestions.isEmpty()) {
            result.append("No suggestions at this time.\n");
        } else {
            result.append("Suggestions:\n");
            suggestions.forEach(s -> result.append("  - ").append(s).append("\n"));
        }
        
        result.append("\n");
        
        if (currentMode == ImpactMode.ZERO_IMPACT) {
            result.append("Note: Suggestions only. No modifications will be made in ZERO_IMPACT mode.\n");
            result.append("Enable CONTROLLED_IMPACT mode to apply modifications.\n");
        } else {
            result.append("Note: To apply these suggestions, use appropriate code modification tools.\n");
        }
        
        return result.toString();
    }
}



