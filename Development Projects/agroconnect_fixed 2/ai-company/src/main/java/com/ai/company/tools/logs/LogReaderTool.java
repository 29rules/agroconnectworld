package com.ai.company.tools.logs;

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
import java.util.stream.Collectors;

/**
 * Read-only tool for reading application and container logs.
 * ZERO-IMPACT MODE: This tool only reads, never modifies logs.
 */
public class LogReaderTool {
    
    private static final Logger log = LoggerFactory.getLogger(LogReaderTool.class);
    
    @Tool("Read logs from a Docker container. Returns last N lines of logs.")
    public String readContainerLogs(String containerName, int lines) {
        try {
            Process process = new ProcessBuilder("docker", "logs", 
                "--tail", String.valueOf(lines), containerName)
                .start();
            
            List<String> logLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logLines.add(line);
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "ERROR: Container not found or Docker command failed";
            }
            
            return logLines.isEmpty() 
                ? "No logs available for container: " + containerName
                : String.join("\n", logLines);
        } catch (IOException | InterruptedException e) {
            log.error("Error reading container logs: {}", containerName, e);
            return "ERROR: " + e.getMessage() + " (Docker may not be available)";
        }
    }
    
    @Tool("Read logs from a Docker container with filter. Returns logs matching the filter pattern.")
    public String readContainerLogsFiltered(String containerName, String filter, int lines) {
        try {
            Process process = new ProcessBuilder("docker", "logs", 
                "--tail", String.valueOf(lines), containerName)
                .start();
            
            List<String> logLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(filter)) {
                        logLines.add(line);
                    }
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "ERROR: Container not found or Docker command failed";
            }
            
            return logLines.isEmpty() 
                ? "No logs matching filter '" + filter + "' for container: " + containerName
                : String.join("\n", logLines);
        } catch (IOException | InterruptedException e) {
            log.error("Error reading filtered container logs: {}", containerName, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    @Tool("Read application log file from filesystem. Returns log file content.")
    public String readLogFile(String logFilePath) {
        try {
            Path logPath = Paths.get(logFilePath);
            
            if (!Files.exists(logPath)) {
                return "ERROR: Log file does not exist: " + logFilePath;
            }
            
            if (!Files.isRegularFile(logPath)) {
                return "ERROR: Path is not a regular file: " + logFilePath;
            }
            
            // Read last 1000 lines to avoid memory issues
            List<String> allLines = Files.readAllLines(logPath);
            int startIndex = Math.max(0, allLines.size() - 1000);
            
            return allLines.subList(startIndex, allLines.size())
                .stream()
                .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error("Error reading log file: {}", logFilePath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    @Tool("Search for error patterns in container logs. Returns lines containing ERROR or exception.")
    public String searchErrors(String containerName, int lines) {
        try {
            Process process = new ProcessBuilder("docker", "logs", 
                "--tail", String.valueOf(lines), containerName)
                .start();
            
            List<String> errorLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String upperLine = line.toUpperCase();
                    if (upperLine.contains("ERROR") || 
                        upperLine.contains("EXCEPTION") || 
                        upperLine.contains("FAILED") ||
                        upperLine.contains("FATAL")) {
                        errorLines.add(line);
                    }
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "ERROR: Container not found or Docker command failed";
            }
            
            return errorLines.isEmpty() 
                ? "No errors found in logs for container: " + containerName
                : String.join("\n", errorLines);
        } catch (IOException | InterruptedException e) {
            log.error("Error searching for errors: {}", containerName, e);
            return "ERROR: " + e.getMessage();
        }
    }
}



