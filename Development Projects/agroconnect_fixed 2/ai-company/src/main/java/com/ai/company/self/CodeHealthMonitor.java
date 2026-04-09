package com.ai.company.self;

import com.ai.company.tools.code.CodeReaderTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Code Health Monitor
 * 
 * Evaluates codebase health by measuring:
 * - Maintainability score (0-100)
 * - Code complexity (cyclomatic complexity)
 * - Unused code detection
 * - Dead code detection
 * - Code duplication percentage
 * - Documentation coverage
 * 
 * Provides a comprehensive health score and actionable metrics.
 * 
 * ZERO-IMPACT MODE: This tool only analyzes, never modifies code.
 */
public class CodeHealthMonitor {
    
    private static final Logger log = LoggerFactory.getLogger(CodeHealthMonitor.class);
    
    private static final Path WORKSPACE_ROOT = Paths.get(".").toAbsolutePath().normalize();
    private static final Path BACKEND_DIR = WORKSPACE_ROOT.resolve("backend").normalize();
    private static final Path FRONTEND_DIR = WORKSPACE_ROOT.resolve("frontend").normalize();
    
    private final CodeReaderTool codeReader;
    
    public CodeHealthMonitor() {
        this.codeReader = new CodeReaderTool();
    }
    
    /**
     * Evaluates overall code health and returns a score (0-100).
     * 
     * @param directoryPath Directory to evaluate (e.g., "backend", "frontend")
     * @return Health report with score and metrics
     */
    public HealthReport evaluateHealth(String directoryPath) {
        log.info("Evaluating code health for: {}", directoryPath);
        
        try {
            Path targetDir = WORKSPACE_ROOT.resolve(directoryPath).normalize();
            
            if (!Files.exists(targetDir) || !Files.isDirectory(targetDir)) {
                return new HealthReport(0, "ERROR: Directory does not exist: " + directoryPath, 
                    new HealthMetrics());
            }
            
            HealthMetrics metrics = collectMetrics(targetDir);
            int score = calculateHealthScore(metrics);
            
            String report = generateReport(score, metrics, directoryPath);
            
            log.info("Code health evaluation completed. Score: {}/100", score);
            return new HealthReport(score, report, metrics);
            
        } catch (Exception e) {
            log.error("Error evaluating code health", e);
            return new HealthReport(0, "ERROR: " + e.getMessage(), new HealthMetrics());
        }
    }
    
    /**
     * Collects health metrics from the directory.
     */
    private HealthMetrics collectMetrics(Path directory) throws IOException {
        HealthMetrics metrics = new HealthMetrics();
        
        List<Path> javaFiles = new ArrayList<>();
        List<Path> jsFiles = new ArrayList<>();
        
        // Collect all code files
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                .forEach(path -> {
                    String fileName = path.getFileName().toString().toLowerCase();
                    if (fileName.endsWith(".java")) {
                        javaFiles.add(path);
                    } else if (fileName.endsWith(".js") || fileName.endsWith(".jsx") || 
                              fileName.endsWith(".ts") || fileName.endsWith(".tsx")) {
                        jsFiles.add(path);
                    }
                });
        }
        
        metrics.totalFiles = javaFiles.size() + jsFiles.size();
        metrics.javaFiles = javaFiles.size();
        metrics.jsFiles = jsFiles.size();
        
        // Analyze complexity
        int totalComplexity = 0;
        int totalLines = 0;
        int documentedMethods = 0;
        int totalMethods = 0;
        
        for (Path javaFile : javaFiles) {
            try {
                String content = Files.readString(javaFile);
                totalLines += content.split("\n").length;
                
                // Count methods
                Pattern methodPattern = Pattern.compile("(public|private|protected)\\s+\\w+\\s+\\w+\\s*\\(");
                long methodCount = methodPattern.matcher(content).results().count();
                totalMethods += methodCount;
                
                // Count documented methods (have JavaDoc)
                Pattern docPattern = Pattern.compile("/\\*\\*.*?\\*/", Pattern.DOTALL);
                long docCount = docPattern.matcher(content).results().count();
                documentedMethods += docCount;
                
                // Simple complexity: count control flow statements
                int complexity = countComplexity(content);
                totalComplexity += complexity;
                
            } catch (IOException e) {
                log.warn("Error analyzing file: {}", javaFile, e);
            }
        }
        
        metrics.averageComplexity = metrics.totalFiles > 0 ? (double) totalComplexity / metrics.totalFiles : 0;
        metrics.totalLinesOfCode = totalLines;
        metrics.documentationCoverage = totalMethods > 0 ? (double) documentedMethods / totalMethods * 100 : 0;
        
        // Detect duplication (simplified: exact duplicate lines)
        metrics.duplicationPercentage = detectDuplication(javaFiles);
        
        // Detect unused code (simplified: check for unused imports)
        metrics.unusedCodePercentage = detectUnusedCode(javaFiles);
        
        return metrics;
    }
    
    /**
     * Counts cyclomatic complexity indicators.
     */
    private int countComplexity(String content) {
        int complexity = 1; // Base complexity
        
        // Count control flow statements
        String[] complexityKeywords = {"if", "else", "for", "while", "switch", "case", "catch", "&&", "||", "?"};
        for (String keyword : complexityKeywords) {
            complexity += countOccurrences(content, keyword);
        }
        
        return complexity;
    }
    
    /**
     * Counts occurrences of a pattern in text.
     */
    private int countOccurrences(String text, String pattern) {
        return text.split(Pattern.quote(pattern), -1).length - 1;
    }
    
    /**
     * Detects code duplication percentage (simplified).
     */
    private double detectDuplication(List<Path> files) {
        if (files.size() < 2) {
            return 0.0;
        }
        
        // Simplified: check for identical method signatures
        Set<String> methodSignatures = new HashSet<>();
        AtomicInteger duplicates = new AtomicInteger(0);
        
        for (Path file : files) {
            try {
                String content = Files.readString(file);
                Pattern methodPattern = Pattern.compile("(public|private|protected)\\s+\\w+\\s+(\\w+)\\s*\\([^)]*\\)");
                methodPattern.matcher(content).results()
                    .forEach(match -> {
                        String signature = match.group(2);
                        if (!methodSignatures.add(signature)) {
                            duplicates.incrementAndGet();
                        }
                    });
            } catch (IOException e) {
                log.warn("Error checking duplication in: {}", file, e);
            }
        }
        
        int totalMethods = methodSignatures.size() + duplicates.get();
        return totalMethods > 0 ? (double) duplicates.get() / totalMethods * 100 : 0.0;
    }
    
    /**
     * Detects unused code percentage (simplified: unused imports).
     */
    private double detectUnusedCode(List<Path> files) {
        if (files.isEmpty()) {
            return 0.0;
        }
        
        int totalImports = 0;
        int unusedImports = 0;
        
        for (Path file : files) {
            try {
                String content = Files.readString(file);
                Pattern importPattern = Pattern.compile("^import\\s+([^;]+);", Pattern.MULTILINE);
                List<String> imports = importPattern.matcher(content).results()
                    .map(m -> m.group(1))
                    .collect(Collectors.toList());
                
                totalImports += imports.size();
                
                // Check if imports are used (simplified check)
                for (String imp : imports) {
                    String className = imp.substring(imp.lastIndexOf('.') + 1);
                    if (!content.contains(className) || content.indexOf(className) < content.indexOf("import")) {
                        unusedImports++;
                    }
                }
            } catch (IOException e) {
                log.warn("Error checking unused code in: {}", file, e);
            }
        }
        
        return totalImports > 0 ? (double) unusedImports / totalImports * 100 : 0.0;
    }
    
    /**
     * Calculates overall health score (0-100).
     */
    private int calculateHealthScore(HealthMetrics metrics) {
        double score = 100.0;
        
        // Deduct points for issues
        if (metrics.averageComplexity > 10) {
            score -= 20; // High complexity
        } else if (metrics.averageComplexity > 5) {
            score -= 10; // Medium complexity
        }
        
        if (metrics.duplicationPercentage > 20) {
            score -= 15; // High duplication
        } else if (metrics.duplicationPercentage > 10) {
            score -= 8; // Medium duplication
        }
        
        if (metrics.unusedCodePercentage > 15) {
            score -= 10; // High unused code
        } else if (metrics.unusedCodePercentage > 5) {
            score -= 5; // Medium unused code
        }
        
        if (metrics.documentationCoverage < 50) {
            score -= 15; // Low documentation
        } else if (metrics.documentationCoverage < 70) {
            score -= 8; // Medium documentation
        }
        
        // Ensure score is between 0 and 100
        return Math.max(0, Math.min(100, (int) score));
    }
    
    /**
     * Generates a human-readable health report.
     */
    private String generateReport(int score, HealthMetrics metrics, String directory) {
        StringBuilder report = new StringBuilder();
        report.append("=== CODE HEALTH REPORT ===\n");
        report.append("Directory: ").append(directory).append("\n");
        report.append("Overall Health Score: ").append(score).append("/100\n\n");
        
        report.append("--- Metrics ---\n");
        report.append("Total Files: ").append(metrics.totalFiles).append("\n");
        report.append("  - Java Files: ").append(metrics.javaFiles).append("\n");
        report.append("  - JS/TS Files: ").append(metrics.jsFiles).append("\n");
        report.append("Total Lines of Code: ").append(metrics.totalLinesOfCode).append("\n");
        report.append("Average Complexity: ").append(String.format("%.2f", metrics.averageComplexity)).append("\n");
        report.append("Code Duplication: ").append(String.format("%.1f", metrics.duplicationPercentage)).append("%\n");
        report.append("Unused Code: ").append(String.format("%.1f", metrics.unusedCodePercentage)).append("%\n");
        report.append("Documentation Coverage: ").append(String.format("%.1f", metrics.documentationCoverage)).append("%\n\n");
        
        report.append("--- Health Assessment ---\n");
        if (score >= 80) {
            report.append("Status: EXCELLENT - Codebase is in great shape\n");
        } else if (score >= 60) {
            report.append("Status: GOOD - Minor improvements recommended\n");
        } else if (score >= 40) {
            report.append("Status: FAIR - Significant improvements needed\n");
        } else {
            report.append("Status: POOR - Major refactoring required\n");
        }
        
        return report.toString();
    }
    
    /**
     * Health metrics data class.
     */
    public static class HealthMetrics {
        public int totalFiles = 0;
        public int javaFiles = 0;
        public int jsFiles = 0;
        public int totalLinesOfCode = 0;
        public double averageComplexity = 0.0;
        public double duplicationPercentage = 0.0;
        public double unusedCodePercentage = 0.0;
        public double documentationCoverage = 0.0;
    }
    
    /**
     * Health report result.
     */
    public static class HealthReport {
        private final int score;
        private final String report;
        private final HealthMetrics metrics;
        
        public HealthReport(int score, String report, HealthMetrics metrics) {
            this.score = score;
            this.report = report;
            this.metrics = metrics;
        }
        
        public int getScore() { return score; }
        public String getReport() { return report; }
        public HealthMetrics getMetrics() { return metrics; }
    }
}

