package com.ai.company.api;

import com.ai.company.promotion.BuildPromotionResult;
import com.ai.company.promotion.BuildPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for Build Promotion Flow
 * CEO can approve builds for promotion through environments
 */
@RestController
@RequestMapping("/ai/promotion")
public class PromotionController {
    
    @Autowired
    private BuildPromotionService promotionService;
    
    /**
     * Promote build from one environment to another
     * Flow: dev → uat → staging → production
     */
    @PostMapping("/promote")
    public ResponseEntity<BuildPromotionResult> promoteBuild(@RequestBody Map<String, String> request) {
        String fromEnv = request.get("fromEnvironment");
        String toEnv = request.get("toEnvironment");
        String buildId = request.get("buildId");
        String branch = request.get("branch");
        String commit = request.get("commit");
        
        BuildPromotionResult result = promotionService.promoteBuild(
            fromEnv, toEnv, buildId, branch, commit
        );
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get promotion checklist for environment
     */
    @GetMapping("/checklist/{environment}")
    public ResponseEntity<Map<String, String>> getChecklist(@PathVariable String environment) {
        String checklist = promotionService.getPromotionChecklist(environment);
        return ResponseEntity.ok(Map.of("checklist", checklist));
    }
    
    /**
     * Approve build for promotion (CEO approval)
     */
    @PostMapping("/approve")
    public ResponseEntity<BuildPromotionResult> approvePromotion(@RequestBody Map<String, String> request) {
        String fromEnv = request.get("fromEnvironment");
        String toEnv = request.get("toEnvironment");
        String buildId = request.get("buildId");
        String branch = request.get("branch");
        String commit = request.get("commit");
        String approvedBy = request.get("approvedBy"); // CEO email
        
        // CEO approval triggers promotion
        BuildPromotionResult result = promotionService.promoteBuild(
            fromEnv, toEnv, buildId, branch, commit
        );
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get promotion status
     */
    @GetMapping("/status/{buildId}")
    public ResponseEntity<BuildPromotionResult> getPromotionStatus(@PathVariable String buildId) {
        // TODO: Load from saved results
        return ResponseEntity.notFound().build();
    }
}



