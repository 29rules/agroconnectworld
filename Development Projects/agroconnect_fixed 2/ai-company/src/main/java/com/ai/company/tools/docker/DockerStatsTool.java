package com.ai.company.tools.docker;

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
 * Read-only tool for reading Docker container stats and configurations.
 * ZERO-IMPACT MODE: This tool only reads, never modifies Docker state.
 */
public class DockerStatsTool {
    
    private static final Logger log = LoggerFactory.getLogger(DockerStatsTool.class);
    private final Path dockerComposePath;
    
    public DockerStatsTool(String dockerComposePath) {
        this.dockerComposePath = Paths.get(dockerComposePath);
    }
    
    public DockerStatsTool() {
        // Default to ops/docker-compose.yml
        this.dockerComposePath = Paths.get("../ops/docker-compose.yml");
    }
    
    @Tool("Read Docker Compose configuration file. Returns YAML content as string.")
    public String readDockerCompose() {
        try {
            if (!Files.exists(dockerComposePath)) {
                return "ERROR: Docker Compose file not found at: " + dockerComposePath;
            }
            
            return Files.readString(dockerComposePath);
        } catch (IOException e) {
            log.error("Error reading Docker Compose file", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    @Tool("Get list of running Docker containers. Returns container names and status.")
    public String listContainers() {
        try {
            Process process = new ProcessBuilder("docker", "ps", "--format", "{{.Names}}\t{{.Status}}\t{{.Ports}}")
                .start();
            
            List<String> containers = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    containers.add(line);
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "ERROR: Docker command failed. Is Docker running?";
            }
            
            return containers.isEmpty() 
                ? "No containers running" 
                : String.join("\n", containers);
        } catch (IOException | InterruptedException e) {
            log.error("Error listing Docker containers", e);
            return "ERROR: " + e.getMessage() + " (Docker may not be available)";
        }
    }
    
    @Tool("Get stats for a specific Docker container. Returns CPU, memory, and network stats.")
    public String getContainerStats(String containerName) {
        try {
            Process process = new ProcessBuilder("docker", "stats", containerName, 
                "--no-stream", "--format", 
                "Container: {{.Container}}\nCPU: {{.CPUPerc}}\nMemory: {{.MemUsage}}\nNetwork: {{.NetIO}}")
                .start();
            
            StringBuilder stats = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stats.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "ERROR: Container not found or Docker command failed";
            }
            
            return stats.toString().isEmpty() 
                ? "ERROR: No stats available for container: " + containerName
                : stats.toString();
        } catch (IOException | InterruptedException e) {
            log.error("Error getting container stats: {}", containerName, e);
            return "ERROR: " + e.getMessage() + " (Docker may not be available)";
        }
    }
    
    @Tool("Read Nginx configuration file. Returns configuration content.")
    public String readNginxConfig() {
        try {
            Path nginxConfig = Paths.get("../ops/nginx/default.conf");
            
            if (!Files.exists(nginxConfig)) {
                return "ERROR: Nginx config file not found";
            }
            
            return Files.readString(nginxConfig);
        } catch (IOException e) {
            log.error("Error reading Nginx config", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    @Tool("Check Docker service health. Returns health status of services from docker-compose.")
    public String checkServiceHealth() {
        try {
            Process process = new ProcessBuilder("docker", "compose", "-f", 
                dockerComposePath.toString(), "ps", "--format", "json")
                .start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "ERROR: Docker Compose command failed";
            }
            
            return output.toString().isEmpty() 
                ? "No services found" 
                : output.toString();
        } catch (IOException | InterruptedException e) {
            log.error("Error checking service health", e);
            return "ERROR: " + e.getMessage() + " (Docker Compose may not be available)";
        }
    }
}



