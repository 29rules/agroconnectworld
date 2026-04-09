package com.ai.company.tools.testing;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * UI Test Tool
 * 
 * Tests UI components, pages, and user flows.
 * Read-only operations for testing.
 */
public class UITestTool {
    
    private static final Logger log = LoggerFactory.getLogger(UITestTool.class);
    
    /**
     * Test if a page loads successfully.
     */
    @Tool("Test if a web page loads successfully. Returns HTTP status, load time, and page title if available. Read-only.")
    public String testPageLoad(String pageUrl) {
        log.info("Testing page load: {}", pageUrl);
        
        try {
            URL url = new URL(pageUrl);
            long startTime = System.currentTimeMillis();
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "AI-Company-UI-Test/1.0");
            
            int statusCode = connection.getResponseCode();
            long loadTime = System.currentTimeMillis() - startTime;
            
            StringBuilder result = new StringBuilder();
            result.append("=== Page Load Test ===\n");
            result.append("URL: ").append(pageUrl).append("\n");
            result.append("Status Code: ").append(statusCode).append("\n");
            result.append("Load Time: ").append(loadTime).append(" ms\n");
            
            if (statusCode >= 200 && statusCode < 300) {
                result.append("Result: PASS - Page loaded successfully\n");
            } else {
                result.append("Result: FAIL - Page failed to load\n");
            }
            
            // Try to extract title from HTML
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null && result.length() < 5000) {
                    if (line.toLowerCase().contains("<title>")) {
                        String title = line.replaceAll("(?i).*<title>(.*?)</title>.*", "$1").trim();
                        result.append("Page Title: ").append(title).append("\n");
                        break;
                    }
                }
            }
            
            return result.toString();
            
        } catch (IOException e) {
            log.error("Error testing page load: {}", pageUrl, e);
            return "ERROR: Page load test failed - " + e.getMessage();
        }
    }
    
    /**
     * Test multiple pages.
     */
    @Tool("Test multiple pages. Provide comma-separated URLs. Returns results for each page. Read-only.")
    public String testMultiplePages(String pageUrls) {
        if (pageUrls == null || pageUrls.isEmpty()) {
            return "ERROR: Page URLs required";
        }
        
        String[] urls = pageUrls.split(",");
        List<String> results = new ArrayList<>();
        
        for (String url : urls) {
            url = url.trim();
            if (!url.isEmpty()) {
                results.add(testPageLoad(url));
            }
        }
        
        StringBuilder combined = new StringBuilder();
        combined.append("=== Multiple Page Load Tests ===\n");
        combined.append("Total Pages: ").append(results.size()).append("\n\n");
        
        int passCount = 0;
        for (String result : results) {
            if (result.contains("Result: PASS")) {
                passCount++;
            }
            combined.append(result).append("\n---\n\n");
        }
        
        combined.append("Summary: ").append(passCount).append("/").append(results.size())
                .append(" pages loaded successfully\n");
        
        return combined.toString();
    }
    
    /**
     * Test user flow (sequence of pages).
     */
    @Tool("Test a user flow by visiting pages in sequence. Provide comma-separated URLs in order. Returns flow test results. Read-only.")
    public String testUserFlow(String flowName, String pageUrls) {
        log.info("Testing user flow: {} with pages: {}", flowName, pageUrls);
        
        StringBuilder result = new StringBuilder();
        result.append("=== User Flow Test ===\n");
        result.append("Flow Name: ").append(flowName).append("\n");
        result.append("Pages: ").append(pageUrls).append("\n\n");
        
        String[] urls = pageUrls.split(",");
        int step = 1;
        int passCount = 0;
        
        for (String url : urls) {
            url = url.trim();
            if (url.isEmpty()) continue;
            
            result.append("Step ").append(step).append(": ").append(url).append("\n");
            String pageResult = testPageLoad(url);
            
            if (pageResult.contains("Result: PASS")) {
                passCount++;
                result.append("  Status: PASS\n");
            } else {
                result.append("  Status: FAIL\n");
            }
            result.append("\n");
            step++;
        }
        
        result.append("Flow Summary: ").append(passCount).append("/").append(step - 1)
                .append(" steps passed\n");
        
        if (passCount == step - 1) {
            result.append("Overall: PASS - User flow completed successfully\n");
        } else {
            result.append("Overall: FAIL - User flow has failures\n");
        }
        
        return result.toString();
    }
}



