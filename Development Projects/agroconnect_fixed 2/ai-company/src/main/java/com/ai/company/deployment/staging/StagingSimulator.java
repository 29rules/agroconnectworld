package com.ai.company.deployment.staging;

import com.ai.company.impact.ImpactMode;
import com.ai.company.impact.ImpactModeManager;
import com.ai.company.tools.code.CodeReaderTool;
import com.ai.company.tools.deployment.DockerComposeTool;
import com.ai.company.tools.deployment.DockerRunnerTool;
import com.ai.company.tools.deployment.HealthCheckTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Staging Simulator
 * 
 * Simulates deployment on local environment for validation.
 * 
 * Capabilities:
 * - Load docker-compose.staging.yml
 * - Spin up containers locally using DockerRunnerTool (only in controlled mode)
 * - Run migrations in dry-run mode
 * - Validate internal API routes between microservices
 * - Validate frontend → gateway → services flow
 * 
 * SAFETY:
 * - Default: DRY-RUN mode (validates without starting containers)
 * - Only starts containers when Impact Mode allows (CONTROLLED_IMPACT or FULL_IMPACT)
 * - All operations logged
 */
public class StagingSimulator {
    
    private static final Logger log = LoggerFactory.getLogger(StagingSimulator.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    private static final String STAGING_COMPOSE_FILE = "ops/docker-compose.staging.yml";
    
    private final DockerComposeTool dockerComposeTool;
    private final DockerRunnerTool dockerRunnerTool;
    private final HealthCheckTool healthCheckTool;
    private final CodeReaderTool codeReader;
    
    public StagingSimulator() {
        this.dockerComposeTool = new DockerComposeTool();
        this.dockerRunnerTool = new DockerRunnerTool();
        this.healthCheckTool = new HealthCheckTool();
        this.codeReader = new CodeReaderTool();
    }
    
    /**
     * Simulates a complete staging deployment.
     * 
     * @param services Comma-separated list of services to simulate (null = all services)
     * @return Staging simulation results
     */
    public StagingSimulationResult simulateStaging(String services) {
        log.info("=".repeat(80));
        log.info("STAGING SIMULATION STARTED");
        log.info("Services: {}", services != null ? services : "all");
        log.info("=".repeat(80));
        
        StagingSimulationResult result = new StagingSimulationResult();
        result.setStartTime(new Date());
        
        try {
            // Step 1: Load and validate docker-compose.staging.yml
            log.info("STEP 1: Loading docker-compose.staging.yml");
            String composeValidation = loadAndValidateComposeFile(result);
            if (composeValidation.contains("ERROR")) {
                result.addError("Failed to load docker-compose.staging.yml: " + composeValidation);
                result.setStatus("FAILED");
                result.setEndTime(new Date());
                return result;
            }
            result.addStepResult("load_compose", composeValidation);
            
            // Step 2: Validate compose file structure
            log.info("STEP 2: Validating compose file structure");
            String structureValidation = dockerComposeTool.validateDockerCompose(STAGING_COMPOSE_FILE);
            result.addStepResult("validate_structure", structureValidation);
            
            // Step 3: Run migrations in dry-run mode
            log.info("STEP 3: Running migrations in dry-run mode");
            String migrationResult = runMigrationsDryRun(result);
            result.addStepResult("migrations", migrationResult);
            
            // Step 4: Spin up containers (if Impact Mode allows)
            log.info("STEP 4: Spinning up containers");
            String containerResult = spinUpContainers(services, result);
            result.addStepResult("containers", containerResult);
            
            // Step 5: Validate internal API routes
            log.info("STEP 5: Validating internal API routes");
            String apiValidation = validateInternalApiRoutes(result);
            result.addStepResult("api_routes", apiValidation);
            
            // Step 6: Validate frontend → gateway → services flow
            log.info("STEP 6: Validating frontend → gateway → services flow");
            String flowValidation = validateFrontendGatewayFlow(result);
            result.addStepResult("frontend_flow", flowValidation);
            
            // Step 7: Health checks
            log.info("STEP 7: Running health checks");
            String healthChecks = runHealthChecks(result);
            result.addStepResult("health_checks", healthChecks);
            
            result.setStatus("COMPLETED");
            result.setEndTime(new Date());
            
            log.info("=".repeat(80));
            log.info("STAGING SIMULATION COMPLETED");
            log.info("Status: {}", result.getStatus());
            log.info("=".repeat(80));
            
        } catch (Exception e) {
            log.error("Error during staging simulation", e);
            result.addError("Staging simulation error: " + e.getMessage());
            result.setStatus("FAILED");
            result.setEndTime(new Date());
        }
        
        return result;
    }
    
    /**
     * Loads and validates docker-compose.staging.yml.
     */
    private String loadAndValidateComposeFile(StagingSimulationResult result) {
        Path composeFile = WORKSPACE_ROOT.resolve(STAGING_COMPOSE_FILE).normalize();
        
        if (!Files.exists(composeFile)) {
            // Try to create a staging compose file from the main one
            log.warn("docker-compose.staging.yml not found, using docker-compose.yml");
            String mainCompose = codeReader.readFile("ops/docker-compose.yml");
            if (mainCompose.startsWith("ERROR")) {
                return "ERROR: Could not find docker-compose.staging.yml or docker-compose.yml";
            }
            result.addInfo("Using docker-compose.yml as staging compose file");
            return "Loaded docker-compose.yml for staging simulation";
        }
        
        String content = codeReader.readFile(STAGING_COMPOSE_FILE);
        if (content.startsWith("ERROR")) {
            return content;
        }
        
        return "Successfully loaded docker-compose.staging.yml";
    }
    
    /**
     * Runs migrations in dry-run mode.
     */
    private String runMigrationsDryRun(StagingSimulationResult result) {
        StringBuilder output = new StringBuilder();
        output.append("=== Migration Dry-Run ===\n");
        output.append("Mode: DRY-RUN (no actual migrations executed)\n\n");
        
        // Check for migration files in services
        String[] services = {"auth-service", "product-service", "supplier-service", 
                            "quote-service", "order-service", "contact-service"};
        
        final int[] migrationCount = {0};
        for (String service : services) {
            Path serviceDir = WORKSPACE_ROOT.resolve("backend").resolve(service);
            if (Files.exists(serviceDir)) {
                // Check for migration files (simplified check)
                try {
                    Files.walk(serviceDir)
                        .filter(p -> p.toString().contains("migration") || 
                                   p.toString().contains("Migration"))
                        .forEach(p -> {
                            migrationCount[0]++;
                            result.addInfo("Found migration: " + p.getFileName());
                        });
                } catch (IOException e) {
                    log.warn("Error checking migrations for service: {}", service, e);
                }
            }
        }
        
        output.append("Migration files found: ").append(migrationCount[0]).append("\n");
        output.append("Status: DRY-RUN - No migrations executed\n");
        output.append("Note: In actual deployment, migrations would be applied here\n");
        
        return output.toString();
    }
    
    /**
     * Spins up containers (if Impact Mode allows).
     */
    private String spinUpContainers(String services, StagingSimulationResult result) {
        ImpactModeManager modeManager = ImpactModeManager.getInstance();
        ImpactMode currentMode = modeManager.getMode();
        
        if (currentMode == ImpactMode.ZERO_IMPACT) {
            // Dry-run mode
            log.info("DRY-RUN: Simulating container startup");
            String simulation = dockerRunnerTool.composeUp(STAGING_COMPOSE_FILE, "staging");
            result.addInfo("Containers simulated (not actually started)");
            return "DRY-RUN MODE:\n" + simulation + "\n\nNote: Containers not actually started in ZERO_IMPACT mode.";
        }
        
        // Actual container startup (CONTROLLED_IMPACT or FULL_IMPACT)
        log.info("Starting containers in {} mode", currentMode);
        String composeResult = dockerRunnerTool.composeUp(STAGING_COMPOSE_FILE, "staging");
        
        if (composeResult.contains("SUCCESS") || composeResult.contains("started")) {
            result.addInfo("Containers started successfully");
            
            // Wait a bit for containers to start
            try {
                Thread.sleep(5000); // Wait 5 seconds for services to initialize
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            result.addWarning("Container startup may have issues: " + composeResult);
        }
        
        return composeResult;
    }
    
    /**
     * Validates internal API routes between microservices.
     */
    private String validateInternalApiRoutes(StagingSimulationResult result) {
        StringBuilder output = new StringBuilder();
        output.append("=== Internal API Route Validation ===\n\n");
        
        // Define expected internal routes
        Map<String, List<String>> serviceRoutes = new HashMap<>();
        serviceRoutes.put("auth-service", Arrays.asList(
            "http://localhost:8081/api/auth/health",
            "http://localhost:8081/actuator/health"
        ));
        serviceRoutes.put("product-service", Arrays.asList(
            "http://localhost:8082/api/products",
            "http://localhost:8082/actuator/health"
        ));
        serviceRoutes.put("gateway", Arrays.asList(
            "http://localhost:8080/actuator/health"
        ));
        
        int successCount = 0;
        int failureCount = 0;
        
        for (Map.Entry<String, List<String>> entry : serviceRoutes.entrySet()) {
            String serviceName = entry.getKey();
            output.append("Service: ").append(serviceName).append("\n");
            
            for (String route : entry.getValue()) {
                try {
                    String healthResult = healthCheckTool.checkServiceHealth(route);
                    
                    if (healthResult.contains("HEALTHY") || healthResult.contains("200")) {
                        output.append("  ✓ ").append(route).append(" - OK\n");
                        successCount++;
                        result.addValidatedRoute(serviceName, route, true);
                    } else {
                        output.append("  ✗ ").append(route).append(" - FAILED\n");
                        failureCount++;
                        result.addValidatedRoute(serviceName, route, false);
                        result.addWarning("Route validation failed: " + route);
                    }
                } catch (Exception e) {
                    output.append("  ✗ ").append(route).append(" - ERROR: ").append(e.getMessage()).append("\n");
                    failureCount++;
                    result.addValidatedRoute(serviceName, route, false);
                    result.addError("Route validation error: " + route + " - " + e.getMessage());
                }
            }
            output.append("\n");
        }
        
        output.append("Summary: ").append(successCount).append(" passed, ").append(failureCount).append(" failed\n");
        
        if (failureCount > 0) {
            result.addWarning("Some API routes failed validation");
        }
        
        return output.toString();
    }
    
    /**
     * Validates frontend → gateway → services flow.
     */
    private String validateFrontendGatewayFlow(StagingSimulationResult result) {
        StringBuilder output = new StringBuilder();
        output.append("=== Frontend → Gateway → Services Flow Validation ===\n\n");
        
        // Test flow: Frontend → Gateway → Auth Service
        output.append("Flow 1: Frontend → Gateway → Auth Service\n");
        String gatewayHealth = healthCheckTool.checkServiceHealth("http://localhost:8080/actuator/health");
        boolean gatewayOk = gatewayHealth.contains("HEALTHY") || gatewayHealth.contains("200");
        
        if (gatewayOk) {
            output.append("  ✓ Gateway is healthy\n");
            
            // Test gateway routing to auth service
            String authViaGateway = testGatewayRoute("/api/auth/health", result);
            if (authViaGateway.contains("OK")) {
                output.append("  ✓ Gateway routes to auth-service correctly\n");
            } else {
                output.append("  ✗ Gateway routing to auth-service failed\n");
                result.addWarning("Gateway routing validation failed");
            }
        } else {
            output.append("  ✗ Gateway is not healthy\n");
            result.addError("Gateway health check failed");
        }
        
        output.append("\n");
        
        // Test flow: Frontend → Gateway → Product Service
        output.append("Flow 2: Frontend → Gateway → Product Service\n");
        if (gatewayOk) {
            String productViaGateway = testGatewayRoute("/api/products", result);
            if (productViaGateway.contains("OK")) {
                output.append("  ✓ Gateway routes to product-service correctly\n");
            } else {
                output.append("  ✗ Gateway routing to product-service failed\n");
                result.addWarning("Gateway routing to product-service failed");
            }
        }
        
        output.append("\n");
        output.append("Flow validation completed\n");
        
        return output.toString();
    }
    
    /**
     * Tests a gateway route.
     */
    private String testGatewayRoute(String path, StagingSimulationResult result) {
        try {
            URL url = new URL("http://localhost:8080" + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode >= 200 && responseCode < 400) {
                return "OK";
            } else {
                return "FAILED (Status: " + responseCode + ")";
            }
        } catch (Exception e) {
            result.addWarning("Gateway route test error: " + path + " - " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Runs health checks on all services.
     */
    private String runHealthChecks(StagingSimulationResult result) {
        StringBuilder output = new StringBuilder();
        output.append("=== Health Checks ===\n\n");
        
        String[] healthUrls = {
            "http://localhost:8080/actuator/health",  // Gateway
            "http://localhost:8081/actuator/health", // Auth Service
            "http://localhost:8082/actuator/health"  // Product Service
        };
        
        int healthyCount = 0;
        int unhealthyCount = 0;
        
        for (String url : healthUrls) {
            String healthResult = healthCheckTool.checkServiceHealth(url);
            output.append(healthResult).append("\n");
            
            if (healthResult.contains("HEALTHY") || healthResult.contains("200")) {
                healthyCount++;
            } else {
                unhealthyCount++;
                result.addWarning("Service unhealthy: " + url);
            }
        }
        
        output.append("\nSummary: ").append(healthyCount).append(" healthy, ")
              .append(unhealthyCount).append(" unhealthy\n");
        
        return output.toString();
    }
    
    /**
     * Staging simulation result.
     */
    public static class StagingSimulationResult {
        private Date startTime;
        private Date endTime;
        private String status; // PENDING, IN_PROGRESS, COMPLETED, FAILED
        private Map<String, String> stepResults;
        private List<String> errors;
        private List<String> warnings;
        private List<String> info;
        private Map<String, List<RouteValidation>> validatedRoutes;
        
        public StagingSimulationResult() {
            this.stepResults = new HashMap<>();
            this.errors = new ArrayList<>();
            this.warnings = new ArrayList<>();
            this.info = new ArrayList<>();
            this.validatedRoutes = new HashMap<>();
            this.status = "PENDING";
        }
        
        public void addStepResult(String step, String result) {
            this.stepResults.put(step, result);
        }
        
        public void addError(String error) {
            this.errors.add(error);
        }
        
        public void addWarning(String warning) {
            this.warnings.add(warning);
        }
        
        public void addInfo(String info) {
            this.info.add(info);
        }
        
        public void addValidatedRoute(String service, String route, boolean valid) {
            this.validatedRoutes.computeIfAbsent(service, k -> new ArrayList<>())
                .add(new RouteValidation(route, valid));
        }
        
        // Getters and Setters
        public Date getStartTime() { return startTime; }
        public void setStartTime(Date startTime) { this.startTime = startTime; }
        public Date getEndTime() { return endTime; }
        public void setEndTime(Date endTime) { this.endTime = endTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Map<String, String> getStepResults() { return stepResults; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
        public List<String> getInfo() { return info; }
        public Map<String, List<RouteValidation>> getValidatedRoutes() { return validatedRoutes; }
        
        public static class RouteValidation {
            private String route;
            private boolean valid;
            
            public RouteValidation(String route, boolean valid) {
                this.route = route;
                this.valid = valid;
            }
            
            public String getRoute() { return route; }
            public boolean isValid() { return valid; }
        }
    }
}

