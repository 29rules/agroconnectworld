package com.ai.company.promotion;

import java.util.ArrayList;
import java.util.List;

public class TestResults {
    private String buildId;
    private int unitTestsPassed;
    private int unitTestsTotal;
    private int integrationTestsPassed;
    private int integrationTestsTotal;
    private int e2ETestsPassed;
    private int e2ETestsTotal;
    private boolean allPassed;
    private List<String> errors = new ArrayList<>();
    
    public void addError(String error) {
        this.errors.add(error);
    }
    
    public int getFailedCount() {
        return (unitTestsTotal - unitTestsPassed) + 
               (integrationTestsTotal - integrationTestsPassed) + 
               (e2ETestsTotal - e2ETestsPassed);
    }
    
    // Getters and Setters
    public String getBuildId() { return buildId; }
    public void setBuildId(String buildId) { this.buildId = buildId; }
    
    public int getUnitTestsPassed() { return unitTestsPassed; }
    public void setUnitTestsPassed(int unitTestsPassed) { this.unitTestsPassed = unitTestsPassed; }
    
    public int getUnitTestsTotal() { return unitTestsTotal; }
    public void setUnitTestsTotal(int unitTestsTotal) { this.unitTestsTotal = unitTestsTotal; }
    
    public int getIntegrationTestsPassed() { return integrationTestsPassed; }
    public void setIntegrationTestsPassed(int integrationTestsPassed) { this.integrationTestsPassed = integrationTestsPassed; }
    
    public int getIntegrationTestsTotal() { return integrationTestsTotal; }
    public void setIntegrationTestsTotal(int integrationTestsTotal) { this.integrationTestsTotal = integrationTestsTotal; }
    
    public int getE2ETestsPassed() { return e2ETestsPassed; }
    public void setE2ETestsPassed(int e2ETestsPassed) { this.e2ETestsPassed = e2ETestsPassed; }
    
    public int getE2ETestsTotal() { return e2ETestsTotal; }
    public void setE2ETestsTotal(int e2ETestsTotal) { this.e2ETestsTotal = e2ETestsTotal; }
    
    public boolean isAllPassed() { return allPassed; }
    public void setAllPassed(boolean allPassed) { this.allPassed = allPassed; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}



