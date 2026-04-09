package com.ai.company.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Loads knowledge base from Markdown files.
 * 
 * This loader reads Markdown files from the /ai-company/knowledge directory
 * and loads them into the memory system. Each file is treated as a knowledge
 * document that can be recalled by agents.
 * 
 * The loader:
 * - Scans the knowledge directory for .md files
 * - Reads file contents
 * - Optionally splits large files into chunks
 * - Stores them in the memory system with appropriate metadata
 */
public class KnowledgeBaseLoader {
    
    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseLoader.class);
    
    private final Path knowledgeBasePath;
    private final AgentMemoryService memoryService;
    
    /**
     * Creates a new knowledge base loader.
     * 
     * @param knowledgeBasePath The path to the knowledge base directory
     * @param memoryService The memory service to use for storing knowledge
     */
    public KnowledgeBaseLoader(String knowledgeBasePath, AgentMemoryService memoryService) {
        this.knowledgeBasePath = Paths.get(knowledgeBasePath);
        this.memoryService = memoryService;
        log.info("Initialized KnowledgeBaseLoader with path: {}", knowledgeBasePath);
    }
    
    /**
     * Creates a new knowledge base loader with default path.
     * 
     * @param memoryService The memory service to use
     */
    public KnowledgeBaseLoader(AgentMemoryService memoryService) {
        this("knowledge", memoryService);
    }
    
    /**
     * Loads all Markdown files from the knowledge base directory.
     * 
     * @return List of loaded knowledge entries
     */
    public List<MemoryEntry> loadAll() {
        log.info("Loading knowledge base from: {}", knowledgeBasePath);
        List<MemoryEntry> loadedEntries = new ArrayList<>();
        
        if (!Files.exists(knowledgeBasePath) || !Files.isDirectory(knowledgeBasePath)) {
            log.warn("Knowledge base directory does not exist: {}", knowledgeBasePath);
            return loadedEntries;
        }
        
        try (Stream<Path> paths = Files.walk(knowledgeBasePath)) {
            paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".md"))
                .forEach(path -> {
                    try {
                        List<MemoryEntry> entries = loadFile(path);
                        loadedEntries.addAll(entries);
                        log.debug("Loaded {} entries from: {}", entries.size(), path);
                    } catch (IOException e) {
                        log.error("Failed to load file: {}", path, e);
                    }
                });
        } catch (IOException e) {
            log.error("Failed to walk knowledge base directory", e);
        }
        
        log.info("Loaded {} knowledge entries from {} files", loadedEntries.size(), 
            loadedEntries.stream().mapToInt(e -> 1).sum());
        return loadedEntries;
    }
    
    /**
     * Loads a single Markdown file.
     * 
     * Large files are split into chunks for better retrieval.
     * 
     * @param filePath The path to the Markdown file
     * @return List of memory entries created from the file
     * @throws IOException If file cannot be read
     */
    public List<MemoryEntry> loadFile(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        String fileName = filePath.getFileName().toString();
        String category = extractCategory(fileName);
        
        // Split large files into chunks (max 2000 characters per chunk)
        List<String> chunks = splitIntoChunks(content, 2000);
        
        List<MemoryEntry> entries = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            String chunkId = chunks.size() > 1 ? String.format("%s-chunk-%d", fileName, i + 1) : fileName;
            
            MemoryEntry entry = memoryService.saveMemory(
                "knowledge_base",
                chunk,
                "knowledge",
                category
            );
            entries.add(entry);
        }
        
        return entries;
    }
    
    /**
     * Extracts category from filename.
     * 
     * Categories are extracted from directory structure or filename prefixes.
     * 
     * @param fileName The filename
     * @return The category
     */
    private String extractCategory(String fileName) {
        // Remove .md extension
        String name = fileName.replaceAll("\\.md$", "");
        
        // Extract category from filename patterns like "category-document.md"
        if (name.contains("-")) {
            String[] parts = name.split("-", 2);
            return parts[0].toLowerCase();
        }
        
        return "general";
    }
    
    /**
     * Splits text into chunks of approximately specified size.
     * 
     * Attempts to split at sentence boundaries when possible.
     * 
     * @param text The text to split
     * @param maxChunkSize Maximum size of each chunk
     * @return List of text chunks
     */
    private List<String> splitIntoChunks(String text, int maxChunkSize) {
        List<String> chunks = new ArrayList<>();
        
        if (text.length() <= maxChunkSize) {
            chunks.add(text);
            return chunks;
        }
        
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxChunkSize, text.length());
            
            // Try to split at sentence boundary
            if (end < text.length()) {
                int lastPeriod = text.lastIndexOf('.', end);
                int lastNewline = text.lastIndexOf('\n', end);
                int splitPoint = Math.max(lastPeriod, lastNewline);
                
                if (splitPoint > start) {
                    end = splitPoint + 1;
                }
            }
            
            chunks.add(text.substring(start, end).trim());
            start = end;
        }
        
        return chunks;
    }
}

