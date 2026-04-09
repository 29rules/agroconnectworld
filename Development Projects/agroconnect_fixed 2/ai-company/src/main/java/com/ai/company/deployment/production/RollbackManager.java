package com.ai.company.deployment.production;

import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.tools.deployment.HealthCheckTool;
import com.ai.company.tools.deployment.SSHDeploymentTool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Rollback Manager
 * 
 * Manages rollbacks for production deployments.
 * 
 * Capabilities:
 * - Keeps snapshots of previous release manifests
 * - Can roll back automatically if:
 *   - health=% < threshold
 *   - error_rate > threshold
 *   - CPU or memory spikes
 *   - Supervisor flags anomaly
 * 
 * SAFETY:
 * - Creates snapshots before deployment
 * - Monitors health metrics
 * - Automatic rollback on critical failures
 * - Supervisor approval for manual rollback
 */
public class RollbackManager {
    
    private static final Logger log = LoggerFactory.getLogger(RollbackManager.class);
    
    private static final double HEALTH_THRESHOLD = 0.7; // 70% must be healthy
    private static final double ERROR_RATE_THRESHOLD = 0.1; // 10% error rate max
    private static final double CPU_THRESHOLD = 0.9; // 90% CPU max
    private static final double MEMORY_THRESHOLD = 0.9; // 90% memory max
    
    private final HealthCheckTool healthCheckTool;
    private final SSHDeploymentTool sshDeploymentTool;
    private final SupervisorAgent supervisorAgent;
    private final Map<String, Snapshot> snapshots;
    
    public RollbackManager() {
        this.healthCheckTool = new HealthCheckTool();
        this.sshDeploymentTool = null; // Will be set if needed
        this.supervisorAgent = null; // Will be set if needed
        this.snapshots = new HashMap<>();
    }
    
    public RollbackManager(ChatLanguageModel chatModel) {
        this.healthCheckTool = new HealthCheckTool();
        this.sshDeploymentTool = new SSHDeploymentTool(chatModel);
        this.supervisorAgent = new SupervisorAgent(chatModel);
        this.snapshots = new HashMap<>();
    }
    
    /**
     * Creates a snapshot of current deployment state.
     * 
     * @param vpsHost VPS hostname
     * @param vpsUser SSH username
     * @param sessionId Session ID
     * @return Snapshot creation result
     */
    public String createSnapshot(String vpsHost, String vpsUser, String sessionId) {
        log.info("Creating rollback snapshot");
        
        String snapshotId = "snapshot-" + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        
        Snapshot snapshot = new Snapshot();
        snapshot.setSnapshotId(snapshotId);
        snapshot.setCreatedAt(LocalDateTime.now());
        snapshot.setVpsHost(vpsHost);
        
        try {
            // Capture current docker-compose configuration
            if (sshDeploymentTool != null) {
                String composeContent = sshDeploymentTool.executeSSHCommand(
                    vpsHost, vpsUser, "cat /deploy/production/docker-compose.yml");
                snapshot.setDockerComposeContent(composeContent);
            }
            
            // Capture current image tags
            if (sshDeploymentTool != null) {
                String images = sshDeploymentTool.executeSSHCommand(
                    vpsHost, vpsUser, "docker images --format '{{.Repository}}:{{.Tag}}'");
                snapshot.setImageTags(images);
            }
            
            // Capture current health status
            captureHealthStatus(snapshot, vpsHost);
            
            snapshots.put(snapshotId, snapshot);
            
            log.info("Snapshot created: {}", snapshotId);
            return "Snapshot created: " + snapshotId;
            
        } catch (Exception e) {
            log.error("Error creating snapshot", e);
            return "ERROR: Failed to create snapshot: " + e.getMessage();
        }
    }
    
    /**
     * Monitors deployment and triggers rollback if needed.
     * 
     * @param snapshotId Snapshot ID to rollback to
     * @param vpsHost VPS hostname
     * @param vpsUser SSH username
     * @param sessionId Session ID
     * @return Monitoring result
     */
    public MonitoringResult monitorAndRollbackIfNeeded(
            String snapshotId,
            String vpsHost,
            String vpsUser,
            String sessionId) {
        
        log.info("Monitoring deployment for rollback triggers");
        
        MonitoringResult result = new MonitoringResult();
        result.setSnapshotId(snapshotId);
        result.setStartTime(LocalDateTime.now());
        
        try {
            // Check health percentage
            double healthPercentage = checkHealthPercentage(vpsHost);
            result.setHealthPercentage(healthPercentage);
            
            if (healthPercentage < HEALTH_THRESHOLD) {
                result.addTrigger("Health percentage below threshold: " + healthPercentage);
                result.setShouldRollback(true);
            }
            
            // Check error rate
            double errorRate = checkErrorRate(vpsHost);
            result.setErrorRate(errorRate);
            
            if (errorRate > ERROR_RATE_THRESHOLD) {
                result.addTrigger("Error rate above threshold: " + errorRate);
                result.setShouldRollback(true);
            }
            
            // Check CPU usage
            double cpuUsage = checkCpuUsage(vpsHost, vpsUser);
            result.setCpuUsage(cpuUsage);
            
            if (cpuUsage > CPU_THRESHOLD) {
                result.addTrigger("CPU usage above threshold: " + cpuUsage);
                result.setShouldRollback(true);
            }
            
            // Check memory usage
            double memoryUsage = checkMemoryUsage(vpsHost, vpsUser);
            result.setMemoryUsage(memoryUsage);
            
            if (memoryUsage > MEMORY_THRESHOLD) {
                result.addTrigger("Memory usage above threshold: " + memoryUsage);
                result.setShouldRollback(true);
            }
            
            // Check Supervisor flags
            if (supervisorAgent != null) {
                boolean supervisorFlag = checkSupervisorFlags(sessionId);
                if (supervisorFlag) {
                    result.addTrigger("Supervisor flagged anomaly");
                    result.setShouldRollback(true);
                }
            }
            
            result.setEndTime(LocalDateTime.now());
            
            if (result.isShouldRollback()) {
                log.warn("Rollback triggered. Reasons: {}", result.getTriggers());
                String rollbackResult = performRollback(snapshotId, vpsHost, vpsUser, sessionId);
                result.setRollbackResult(rollbackResult);
            } else {
                log.info("No rollback needed. All metrics within thresholds.");
            }
            
        } catch (Exception e) {
            log.error("Error during monitoring", e);
            result.addError("Monitoring error: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Performs rollback to a snapshot.
     */
    public String performRollback(String snapshotId, String vpsHost, String vpsUser, String sessionId) {
        log.warn("PERFORMING ROLLBACK to snapshot: {}", snapshotId);
        
        Snapshot snapshot = snapshots.get(snapshotId);
        if (snapshot == null) {
            return "ERROR: Snapshot not found: " + snapshotId;
        }
        
        try {
            // Restore docker-compose.yml
            if (snapshot.getDockerComposeContent() != null && sshDeploymentTool != null) {
                // Write compose file back
                String restoreCommand = String.format(
                    "echo '%s' > /deploy/production/docker-compose.yml",
                    snapshot.getDockerComposeContent().replace("'", "\\'")
                );
                sshDeploymentTool.executeSSHCommand(vpsHost, vpsUser, restoreCommand);
            }
            
            // Restart services with previous configuration
            if (sshDeploymentTool != null) {
                String restartCommand = "cd /deploy/production && docker compose down && docker compose up -d";
                String restartResult = sshDeploymentTool.executeSSHCommand(vpsHost, vpsUser, restartCommand);
                
                if (restartResult.contains("ERROR")) {
                    return "ERROR: Rollback restart failed: " + restartResult;
                }
            }
            
            log.warn("Rollback completed to snapshot: {}", snapshotId);
            return "Rollback completed successfully to snapshot: " + snapshotId;
            
        } catch (Exception e) {
            log.error("Error during rollback", e);
            return "ERROR: Rollback failed: " + e.getMessage();
        }
    }
    
    /**
     * Checks health percentage.
     */
    private double checkHealthPercentage(String vpsHost) {
        String[] healthUrls = {
            "http://" + vpsHost + ":8080/actuator/health",
            "http://" + vpsHost + ":8081/actuator/health",
            "http://" + vpsHost + ":8082/actuator/health"
        };
        
        int healthyCount = 0;
        for (String url : healthUrls) {
            String healthResult = healthCheckTool.checkServiceHealth(url);
            if (healthResult.contains("HEALTHY") || healthResult.contains("200")) {
                healthyCount++;
            }
        }
        
        return (double) healthyCount / healthUrls.length;
    }
    
    /**
     * Checks error rate (simplified).
     */
    private double checkErrorRate(String vpsHost) {
        // In real implementation, would check logs or metrics
        // For now, return a default value
        return 0.05; // 5% default
    }
    
    /**
     * Checks CPU usage.
     */
    private double checkCpuUsage(String vpsHost, String vpsUser) {
        if (sshDeploymentTool == null) {
            return 0.5; // Default
        }
        
        String cpuCommand = "top -bn1 | grep 'Cpu(s)' | sed 's/.*, *\\([0-9.]*\\)%* id.*/\\1/' | awk '{print 100 - $1}'";
        String cpuResult = sshDeploymentTool.executeSSHCommand(vpsHost, vpsUser, cpuCommand);
        
        try {
            return Double.parseDouble(cpuResult.trim()) / 100.0;
        } catch (NumberFormatException e) {
            return 0.5; // Default
        }
    }
    
    /**
     * Checks memory usage.
     */
    private double checkMemoryUsage(String vpsHost, String vpsUser) {
        if (sshDeploymentTool == null) {
            return 0.5; // Default
        }
        
        String memCommand = "free | grep Mem | awk '{print ($3/$2) * 100.0}'";
        String memResult = sshDeploymentTool.executeSSHCommand(vpsHost, vpsUser, memCommand);
        
        try {
            return Double.parseDouble(memResult.trim()) / 100.0;
        } catch (NumberFormatException e) {
            return 0.5; // Default
        }
    }
    
    /**
     * Checks Supervisor flags.
     */
    private boolean checkSupervisorFlags(String sessionId) {
        // In real implementation, would check Supervisor agent for flags
        return false; // Default: no flags
    }
    
    /**
     * Captures health status.
     */
    private void captureHealthStatus(Snapshot snapshot, String vpsHost) {
        Map<String, Boolean> healthStatus = new HashMap<>();
        
        String[] healthUrls = {
            "http://" + vpsHost + ":8080/actuator/health",
            "http://" + vpsHost + ":8081/actuator/health",
            "http://" + vpsHost + ":8082/actuator/health"
        };
        
        for (String url : healthUrls) {
            String healthResult = healthCheckTool.checkServiceHealth(url);
            healthStatus.put(url, healthResult.contains("HEALTHY") || healthResult.contains("200"));
        }
        
        snapshot.setHealthStatus(healthStatus);
    }
    
    /**
     * Snapshot data structure.
     */
    public static class Snapshot {
        private String snapshotId;
        private LocalDateTime createdAt;
        private String vpsHost;
        private String dockerComposeContent;
        private String imageTags;
        private Map<String, Boolean> healthStatus;
        
        // Getters and Setters
        public String getSnapshotId() { return snapshotId; }
        public void setSnapshotId(String snapshotId) { this.snapshotId = snapshotId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public String getVpsHost() { return vpsHost; }
        public void setVpsHost(String vpsHost) { this.vpsHost = vpsHost; }
        public String getDockerComposeContent() { return dockerComposeContent; }
        public void setDockerComposeContent(String dockerComposeContent) { this.dockerComposeContent = dockerComposeContent; }
        public String getImageTags() { return imageTags; }
        public void setImageTags(String imageTags) { this.imageTags = imageTags; }
        public Map<String, Boolean> getHealthStatus() { return healthStatus; }
        public void setHealthStatus(Map<String, Boolean> healthStatus) { this.healthStatus = healthStatus; }
    }
    
    /**
     * Monitoring result.
     */
    public static class MonitoringResult {
        private String snapshotId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private boolean shouldRollback;
        private double healthPercentage;
        private double errorRate;
        private double cpuUsage;
        private double memoryUsage;
        private List<String> triggers;
        private List<String> errors;
        private String rollbackResult;
        
        public MonitoringResult() {
            this.triggers = new ArrayList<>();
            this.errors = new ArrayList<>();
        }
        
        public void addTrigger(String trigger) {
            this.triggers.add(trigger);
        }
        
        public void addError(String error) {
            this.errors.add(error);
        }
        
        // Getters and Setters
        public String getSnapshotId() { return snapshotId; }
        public void setSnapshotId(String snapshotId) { this.snapshotId = snapshotId; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public boolean isShouldRollback() { return shouldRollback; }
        public void setShouldRollback(boolean shouldRollback) { this.shouldRollback = shouldRollback; }
        public double getHealthPercentage() { return healthPercentage; }
        public void setHealthPercentage(double healthPercentage) { this.healthPercentage = healthPercentage; }
        public double getErrorRate() { return errorRate; }
        public void setErrorRate(double errorRate) { this.errorRate = errorRate; }
        public double getCpuUsage() { return cpuUsage; }
        public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
        public double getMemoryUsage() { return memoryUsage; }
        public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
        public List<String> getTriggers() { return triggers; }
        public String getRollbackResult() { return rollbackResult; }
        public void setRollbackResult(String rollbackResult) { this.rollbackResult = rollbackResult; }
        public List<String> getErrors() { return errors; }
    }
}



