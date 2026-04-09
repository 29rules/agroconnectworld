package com.ai.company.tools.deployment;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Health Check Tool
 * 
 * Sends HTTP health checks to services and containers.
 * 
 * SAFETY:
 * - Read-only operation, always safe
 * - Only checks health endpoints
 * - Reports latency, uptime, errors
 * - Never modifies anything
 * 
 * This tool is always safe and can be used in any Impact Mode.
 */
@Component
public class HealthCheckTool {
    
    private static final Logger log = LoggerFactory.getLogger(HealthCheckTool.class);
    
    /**
     * Checks health of a service via HTTP endpoint.
     * 
     * @param serviceUrl Service health endpoint URL (e.g., "http://localhost:8080/actuator/health")
     * @return Health check result
     */
    @Tool("Check health of a service via HTTP endpoint. Returns status, latency, and any errors. Read-only, always safe.")
    public String checkServiceHealth(String serviceUrl) {
        if (serviceUrl == null || serviceUrl.isEmpty()) {
            return "ERROR: Service URL is required";
        }
        
        log.info("Checking service health: {}", serviceUrl);
        
        try {
            URL url = new URL(serviceUrl);
            Instant start = Instant.now();
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "AI-Company-HealthCheck/1.0");
            
            int responseCode = connection.getResponseCode();
            Instant end = Instant.now();
            long latencyMs = Duration.between(start, end).toMillis();
            
            String responseBody = "";
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 
                        ? connection.getInputStream() 
                        : connection.getErrorStream()
                ))) {
                responseBody = reader.lines().reduce("", (a, b) -> a + b + "\n");
            }
            
            StringBuilder result = new StringBuilder();
            result.append("=== Service Health Check ===\n");
            result.append("URL: ").append(serviceUrl).append("\n");
            result.append("Status Code: ").append(responseCode).append("\n");
            result.append("Latency: ").append(latencyMs).append(" ms\n");
            result.append("Timestamp: ").append(Instant.now()).append("\n\n");
            
            if (responseCode >= 200 && responseCode < 300) {
                result.append("Status: HEALTHY\n");
            } else if (responseCode == 503) {
                result.append("Status: UNHEALTHY (Service Unavailable)\n");
            } else if (responseCode == 404) {
                result.append("Status: NOT FOUND (Endpoint may not exist)\n");
            } else {
                result.append("Status: ERROR\n");
            }
            
            if (!responseBody.isEmpty()) {
                result.append("Response Body:\n").append(responseBody).append("\n");
            }
            
            return result.toString();
            
        } catch (IOException e) {
            log.error("Error checking service health: {}", serviceUrl, e);
            return "ERROR: Health check failed: " + e.getMessage();
        }
    }
    
    /**
     * Checks health of multiple services.
     * 
     * @param serviceUrls Comma-separated list of service URLs
     * @return Combined health check results
     */
    @Tool("Check health of multiple services. Provide comma-separated URLs. Returns combined results. Read-only, always safe.")
    public String checkMultipleServices(String serviceUrls) {
        if (serviceUrls == null || serviceUrls.isEmpty()) {
            return "ERROR: Service URLs are required";
        }
        
        String[] urls = serviceUrls.split(",");
        List<String> results = new ArrayList<>();
        
        for (String url : urls) {
            url = url.trim();
            if (!url.isEmpty()) {
                results.add(checkServiceHealth(url));
            }
        }
        
        StringBuilder combined = new StringBuilder();
        combined.append("=== Multiple Service Health Checks ===\n");
        combined.append("Total Services: ").append(results.size()).append("\n\n");
        
        int healthyCount = 0;
        for (String result : results) {
            if (result.contains("Status: HEALTHY")) {
                healthyCount++;
            }
            combined.append(result).append("\n---\n\n");
        }
        
        combined.append("Summary: ").append(healthyCount).append("/").append(results.size())
                .append(" services healthy\n");
        
        return combined.toString();
    }
    
    /**
     * Checks Docker container health status.
     * 
     * @param containerName Container name
     * @return Container health status
     */
    @Tool("Check Docker container health status. Returns container status, uptime, and resource usage. Read-only, always safe.")
    public String checkContainerHealth(String containerName) {
        if (containerName == null || containerName.isEmpty()) {
            return "ERROR: Container name is required";
        }
        
        log.info("Checking container health: {}", containerName);
        
        try {
            // Use docker inspect to get health status
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "inspect",
                "--format", "{{.State.Status}}|{{.State.Health.Status}}|{{.State.StartedAt}}",
                containerName
            );
            
            Process process = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(line -> output.append(line).append("\n"));
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                return "ERROR: Container not found or docker command failed";
            }
            
            String[] parts = output.toString().trim().split("\\|");
            String status = parts.length > 0 ? parts[0] : "unknown";
            String healthStatus = parts.length > 1 ? parts[1] : "none";
            String startedAt = parts.length > 2 ? parts[2] : "unknown";
            
            StringBuilder result = new StringBuilder();
            result.append("=== Container Health Check ===\n");
            result.append("Container: ").append(containerName).append("\n");
            result.append("Status: ").append(status).append("\n");
            result.append("Health: ").append(healthStatus.isEmpty() ? "no healthcheck" : healthStatus).append("\n");
            result.append("Started At: ").append(startedAt).append("\n");
            
            if ("running".equals(status)) {
                result.append("Overall: HEALTHY\n");
            } else {
                result.append("Overall: UNHEALTHY\n");
            }
            
            return result.toString();
            
        } catch (IOException | InterruptedException e) {
            log.error("Error checking container health: {}", containerName, e);
            return "ERROR: " + e.getMessage();
        }
    }
}

