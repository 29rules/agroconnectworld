package com.ai.company.tools.github;

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
 * Read-only tool for reading GitHub repository structure and files.
 * ZERO-IMPACT MODE: This tool only reads, never modifies.
 */
public class GitHubReaderTool {
    
    private static final Logger log = LoggerFactory.getLogger(GitHubReaderTool.class);
    private final Path repositoryRoot;
    
    public GitHubReaderTool(String repositoryPath) {
        this.repositoryRoot = Paths.get(repositoryPath);
    }
    
    public GitHubReaderTool() {
        // Default to parent directory (AgroConnectWorld root)
        this.repositoryRoot = Paths.get("..").toAbsolutePath().normalize();
    }
    
    @Tool("Read the contents of a file from the repository. Returns file content as string.")
    public String readFile(String filePath) {
        try {
            Path fullPath = repositoryRoot.resolve(filePath).normalize();
            
            // Security: Ensure path is within repository
            if (!fullPath.startsWith(repositoryRoot)) {
                log.warn("Attempted to read file outside repository: {}", filePath);
                return "ERROR: Path outside repository boundaries";
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
    
    @Tool("List all files in a directory. Returns list of file paths.")
    public String listDirectory(String directoryPath) {
        try {
            Path fullPath = repositoryRoot.resolve(directoryPath).normalize();
            
            // Security: Ensure path is within repository
            if (!fullPath.startsWith(repositoryRoot)) {
                log.warn("Attempted to list directory outside repository: {}", directoryPath);
                return "ERROR: Path outside repository boundaries";
            }
            
            if (!Files.exists(fullPath)) {
                return "ERROR: Directory does not exist: " + directoryPath;
            }
            
            if (!Files.isDirectory(fullPath)) {
                return "ERROR: Path is not a directory: " + directoryPath;
            }
            
            try (Stream<Path> paths = Files.walk(fullPath, 1)) {
                List<String> files = paths
                    .filter(Files::isRegularFile)
                    .map(p -> repositoryRoot.relativize(p).toString())
                    .collect(Collectors.toList());
                
                return String.join("\n", files);
            }
        } catch (IOException e) {
            log.error("Error listing directory: {}", directoryPath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    @Tool("Get the repository structure (directory tree). Returns tree structure as string.")
    public String getRepositoryStructure(String basePath) {
        try {
            Path fullPath = repositoryRoot.resolve(basePath).normalize();
            
            if (!fullPath.startsWith(repositoryRoot)) {
                return "ERROR: Path outside repository boundaries";
            }
            
            if (!Files.exists(fullPath) || !Files.isDirectory(fullPath)) {
                return "ERROR: Invalid directory path: " + basePath;
            }
            
            StringBuilder tree = new StringBuilder();
            buildTree(fullPath, repositoryRoot, tree, "", true);
            return tree.toString();
        } catch (IOException e) {
            log.error("Error building repository structure: {}", basePath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    private void buildTree(Path directory, Path root, StringBuilder tree, String prefix, boolean isLast) throws IOException {
        String name = directory.getFileName().toString();
        tree.append(prefix).append(isLast ? "└── " : "├── ").append(name).append("\n");
        
        String newPrefix = prefix + (isLast ? "    " : "│   ");
        
        try (Stream<Path> paths = Files.list(directory)) {
            List<Path> entries = paths
                .filter(Files::isDirectory)
                .sorted()
                .collect(Collectors.toList());
            
            for (int i = 0; i < entries.size(); i++) {
                buildTree(entries.get(i), root, tree, newPrefix, i == entries.size() - 1);
            }
        }
    }
}



