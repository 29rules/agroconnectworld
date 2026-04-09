package com.ai.company.tools.filesystem;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * High-level project summary tool that provides a comprehensive overview
 * of the project structure in a single call, reducing the need for multiple
 * low-level file operations.
 * 
 * ZERO-IMPACT MODE: This tool only reads, never modifies.
 */
public class ProjectSummaryTool {
    
    private static final Logger log = LoggerFactory.getLogger(ProjectSummaryTool.class);
    private final Path basePath;
    
    public ProjectSummaryTool(String basePath) {
        this.basePath = Paths.get(basePath).toAbsolutePath().normalize();
    }
    
    public ProjectSummaryTool() {
        // Default to workspace root
        this.basePath = Paths.get(".").toAbsolutePath().normalize();
    }
    
    /**
     * Get a high-level summary of the project structure, key files, and architecture.
     * This tool reads multiple key files in one call to provide comprehensive context.
     * 
     * @param projectArea Optional: "backend", "frontend", "ai-company", or "all" (default)
     * @return A structured summary of the project
     */
    @Tool("Get a high-level project summary. Reads key configuration files and structure. " +
          "Use this instead of making many individual file reads. " +
          "Parameter: 'backend', 'frontend', 'ai-company', or 'all' (default).")
    public String getProjectSummary(String projectArea) {
        try {
            if (projectArea == null || projectArea.trim().isEmpty()) {
                projectArea = "all";
            }
            projectArea = projectArea.toLowerCase().trim();
            
            StringBuilder summary = new StringBuilder();
            summary.append("=== PROJECT SUMMARY ===\n\n");
            
            // Read key files based on area
            if (projectArea.equals("all") || projectArea.equals("backend")) {
                summary.append("--- BACKEND SERVICES ---\n");
                summary.append(readKeyFile("ops/docker-compose.yml", "Docker Compose configuration"));
                summary.append("\n");
            }
            
            if (projectArea.equals("all") || projectArea.equals("ai-company")) {
                summary.append("--- AI COMPANY LAYER ---\n");
                summary.append(readKeyFile("ai-company/pom.xml", "AI Company Maven configuration"));
                summary.append("\n");
            }
            
            if (projectArea.equals("all") || projectArea.equals("frontend")) {
                summary.append("--- FRONTEND ---\n");
                summary.append(readKeyFile("frontend/package.json", "Frontend package configuration"));
                summary.append("\n");
            }
            
            // Add directory structure overview
            summary.append("--- DIRECTORY STRUCTURE ---\n");
            summary.append(getDirectoryOverview());
            
            return summary.toString();
        } catch (Exception e) {
            log.error("Error generating project summary", e);
            return "ERROR: Could not generate project summary: " + e.getMessage();
        }
    }
    
    /**
     * Read a key file and return its content with a label.
     */
    private String readKeyFile(String relativePath, String label) {
        try {
            Path fullPath = basePath.resolve(relativePath).normalize();
            
            if (!fullPath.startsWith(basePath)) {
                return label + ": Path outside allowed boundaries\n";
            }
            
            if (!Files.exists(fullPath)) {
                return label + ": File not found\n";
            }
            
            String content = Files.readString(fullPath);
            // Limit content size to prevent huge outputs
            int maxLength = 2000;
            if (content.length() > maxLength) {
                content = content.substring(0, maxLength) + "\n... (truncated)";
            }
            
            return label + ":\n" + content + "\n";
        } catch (IOException e) {
            log.warn("Could not read key file: {}", relativePath, e);
            return label + ": Could not read file (" + e.getMessage() + ")\n";
        }
    }
    
    /**
     * Get a high-level directory overview.
     */
    private String getDirectoryOverview() {
        List<String> topLevelDirs = new ArrayList<>();
        
        try {
            Path root = basePath;
            if (Files.exists(root) && Files.isDirectory(root)) {
                Files.list(root)
                    .filter(Files::isDirectory)
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .forEach(topLevelDirs::add);
            }
        } catch (IOException e) {
            log.warn("Could not list directories", e);
            return "Could not list directory structure\n";
        }
        
        return "Top-level directories: " + String.join(", ", topLevelDirs) + "\n";
    }
}



