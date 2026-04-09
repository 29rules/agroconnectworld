package com.ai.company.tools.filesystem;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Read-only tool for reading filesystem structure and files.
 * ZERO-IMPACT MODE: This tool only reads, never modifies.
 */
public class FileSystemReaderTool {
    
    private static final Logger log = LoggerFactory.getLogger(FileSystemReaderTool.class);
    private final Path basePath;
    
    public FileSystemReaderTool(String basePath) {
        this.basePath = Paths.get(basePath).toAbsolutePath().normalize();
    }
    
    public FileSystemReaderTool() {
        // Default to current directory
        this.basePath = Paths.get(".").toAbsolutePath().normalize();
    }
    
    @Tool("Read a file from the filesystem. Returns file content as string.")
    public String readFile(String filePath) {
        try {
            Path fullPath = basePath.resolve(filePath).normalize();
            
            // Security: Ensure path is within base
            if (!fullPath.startsWith(basePath)) {
                log.warn("Attempted to read file outside base path: {}", filePath);
                return "ERROR: Path outside allowed boundaries";
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
    
    @Tool("List files in a directory. Returns list of file and directory names.")
    public String listFiles(String directoryPath) {
        try {
            Path fullPath = basePath.resolve(directoryPath).normalize();
            
            if (!fullPath.startsWith(basePath)) {
                return "ERROR: Path outside allowed boundaries";
            }
            
            if (!Files.exists(fullPath)) {
                return "ERROR: Directory does not exist: " + directoryPath;
            }
            
            if (!Files.isDirectory(fullPath)) {
                return "ERROR: Path is not a directory: " + directoryPath;
            }
            
            try (Stream<Path> paths = Files.list(fullPath)) {
                List<String> entries = paths
                    .map(p -> {
                        String name = p.getFileName().toString();
                        return Files.isDirectory(p) ? name + "/" : name;
                    })
                    .sorted()
                    .collect(Collectors.toList());
                
                return String.join("\n", entries);
            }
        } catch (IOException e) {
            log.error("Error listing files: {}", directoryPath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    @Tool("Check if a file or directory exists. Returns true or false.")
    public String fileExists(String filePath) {
        try {
            Path fullPath = basePath.resolve(filePath).normalize();
            
            if (!fullPath.startsWith(basePath)) {
                return "false";
            }
            
            return Files.exists(fullPath) ? "true" : "false";
        } catch (Exception e) {
            log.error("Error checking file existence: {}", filePath, e);
            return "false";
        }
    }
    
    @Tool("Get file metadata (size, last modified). Returns metadata as string.")
    public String getFileMetadata(String filePath) {
        try {
            Path fullPath = basePath.resolve(filePath).normalize();
            
            if (!fullPath.startsWith(basePath) || !Files.exists(fullPath)) {
                return "ERROR: File does not exist or path invalid";
            }
            
            long size = Files.size(fullPath);
            String lastModified = Files.getLastModifiedTime(fullPath).toString();
            boolean isDirectory = Files.isDirectory(fullPath);
            
            return String.format("Path: %s\nSize: %d bytes\nLast Modified: %s\nIs Directory: %s",
                filePath, size, lastModified, isDirectory);
        } catch (IOException e) {
            log.error("Error getting file metadata: {}", filePath, e);
            return "ERROR: " + e.getMessage();
        }
    }
}



