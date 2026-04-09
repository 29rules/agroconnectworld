package com.ai.company.tools.code;

import dev.langchain4j.agent.tool.Tool;
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
 * Test Runner Tool - Execute tests for backend and frontend.
 * 
 * This tool runs tests:
 * - Backend: mvn test
 * - Frontend: npm run test
 * 
 * SAFETY:
 * - Read-only operation (runs tests, doesn't modify code)
 * - Returns console output
 * - No Controlled-Impact Mode required (tests are safe)
 */
public class TestRunnerTool {
    
    private static final Logger log = LoggerFactory.getLogger(TestRunnerTool.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    private static final Path BACKEND_DIR = WORKSPACE_ROOT.resolve("backend").normalize();
    private static final Path FRONTEND_DIR = WORKSPACE_ROOT.resolve("frontend").normalize();
    
    /**
     * Runs Maven tests for backend services.
     * 
     * @param servicePath Relative path to service (e.g., "backend/gateway" or "backend" for all)
     * @return Test execution output
     */
    @Tool("Run Maven tests for backend service. Provide service path (e.g., 'backend/gateway') or 'backend' for all services. Returns test output.")
    public String runBackendTests(String servicePath) {
        try {
            Path fullPath = WORKSPACE_ROOT.resolve(servicePath).normalize();
            
            // Validate path
            if (!fullPath.startsWith(BACKEND_DIR)) {
                return "ERROR: Path must be within backend/ directory";
            }
            
            if (!java.nio.file.Files.exists(fullPath)) {
                return "ERROR: Path does not exist: " + servicePath;
            }
            
            // Check if pom.xml exists
            Path pomPath = fullPath.resolve("pom.xml");
            if (!java.nio.file.Files.exists(pomPath)) {
                return "ERROR: pom.xml not found in: " + servicePath;
            }
            
            // Run mvn test
            ProcessBuilder pb = new ProcessBuilder("mvn", "test", "-q");
            pb.directory(fullPath.toFile());
            Process process = pb.start();
            
            // Capture output
            List<String> output = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(output::add);
            }
            
            // Capture error output
            List<String> errors = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))) {
                reader.lines().forEach(errors::add);
            }
            
            int exitCode = process.waitFor();
            
            // Combine output
            List<String> allOutput = new ArrayList<>();
            allOutput.add("=== Maven Test Output ===");
            allOutput.add("Service: " + servicePath);
            allOutput.add("Exit Code: " + exitCode);
            allOutput.add("");
            if (!output.isEmpty()) {
                allOutput.add("--- Standard Output ---");
                allOutput.addAll(output);
            }
            if (!errors.isEmpty()) {
                allOutput.add("");
                allOutput.add("--- Error Output ---");
                allOutput.addAll(errors);
            }
            
            log.info("Backend tests executed for: {} (exit code: {})", servicePath, exitCode);
            return String.join("\n", allOutput);
            
        } catch (IOException | InterruptedException e) {
            log.error("Error running backend tests: {}", servicePath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Runs npm tests for frontend.
     * 
     * @return Test execution output
     */
    @Tool("Run npm tests for frontend. Returns test output.")
    public String runFrontendTests() {
        try {
            if (!java.nio.file.Files.exists(FRONTEND_DIR)) {
                return "ERROR: Frontend directory does not exist";
            }
            
            // Check if package.json exists
            Path packageJson = FRONTEND_DIR.resolve("package.json");
            if (!java.nio.file.Files.exists(packageJson)) {
                return "ERROR: package.json not found in frontend/";
            }
            
            // Run npm test
            ProcessBuilder pb = new ProcessBuilder("npm", "run", "test");
            pb.directory(FRONTEND_DIR.toFile());
            Process process = pb.start();
            
            // Capture output
            List<String> output = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(output::add);
            }
            
            // Capture error output
            List<String> errors = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))) {
                reader.lines().forEach(errors::add);
            }
            
            int exitCode = process.waitFor();
            
            // Combine output
            List<String> allOutput = new ArrayList<>();
            allOutput.add("=== NPM Test Output ===");
            allOutput.add("Exit Code: " + exitCode);
            allOutput.add("");
            if (!output.isEmpty()) {
                allOutput.add("--- Standard Output ---");
                allOutput.addAll(output);
            }
            if (!errors.isEmpty()) {
                allOutput.add("");
                allOutput.add("--- Error Output ---");
                allOutput.addAll(errors);
            }
            
            log.info("Frontend tests executed (exit code: {})", exitCode);
            return String.join("\n", allOutput);
            
        } catch (IOException | InterruptedException e) {
            log.error("Error running frontend tests", e);
            return "ERROR: " + e.getMessage();
        }
    }
}



