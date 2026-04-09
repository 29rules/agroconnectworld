# Supervisor Deployment Rules

## Overview

The Supervisor Agent is the ultimate authority for deployment approvals. It enforces strict rules to ensure all deployments are safe, tested, and properly configured before execution.

## Deployment Blocking Rules

The Supervisor Agent **MUST BLOCK** any deployment attempt that does not meet ALL of the following prerequisites:

### 1. CTO Approval
- **Requirement:** Explicit CTO approval must be present
- **Validation:** CTO approval status must be verified
- **Details:** CTO approval details should be provided
- **Blocking:** Deployment is blocked if CTO approval is missing

### 2. QA Test Pass
- **Requirement:** All QA tests must pass
- **Validation:** Test results must show all tests passing
- **Coverage:** Test coverage must meet minimum threshold
- **Blocking:** Deployment is blocked if tests fail or coverage is insufficient

### 3. DevOps Risk Analysis
- **Requirement:** DevOps risk analysis must be completed
- **Validation:** Risk analysis report must be present
- **Risk Level:** Risk level must be within acceptable threshold
- **Blocking:** Deployment is blocked if risk analysis is missing or risk is too high

### 4. Staging Validation Pass
- **Requirement:** Staging validation must pass
- **Validation:** Staging simulation results must show success
- **Health Checks:** All staging health checks must pass
- **Blocking:** Deployment is blocked if staging validation fails

### 5. Code Health Score >= Minimum Threshold
- **Requirement:** Code health score must meet minimum threshold
- **Default Threshold:** 70 (configurable)
- **Validation:** Code health score must be >= threshold
- **Blocking:** Deployment is blocked if code health is below threshold

### 6. Proper Rollback Configuration
- **Requirement:** Rollback configuration must be present
- **Validation:** Rollback plan must be documented
- **Snapshot:** Rollback snapshot must be created
- **Blocking:** Deployment is blocked if rollback is not configured

---

## Deployment Approval Methods

### approveStagingDeployment()

Approves or rejects staging deployments.

**Required Prerequisites:**
- CTO approval
- QA test pass
- Staging validation pass
- Code health score >= threshold
- Rollback configuration

**Output:**
- `APPROVED` - If all prerequisites are met
- `REJECTED` - If any prerequisite is missing, with detailed reasons

**Usage:**
```java
SupervisorAgent supervisor = new SupervisorAgent(chatModel);
SupervisorAgent.DeploymentApproval approval = supervisor.approveStagingDeployment(
    stagingContext, "session-123");

if (approval.isApproved()) {
    // Proceed with staging deployment
} else {
    // Fix missing prerequisites
    System.out.println("Rejection reasons: " + approval.getRejectionReasons());
}
```

---

### approveProductionDeployment()

Approves or rejects production deployments.

**Required Prerequisites (ALL must be met):**
- CTO approval
- QA test pass
- DevOps risk analysis
- Staging validation pass
- Code health score >= threshold
- Proper rollback configuration

**Output:**
- `APPROVED` - If ALL prerequisites are met
- `REJECTED` - If ANY prerequisite is missing, with detailed reasons

**Usage:**
```java
SupervisorAgent supervisor = new SupervisorAgent(chatModel);
SupervisorAgent.DeploymentApproval approval = supervisor.approveProductionDeployment(
    productionContext, "session-123");

if (approval.isApproved()) {
    // Proceed with production deployment
} else {
    // Fix missing prerequisites
    System.out.println("Rejection reasons: " + approval.getRejectionReasons());
}
```

---

## Deployment Prerequisites Validation

### validateDeploymentPrerequisites()

Validates all deployment prerequisites before allowing deployment.

**Checks:**
1. CTO approval status
2. QA test pass status
3. DevOps risk analysis status
4. Staging validation pass status
5. Code health score vs threshold
6. Rollback configuration status

**Output:**
- `approved: true` - If all prerequisites are met
- `approved: false` - If any prerequisite is missing
- `missingPrerequisites` - List of missing prerequisites

**Usage:**
```java
SupervisorAgent.DeploymentPrerequisites prerequisites = new SupervisorAgent.DeploymentPrerequisites();
prerequisites.setCtoApproved(true);
prerequisites.setQaTestPass(true);
prerequisites.setDevOpsRiskAnalysis(true);
prerequisites.setStagingValidationPass(true);
prerequisites.setCodeHealthScore(85.0);
prerequisites.setMinimumHealthThreshold(70.0);
prerequisites.setRollbackConfigured(true);

SupervisorAgent supervisor = new SupervisorAgent(chatModel);
SupervisorAgent.DeploymentValidation validation = supervisor.validateDeploymentPrerequisites(
    prerequisites, "session-123");

if (validation.isApproved()) {
    // All prerequisites met
} else {
    // Missing prerequisites
    System.out.println("Missing: " + validation.getMissingPrerequisites());
}
```

---

## Unsafe Change Rejection

### rejectUnsafeChanges()

Rejects changes that pose safety risks.

**Safety Concerns Checked:**
- Security risks
- Data integrity risks
- Performance risks
- Stability risks

**Output:**
- `rejected: true` - If changes are unsafe
- `rejected: false` - If changes are safe
- `safetyConcerns` - List of identified safety concerns

**Usage:**
```java
SupervisorAgent supervisor = new SupervisorAgent(chatModel);
SupervisorAgent.UnsafeChangeRejection rejection = supervisor.rejectUnsafeChanges(
    changeDescription, riskAssessment, "session-123");

if (rejection.isRejected()) {
    System.out.println("Safety concerns: " + rejection.getSafetyConcerns());
}
```

---

## Test Coverage Rejection

### rejectInsufficientTestCoverage()

Rejects deployments with insufficient test coverage.

**Coverage Areas Checked:**
- Unit test coverage
- Integration test coverage
- E2E test coverage

**Output:**
- `rejected: true` - If coverage is below threshold
- `rejected: false` - If coverage meets threshold
- `coverageIssues` - List of coverage issues

**Usage:**
```java
SupervisorAgent supervisor = new SupervisorAgent(chatModel);
SupervisorAgent.TestCoverageRejection rejection = supervisor.rejectInsufficientTestCoverage(
    testCoverageReport, 80.0, "session-123");

if (rejection.isRejected()) {
    System.out.println("Coverage issues: " + rejection.getCoverageIssues());
}
```

---

## Deployment Workflow Integration

### Staging Deployment Workflow

```
1. Run staging simulation
   ↓
2. Validate staging results
   ↓
3. Check prerequisites:
   - CTO approval ✓
   - QA test pass ✓
   - Staging validation pass ✓
   - Code health score >= threshold ✓
   - Rollback configured ✓
   ↓
4. SupervisorAgent.approveStagingDeployment()
   ↓
5. If approved → Deploy to staging
   If rejected → Fix issues and retry
```

### Production Deployment Workflow

```
1. Run staging simulation
   ↓
2. Validate staging results
   ↓
3. Get CTO approval
   ↓
4. Get DevOps risk analysis
   ↓
5. Check prerequisites:
   - CTO approval ✓
   - QA test pass ✓
   - DevOps risk analysis ✓
   - Staging validation pass ✓
   - Code health score >= threshold ✓
   - Rollback configured ✓
   ↓
6. SupervisorAgent.validateDeploymentPrerequisites()
   ↓
7. SupervisorAgent.approveProductionDeployment()
   ↓
8. If approved → Deploy to production
   If rejected → Fix issues and retry
```

---

## Prerequisites Checklist

Before requesting deployment approval, ensure:

- [ ] CTO approval obtained
- [ ] QA tests passed
- [ ] DevOps risk analysis completed
- [ ] Staging validation passed
- [ ] Code health score >= threshold
- [ ] Rollback configuration present
- [ ] Test coverage meets minimum
- [ ] No unsafe changes detected

---

## Default Thresholds

- **Code Health Score Minimum:** 70.0
- **Test Coverage Minimum:** 80.0%
- **Risk Level Maximum:** 30% (0.3)
- **Health Check Pass Rate:** 80%

---

## Enforcement Rules

1. **Zero Tolerance:** Missing ANY prerequisite blocks deployment
2. **No Exceptions:** No bypassing of prerequisites
3. **Strict Validation:** All prerequisites must be verified
4. **Clear Rejection:** Rejections include detailed reasons
5. **Safety First:** Safety concerns always block deployment

---

## Best Practices

1. **Prepare Early:** Gather all prerequisites before requesting approval
2. **Document Everything:** Provide detailed context for all prerequisites
3. **Fix Issues First:** Address all missing prerequisites before retry
4. **Monitor Health:** Keep code health score above threshold
5. **Maintain Coverage:** Keep test coverage above minimum
6. **Configure Rollback:** Always configure rollback before deployment
7. **Get Approvals:** Obtain CTO and DevOps approvals early
8. **Validate Staging:** Always validate in staging first

---

## Error Handling

### Missing Prerequisites
- Deployment is blocked
- Missing prerequisites are listed
- Fix issues and retry

### Unsafe Changes
- Changes are rejected
- Safety concerns are listed
- Address concerns before retry

### Insufficient Coverage
- Deployment is rejected
- Coverage issues are listed
- Improve coverage before retry

---

## Integration Points

The Supervisor Agent integrates with:

1. **CTOAgent** - For CTO approval
2. **QAAgent** - For test results
3. **DevOpsAgent** - For risk analysis
4. **StagingSimulator** - For staging validation
5. **CodeHealthMonitor** - For code health scores
6. **RollbackManager** - For rollback configuration

---

## Zero-Impact Compliance

All deployment approvals:
- ✅ Respect Impact Mode settings
- ✅ Require explicit approvals
- ✅ Validate all prerequisites
- ✅ Never bypass safety checks
- ✅ Provide full audit trails
- ✅ Block unsafe deployments

The Supervisor Agent is the final gatekeeper ensuring all deployments are safe, tested, and properly configured.



