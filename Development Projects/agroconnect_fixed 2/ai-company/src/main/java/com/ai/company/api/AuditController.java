package com.ai.company.api;

import com.ai.company.audit.ProjectAuditReport;
import com.ai.company.audit.ProjectAuditService;
import com.ai.company.audit.SystemAuditResult;
import com.ai.company.audit.SystemAuditService;
import com.ai.company.devops.DevOpsValidationReport;
import com.ai.company.devops.DevOpsValidationService;
import com.ai.company.engineering.EngineeringPipelineService;
import com.ai.company.engineering.UserStory;
import com.ai.company.testing.TestGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Audit and Validation endpoints
 * Accessible via CEO Portal only (secured by SecurityFilter)
 */
@RestController
@RequestMapping("/ai/audit")
public class AuditController {
    
    @Autowired
    private ProjectAuditService auditService;
    
    @Autowired
    private DevOpsValidationService devOpsValidationService;
    
    @Autowired
    private EngineeringPipelineService engineeringService;
    
    @Autowired
    private TestGenerationService testGenerationService;
    
    @Autowired
    private SystemAuditService systemAuditService;
    
    /**
     * Trigger full system audit (CTO-initiated)
     * Produces 5-7 comprehensive reports
     */
    @PostMapping("/system-audit")
    public ResponseEntity<SystemAuditResult> triggerSystemAudit() {
        SystemAuditResult result = systemAuditService.initiateFullSystemAudit();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get latest project audit report
     */
    @GetMapping("/latest")
    public ResponseEntity<ProjectAuditReport> getLatestAudit() {
        ProjectAuditReport report = auditService.getLatestAudit();
        if (report != null) {
            return ResponseEntity.ok(report);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Trigger manual audit
     */
    @PostMapping("/run")
    public ResponseEntity<ProjectAuditReport> runAudit() {
        ProjectAuditReport report = auditService.conductFullAudit();
        return ResponseEntity.ok(report);
    }
    
    /**
     * Get latest DevOps validation report
     */
    @GetMapping("/devops/latest")
    public ResponseEntity<DevOpsValidationReport> getLatestDevOpsValidation() {
        DevOpsValidationReport report = devOpsValidationService.conductFullValidation();
        return ResponseEntity.ok(report);
    }
    
    /**
     * Trigger DevOps validation
     */
    @PostMapping("/devops/validate")
    public ResponseEntity<DevOpsValidationReport> validateDevOps() {
        DevOpsValidationReport report = devOpsValidationService.conductFullValidation();
        return ResponseEntity.ok(report);
    }
    
    /**
     * Generate user stories
     */
    @PostMapping("/engineering/stories")
    public ResponseEntity<List<UserStory>> generateStories(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        List<UserStory> stories = engineeringService.generateUserStories(requirement);
        return ResponseEntity.ok(stories);
    }
    
    /**
     * Generate test cases
     */
    @PostMapping("/testing/generate")
    public ResponseEntity<Map<String, Object>> generateTests() {
        testGenerationService.generateAPITestCases();
        testGenerationService.generatePostmanCollection();
        testGenerationService.generateSeleniumUITests();
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Test cases generated successfully"
        ));
    }
}

