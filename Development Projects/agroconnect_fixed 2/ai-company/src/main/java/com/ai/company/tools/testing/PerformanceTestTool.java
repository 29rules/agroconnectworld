package com.ai.company.tools.testing;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Performance Test Tool
 * 
 * Tests performance metrics: response times, load times, throughput.
 * Read-only operations for testing.
 */
public class PerformanceTestTool {
    
    private static final Logger log = LoggerFactory.getLogger(PerformanceTestTool.class);
    
    /**
     * Measure response time for an endpoint.
     */
    @Tool("Measure response time for an endpoint. Returns average, min, max response times. Read-only.")
    public String measureResponseTime(String endpointUrl, int numberOfRequests) {
        log.info("Measuring response time for: {} with {} requests", endpointUrl, numberOfRequests);
        
        if (numberOfRequests <= 0) {
            numberOfRequests = 5; // Default
        }
        if (numberOfRequests > 20) {
            numberOfRequests = 20; // Cap at 20
        }
        
        List<Long> responseTimes = new ArrayList<>();
        
        try {
            URL url = new URL(endpointUrl);
            
            for (int i = 0; i < numberOfRequests; i++) {
                long startTime = System.currentTimeMillis();
                
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "AI-Company-Performance-Test/1.0");
                
                int statusCode = connection.getResponseCode();
                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;
                
                responseTimes.add(responseTime);
                
                connection.disconnect();
                
                // Small delay between requests
                Thread.sleep(100);
            }
            
            // Calculate statistics
            long sum = responseTimes.stream().mapToLong(Long::longValue).sum();
            double average = (double) sum / responseTimes.size();
            long min = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
            long max = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
            
            StringBuilder result = new StringBuilder();
            result.append("=== Performance Test ===\n");
            result.append("Endpoint: ").append(endpointUrl).append("\n");
            result.append("Number of Requests: ").append(numberOfRequests).append("\n");
            result.append("Average Response Time: ").append(String.format("%.2f", average)).append(" ms\n");
            result.append("Min Response Time: ").append(min).append(" ms\n");
            result.append("Max Response Time: ").append(max).append(" ms\n");
            
            // Performance assessment
            if (average < 200) {
                result.append("Performance: EXCELLENT (< 200ms)\n");
            } else if (average < 500) {
                result.append("Performance: GOOD (< 500ms)\n");
            } else if (average < 1000) {
                result.append("Performance: ACCEPTABLE (< 1s)\n");
            } else {
                result.append("Performance: POOR (> 1s) - Needs optimization\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("Error measuring response time: {}", endpointUrl, e);
            return "ERROR: Performance test failed - " + e.getMessage();
        }
    }
    
    /**
     * Test concurrent load.
     */
    @Tool("Test concurrent load by making multiple simultaneous requests. Returns load test results. Read-only.")
    public String testConcurrentLoad(String endpointUrl, int concurrentRequests) {
        log.info("Testing concurrent load: {} with {} concurrent requests", endpointUrl, concurrentRequests);
        
        if (concurrentRequests <= 0) {
            concurrentRequests = 5; // Default
        }
        if (concurrentRequests > 10) {
            concurrentRequests = 10; // Cap at 10 for safety
        }
        
        List<Thread> threads = new ArrayList<>();
        List<Long> responseTimes = new ArrayList<>();
        List<Boolean> success = new ArrayList<>();
        
        try {
            URL url = new URL(endpointUrl);
            
            for (int i = 0; i < concurrentRequests; i++) {
                final int requestNum = i;
                Thread thread = new Thread(() -> {
                    try {
                        long startTime = System.currentTimeMillis();
                        
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(10000);
                        connection.setReadTimeout(10000);
                        connection.setRequestProperty("User-Agent", "AI-Company-Load-Test/1.0");
                        
                        int statusCode = connection.getResponseCode();
                        long endTime = System.currentTimeMillis();
                        long responseTime = endTime - startTime;
                        
                        synchronized (responseTimes) {
                            responseTimes.add(responseTime);
                            success.add(statusCode >= 200 && statusCode < 300);
                        }
                        
                        connection.disconnect();
                    } catch (Exception e) {
                        synchronized (responseTimes) {
                            success.add(false);
                        }
                    }
                });
                threads.add(thread);
            }
            
            // Start all threads
            for (Thread thread : threads) {
                thread.start();
            }
            
            // Wait for all threads
            for (Thread thread : threads) {
                thread.join();
            }
            
            // Calculate statistics
            long sum = responseTimes.stream().mapToLong(Long::longValue).sum();
            double average = responseTimes.isEmpty() ? 0 : (double) sum / responseTimes.size();
            int successCount = (int) success.stream().filter(Boolean::booleanValue).count();
            
            StringBuilder result = new StringBuilder();
            result.append("=== Concurrent Load Test ===\n");
            result.append("Endpoint: ").append(endpointUrl).append("\n");
            result.append("Concurrent Requests: ").append(concurrentRequests).append("\n");
            result.append("Successful Requests: ").append(successCount).append("/").append(concurrentRequests).append("\n");
            result.append("Average Response Time: ").append(String.format("%.2f", average)).append(" ms\n");
            
            if (successCount == concurrentRequests) {
                result.append("Result: PASS - All requests succeeded\n");
            } else {
                result.append("Result: FAIL - Some requests failed\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("Error testing concurrent load: {}", endpointUrl, e);
            return "ERROR: Concurrent load test failed - " + e.getMessage();
        }
    }
}



