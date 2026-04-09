package com.ai.company.tests;

import com.ai.company.tools.deployment.HealthCheckTool;
import com.ai.company.tools.testing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Direct Website Testing (No AI Agents Required)
 * 
 * Tests AgroConnectWorld website directly using testing tools.
 * This bypasses AI agents and tests the website directly.
 */
public class TestWebsiteDirect {
    
    private static final Logger log = LoggerFactory.getLogger(TestWebsiteDirect.class);
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("🌐 AGROCONNECTWORLD DIRECT WEBSITE TESTING - DEVELOPMENT");
        System.out.println("=".repeat(80));
        System.out.println();
        
        String baseUrl = "http://localhost:8080";
        
        System.out.println("📋 Testing Environment: DEVELOPMENT");
        System.out.println("📍 Base URL: " + baseUrl);
        System.out.println();
        
        // Initialize tools
        UITestTool uiTool = new UITestTool();
        APITestTool apiTool = new APITestTool();
        PerformanceTestTool perfTool = new PerformanceTestTool();
        SecurityTestTool secTool = new SecurityTestTool();
        HealthCheckTool healthTool = new HealthCheckTool();
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("COMPREHENSIVE TEST REPORT - DEVELOPMENT ENVIRONMENT\n");
        report.append("=".repeat(80)).append("\n\n");
        
        try {
            // 1. Health Checks
            System.out.println("🔍 Step 1: Health Checks...");
            report.append("--- HEALTH CHECKS ---\n");
            
            String gatewayHealth = healthTool.checkServiceHealth(baseUrl + "/actuator/health");
            report.append("Gateway Health:\n").append(gatewayHealth).append("\n\n");
            System.out.println("   ✓ Gateway health checked");
            
            String frontendHealth = healthTool.checkServiceHealth(baseUrl);
            report.append("Frontend Health:\n").append(frontendHealth).append("\n\n");
            System.out.println("   ✓ Frontend health checked");
            
            // 2. UI Testing
            System.out.println("\n🖥️  Step 2: UI Testing...");
            report.append("--- UI TESTING ---\n");
            
            String homePage = uiTool.testPageLoad(baseUrl);
            report.append("Home Page:\n").append(homePage).append("\n\n");
            System.out.println("   ✓ Home page tested");
            
            String productsPage = uiTool.testPageLoad(baseUrl + "/products");
            report.append("Products Page:\n").append(productsPage).append("\n\n");
            System.out.println("   ✓ Products page tested");
            
            String aboutPage = uiTool.testPageLoad(baseUrl + "/about");
            report.append("About Page:\n").append(aboutPage).append("\n\n");
            System.out.println("   ✓ About page tested");
            
            // Test user flow: Home -> Products -> Product Detail
            String userFlow = uiTool.testUserFlow(
                "Browse Products Flow",
                baseUrl + "," + baseUrl + "/products"
            );
            report.append("User Flow (Home -> Products):\n").append(userFlow).append("\n\n");
            System.out.println("   ✓ User flow tested");
            
            // 3. API Testing
            System.out.println("\n🔌 Step 3: API Testing...");
            report.append("--- API TESTING ---\n");
            
            // Test public endpoints
            String productsAPI = apiTool.testAPIEndpoint(
                baseUrl + "/api/products",
                "GET",
                null,
                null
            );
            report.append("Products API:\n").append(productsAPI).append("\n\n");
            System.out.println("   ✓ Products API tested");
            
            // Test authentication security
            String authSecurity = secTool.testAuthenticationSecurity(
                baseUrl + "/api/orders"
            );
            report.append("Authentication Security:\n").append(authSecurity).append("\n\n");
            System.out.println("   ✓ Authentication security tested");
            
            // 4. Performance Testing
            System.out.println("\n⚡ Step 4: Performance Testing...");
            report.append("--- PERFORMANCE TESTING ---\n");
            
            String homePerf = perfTool.measureResponseTime(baseUrl, 5);
            report.append("Home Page Performance:\n").append(homePerf).append("\n\n");
            System.out.println("   ✓ Home page performance tested");
            
            String apiPerf = perfTool.measureResponseTime(baseUrl + "/api/products", 5);
            report.append("Products API Performance:\n").append(apiPerf).append("\n\n");
            System.out.println("   ✓ Products API performance tested");
            
            // 5. Security Testing
            System.out.println("\n🔒 Step 5: Security Testing...");
            report.append("--- SECURITY TESTING ---\n");
            
            String corsTest = secTool.testCORS(baseUrl + "/api/products");
            report.append("CORS Configuration:\n").append(corsTest).append("\n\n");
            System.out.println("   ✓ CORS configuration tested");
            
            // 6. Container Health
            System.out.println("\n🐳 Step 6: Container Health...");
            report.append("--- CONTAINER HEALTH ---\n");
            
            try {
                String gatewayContainer = healthTool.checkContainerHealth("edge_service");
                report.append("Gateway Container:\n").append(gatewayContainer).append("\n\n");
                System.out.println("   ✓ Gateway container checked");
            } catch (Exception e) {
                report.append("Gateway Container: ERROR - ").append(e.getMessage()).append("\n\n");
                System.out.println("   ⚠ Gateway container check skipped (not running in Docker)");
            }
            
            try {
                String frontendContainer = healthTool.checkContainerHealth("frontend_service");
                report.append("Frontend Container:\n").append(frontendContainer).append("\n\n");
                System.out.println("   ✓ Frontend container checked");
            } catch (Exception e) {
                report.append("Frontend Container: ERROR - ").append(e.getMessage()).append("\n\n");
                System.out.println("   ⚠ Frontend container check skipped (not running in Docker)");
            }
            
            // Summary
            report.append("=".repeat(80)).append("\n");
            report.append("TEST SUMMARY\n");
            report.append("=".repeat(80)).append("\n");
            report.append("Environment: DEVELOPMENT\n");
            report.append("Base URL: ").append(baseUrl).append("\n");
            report.append("Test Date: ").append(java.time.Instant.now()).append("\n");
            report.append("\nAll tests completed. Review results above.\n");
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("📊 COMPREHENSIVE TEST REPORT");
            System.out.println("=".repeat(80));
            System.out.println();
            System.out.println(report.toString());
            System.out.println("=".repeat(80));
            System.out.println("✅ Testing Complete!");
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            log.error("Error running tests", e);
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}



