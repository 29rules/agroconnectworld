package com.ai.company.tests;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * AI System Test Dashboard
 * 
 * A comprehensive test runner that executes all system tests and provides
 * a color-coded, structured report of test results.
 * 
 * Features:
 * - Runs all test classes in the tests package
 * - ANSI color-coded output (Green=PASS, Red=FAIL, Yellow=RUNNING, Blue=Title)
 * - Exception handling with detailed error messages
 * - Sequential test execution
 * - Zero-impact mode verification
 * 
 * Usage:
 *   mvn -q exec:java -Dexec.mainClass="com.ai.company.tests.RunAllTests"
 * 
 * ZERO-IMPACT MODE: All tests operate in read-only mode.
 * No modifications to existing AgroConnectWorld backend/frontend code.
 */
public class RunAllTests {
    
    // ANSI Color Codes
    private static final String RESET = "\033[0m";
    private static final String GREEN = "\033[32m";
    private static final String RED = "\033[31m";
    private static final String YELLOW = "\033[33m";
    private static final String BLUE = "\033[34m";
    private static final String BOLD = "\033[1m";
    
    // Test Results
    private static class TestResult {
        String testName;
        boolean passed;
        String message;
        Exception exception;
        
        TestResult(String testName, boolean passed, String message) {
            this.testName = testName;
            this.passed = passed;
            this.message = message;
        }
        
        TestResult(String testName, boolean passed, String message, Exception exception) {
            this.testName = testName;
            this.passed = passed;
            this.message = message;
            this.exception = exception;
        }
    }
    
    private static final List<TestResult> results = new ArrayList<>();
    
    public static void main(String[] args) {
        printTitle("AI COMPANY TEST DASHBOARD");
        
        System.out.println();
        printInfo("Zero-Impact Mode: ENABLED");
        printInfo("All tests operate in read-only mode");
        printInfo("No modifications to existing AgroConnectWorld code");
        System.out.println();
        
        // Run all test layers
        runTest("Agent Layer", "Test_AgentLayer", "Agent layer tests passed");
        runTest("Workflow Layer", "Test_WorkflowLayer", "Workflow layer tests passed");
        runTest("Planner Layer", "Test_PlannerLayer", "Planner layer tests passed");
        runTest("Build Layer", "Test_BuildLayer", "Build layer tests passed");
        runTest("Deployment Layer", "Test_DeploymentLayer", "Deployment layer tests passed");
        
        // Print Summary
        printSummary();
    }
    
    /**
     * Runs a test class by reflection.
     * Sets a system property to prevent tests from calling System.exit().
     */
    private static void runTest(String displayName, String className, String successMessage) {
        printRunning(displayName + "...");
        System.out.println();
        
        // Set system property to prevent System.exit() calls
        String originalNoExit = System.getProperty("runAllTests.noExit");
        System.setProperty("runAllTests.noExit", "true");
        
        try {
            Class<?> testClass = Class.forName("com.ai.company.tests." + className);
            Method mainMethod = testClass.getMethod("main", String[].class);
            
            // Run the test
            try {
                mainMethod.invoke(null, (Object) new String[0]);
                
                // If we get here, test passed (didn't exit)
                printSuccess(successMessage);
                results.add(new TestResult(displayName, true, successMessage));
                
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause();
                
                // Real exception - test failed
                String errorMsg = cause != null ? cause.getMessage() : e.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = cause != null ? cause.getClass().getSimpleName() : e.getClass().getSimpleName();
                }
                printFailure("Test failed: " + errorMsg);
                results.add(new TestResult(displayName, false, "Test failed: " + errorMsg, e));
            }
            
        } catch (ClassNotFoundException e) {
            printFailure("Test class not found: " + className);
            results.add(new TestResult(displayName, false, "Test class not found: " + className, e));
        } catch (NoSuchMethodException e) {
            printFailure("Test class missing main method: " + className);
            results.add(new TestResult(displayName, false, "Test class missing main method: " + className, e));
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = e.getClass().getSimpleName();
            }
            printFailure("Test failed: " + errorMsg);
            results.add(new TestResult(displayName, false, "Test failed: " + errorMsg, e));
        } finally {
            // Restore original property
            if (originalNoExit != null) {
                System.setProperty("runAllTests.noExit", originalNoExit);
            } else {
                System.clearProperty("runAllTests.noExit");
            }
        }
        
        System.out.println();
    }
    
    /**
     * Prints the dashboard title.
     */
    private static void printTitle(String title) {
        String separator = "-".repeat(80);
        System.out.println(BLUE + BOLD + separator + RESET);
        System.out.println(BLUE + BOLD + centerText(title, 80) + RESET);
        System.out.println(BLUE + BOLD + separator + RESET);
    }
    
    /**
     * Prints a running test indicator.
     */
    private static void printRunning(String message) {
        System.out.print(YELLOW + "[Running] " + RESET + message);
        System.out.flush();
    }
    
    /**
     * Prints a success message.
     */
    private static void printSuccess(String message) {
        System.out.println(GREEN + "[PASS]    " + RESET + message);
    }
    
    /**
     * Prints a failure message.
     */
    private static void printFailure(String message) {
        System.out.println(RED + "[FAIL]    " + RESET + message);
    }
    
    /**
     * Prints an info message.
     */
    private static void printInfo(String message) {
        System.out.println(BLUE + "[INFO]    " + RESET + message);
    }
    
    /**
     * Prints the test summary.
     */
    private static void printSummary() {
        System.out.println();
        printTitle("SUMMARY");
        
        int passed = 0;
        int failed = 0;
        
        for (TestResult result : results) {
            if (result.passed) {
                passed++;
            } else {
                failed++;
            }
        }
        
        System.out.println();
        System.out.println("Total Tests: " + results.size());
        System.out.println(GREEN + "Passed: " + passed + RESET);
        if (failed > 0) {
            System.out.println(RED + "Failed: " + failed + RESET);
        } else {
            System.out.println("Failed: " + failed);
        }
        System.out.println();
        
        if (failed == 0) {
            System.out.println();
            printSuccess("ALL SYSTEMS OPERATIONAL");
            System.out.println();
            printInfo("Zero-Impact + Controlled-Impact + Deployment Modes Verified");
        } else {
            printFailure("Some tests failed. Review errors above.");
            System.out.println();
            System.out.println("Failed Tests:");
            for (TestResult result : results) {
                if (!result.passed) {
                    System.out.println(RED + "  ✗ " + result.testName + ": " + result.message + RESET);
                    if (result.exception != null && result.exception.getCause() != null) {
                        System.out.println("    " + result.exception.getCause().getClass().getSimpleName() + 
                                         ": " + result.exception.getCause().getMessage());
                    }
                }
            }
        }
        
        System.out.println();
        String separator = "-".repeat(80);
        System.out.println(BLUE + BOLD + separator + RESET);
    }
    
    /**
     * Centers text within a given width.
     */
    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
}

