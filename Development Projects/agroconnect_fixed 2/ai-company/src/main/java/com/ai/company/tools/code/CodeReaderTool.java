package com.ai.company.tools.code;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Code Reader Tool - Read-only file and directory operations.
 * 
 * This tool provides safe, read-only access to code files.
 * It can read individual files or recursively read directory contents.
 * 
 * SAFETY:
 * - Read-only operations only
 * - No modifications to files
 * - Path validation ensures access only to permitted directories
 * 
 * ZERO-IMPACT MODE: This tool only reads, never modifies.
 */
@Component
public class CodeReaderTool {
    
    private static final Logger log = LoggerFactory.getLogger(CodeReaderTool.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    private static final Path BACKEND_DIR = WORKSPACE_ROOT.resolve("backend").normalize();
    private static final Path FRONTEND_DIR = WORKSPACE_ROOT.resolve("frontend").normalize();
    private static final Path AI_COMPANY_DIR = WORKSPACE_ROOT.resolve("ai-company").normalize();
    
    /**
     * Reads a file by path and returns its content as a string.
     * 
     * @param filePath Relative path from workspace root (e.g., "backend/gateway/pom.xml")
     * @return File content as string, or error message if file cannot be read
     */
    @Tool("Read a file by path. Returns file content as string. Path should be relative to workspace root (e.g., 'backend/gateway/pom.xml').")
    public String readFile(String filePath) {
        try {
            Path fullPath = WORKSPACE_ROOT.resolve(filePath).normalize();
            
            // Validate path is within permitted directories
            if (!isPathPermitted(fullPath)) {
                String warning = String.format(
                    "WARNING: File path '%s' is outside permitted directories (backend/, frontend/, ai-company/). Access denied.",
                    filePath
                );
                log.warn(warning);
                return warning;
            }
            
            if (!Files.exists(fullPath)) {
                return "ERROR: File does not exist: " + filePath;
            }
            
            if (!Files.isRegularFile(fullPath)) {
                return "ERROR: Path is not a regular file: " + filePath;
            }
            
            return Files.readString(fullPath);
            
        } catch (IOException e) {
            log.error("Error reading file: {}", filePath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Reads a folder recursively and returns a list of all files with their contents.
     * 
     * @param folderPath Relative path from workspace root (e.g., "backend/gateway")
     * @return Formatted string containing all files and their contents
     */
    @Tool("Read a folder recursively. Returns all files in the folder and subfolders with their contents. Path should be relative to workspace root.")
    public String readFolder(String folderPath) {
        try {
            Path fullPath = WORKSPACE_ROOT.resolve(folderPath).normalize();
            
            // Validate path is within permitted directories
            if (!isPathPermitted(fullPath)) {
                String warning = String.format(
                    "WARNING: Folder path '%s' is outside permitted directories (backend/, frontend/, ai-company/). Access denied.",
                    folderPath
                );
                log.warn(warning);
                return warning;
            }
            
            if (!Files.exists(fullPath)) {
                return "ERROR: Folder does not exist: " + folderPath;
            }
            
            if (!Files.isDirectory(fullPath)) {
                return "ERROR: Path is not a directory: " + folderPath;
            }
            
            List<String> results = new ArrayList<>();
            results.add("=== Folder Contents: " + folderPath + " ===\n");
            
            try (Stream<Path> paths = Files.walk(fullPath)) {
                paths.filter(Files::isRegularFile)
                    .sorted()
                    .forEach(filePath -> {
                        try {
                            String relativePath = WORKSPACE_ROOT.relativize(filePath).toString();
                            String content = Files.readString(filePath);
                            results.add("--- File: " + relativePath + " ---");
                            results.add(content);
                            results.add("\n");
                        } catch (IOException e) {
                            log.warn("Error reading file in folder: {}", filePath, e);
                            results.add("--- File: " + filePath + " --- (ERROR: " + e.getMessage() + ")\n");
                        }
                    });
            }
            
            return String.join("\n", results);
            
        } catch (IOException e) {
            log.error("Error reading folder: {}", folderPath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Lists files in a directory without reading their contents.
     * 
     * @param folderPath Relative path from workspace root
     * @return List of file paths
     */
    @Tool("List files in a directory. Returns list of file paths without reading contents. Path should be relative to workspace root.")
    public String listFiles(String folderPath) {
        try {
            Path fullPath = WORKSPACE_ROOT.resolve(folderPath).normalize();
            
            // Validate path is within permitted directories
            if (!isPathPermitted(fullPath)) {
                String warning = String.format(
                    "WARNING: Folder path '%s' is outside permitted directories (backend/, frontend/, ai-company/). Access denied.",
                    folderPath
                );
                log.warn(warning);
                return warning;
            }
            
            if (!Files.exists(fullPath)) {
                return "ERROR: Folder does not exist: " + folderPath;
            }
            
            if (!Files.isDirectory(fullPath)) {
                return "ERROR: Path is not a directory: " + folderPath;
            }
            
            List<String> files = new ArrayList<>();
            try (Stream<Path> paths = Files.list(fullPath)) {
                paths.sorted()
                    .forEach(path -> {
                        String name = path.getFileName().toString();
                        if (Files.isDirectory(path)) {
                            files.add(name + "/");
                        } else {
                            files.add(name);
                        }
                    });
            }
            
            return String.join("\n", files);
            
        } catch (IOException e) {
            log.error("Error listing files: {}", folderPath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Checks if a path is within permitted directories.
     */
    private boolean isPathPermitted(Path path) {
        return path.startsWith(BACKEND_DIR) || 
               path.startsWith(FRONTEND_DIR) || 
               path.startsWith(AI_COMPANY_DIR);
    }
}

