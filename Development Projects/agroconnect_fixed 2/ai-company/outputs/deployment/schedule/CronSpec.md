# Deployment Scheduler Cron Specification

## Overview

The Deployment Scheduler automates deployment-related tasks on a regular schedule. All tasks run in safe mode (ZERO_IMPACT by default) and only perform simulations, builds, tests, and health checks - never actual deployments without explicit approval.

## Scheduled Tasks

### 1. Daily Build & Test
**Schedule:** Every day at 2:00 AM  
**Cron Expression:** `0 2 * * *` (2:00 AM daily)

**Task:** Nightly Deployment Agent
- Builds all services (auth-service, product-service, gateway, frontend)
- Runs backend tests
- Runs frontend tests
- Performs health checks on all services
- Generates AI analysis of results
- Reports issues to CTOAgent and SupervisorAgent

**Output:**
- Build results for all services
- Test results (backend and frontend)
- Health check results
- AI analysis report
- CTO report (if issues found)
- Supervisor report (if issues found)

**Safety:**
- Only builds and tests, never deploys
- All operations logged
- Issues reported to CTO and Supervisor

---

### 2. Weekly Staging Deploy Simulation
**Schedule:** Every Sunday  
**Cron Expression:** `0 0 * * 0` (Midnight on Sunday)

**Task:** Staging Simulation
- Loads docker-compose.staging.yml
- Validates compose file structure
- Runs migrations in dry-run mode
- Spins up containers (if Impact Mode allows)
- Validates internal API routes
- Validates frontend → gateway → services flow
- Runs health checks

**Output:**
- Staging simulation results
- Route validation results
- Health check results
- Errors and warnings

**Safety:**
- Only simulates, never actually deploys to staging
- Containers only started if Impact Mode allows
- All results logged

---

### 3. Monthly Self-Improvement Deploy Proposal
**Schedule:** First day of every month  
**Cron Expression:** `0 0 1 * *` (Midnight on 1st of month)

**Task:** Self-Improvement Cycle
- SelfAnalysisAgent analyzes codebase
- CodeHealthMonitor evaluates health
- ArchitectureDriftDetector detects drift
- FeatureOpportunityFinder finds opportunities
- ArchitectAgent designs improvements
- CTOAgent reviews improvements
- EngineerAgent generates implementation specs
- QAAgent generates test updates

**Output:**
- Self-improvement cycle results
- Analysis reports
- Health scores
- Drift reports
- Feature opportunities
- Architecture improvements
- Implementation specs
- Test plans

**Safety:**
- Only generates proposals, never auto-deploys
- All improvements require approval
- Respects Impact Mode settings

---

## Schedule Summary

| Task | Frequency | Time | Agent/Component |
|------|-----------|------|-----------------|
| Daily Build & Test | Daily | 2:00 AM | NightlyDeploymentAgent |
| Weekly Staging Simulation | Weekly (Sunday) | Midnight | StagingSimulator |
| Monthly Self-Improvement | Monthly (1st) | Midnight | SelfImprovementEngine |

---

## Implementation Details

### Daily Build & Test

**Trigger:** 2:00 AM daily  
**Component:** `NightlyDeploymentAgent`  
**Actions:**
1. Build all services using `DockerBuildTool`
2. Run backend tests using `TestRunnerTool`
3. Run frontend tests using `TestRunnerTool`
4. Check health using `HealthCheckTool`
5. Generate AI analysis
6. Report issues to CTO and Supervisor

**Duration:** Approximately 15-30 minutes  
**Impact:** ZERO_IMPACT (read-only operations)

---

### Weekly Staging Simulation

**Trigger:** Sunday at midnight  
**Component:** `StagingSimulator`  
**Actions:**
1. Load docker-compose.staging.yml
2. Validate compose structure
3. Run migrations (dry-run)
4. Spin up containers (if allowed)
5. Validate API routes
6. Validate frontend flow
7. Run health checks

**Duration:** Approximately 10-20 minutes  
**Impact:** ZERO_IMPACT (simulation only) or CONTROLLED_IMPACT (if containers started)

---

### Monthly Self-Improvement

**Trigger:** 1st of month at midnight  
**Component:** `SelfImprovementEngine`  
**Actions:**
1. Self-analysis
2. Health monitoring
3. Drift detection
4. Feature opportunity finding
5. Architecture improvements
6. CTO review
7. Implementation specs
8. Test plans

**Duration:** Approximately 30-60 minutes  
**Impact:** ZERO_IMPACT (proposals only)

---

## Cron Expression Reference

### Daily at 2:00 AM
```
0 2 * * *
```
- Minute: 0
- Hour: 2
- Day of month: * (every day)
- Month: * (every month)
- Day of week: * (every day of week)

### Weekly on Sunday
```
0 0 * * 0
```
- Minute: 0
- Hour: 0 (midnight)
- Day of month: * (every day)
- Month: * (every month)
- Day of week: 0 (Sunday)

### Monthly on 1st
```
0 0 1 * *
```
- Minute: 0
- Hour: 0 (midnight)
- Day of month: 1
- Month: * (every month)
- Day of week: * (any day)

---

## Safety Guarantees

1. **No Auto-Deployment:** All scheduled tasks are read-only or simulation-only
2. **Impact Mode Respect:** Tasks respect Impact Mode settings
3. **Approval Required:** Any actual deployments require explicit approval
4. **Full Logging:** All operations are logged with timestamps
5. **Error Handling:** Failures are logged and reported
6. **No Production Changes:** Never modifies production without approval

---

## Configuration

### Starting the Scheduler

```java
ChatLanguageModel chatModel = // ... initialize
AgentRegistry registry = AgentRegistry.getInstance(chatModel);
DeploymentScheduler scheduler = new DeploymentScheduler(chatModel, registry);
scheduler.start();
```

### Stopping the Scheduler

```java
scheduler.stop();
```

### Checking Status

```java
boolean isRunning = scheduler.isRunning();
```

---

## Monitoring

### Daily Build & Test Monitoring
- Check build logs for failures
- Review test results
- Monitor health check status
- Review AI analysis reports
- Check CTO and Supervisor reports

### Weekly Staging Simulation Monitoring
- Review staging simulation results
- Check route validation
- Monitor health checks
- Review errors and warnings

### Monthly Self-Improvement Monitoring
- Review self-improvement cycle results
- Check health scores
- Review drift reports
- Evaluate feature opportunities
- Review architecture improvements

---

## Best Practices

1. **Review Reports:** Always review scheduled task reports
2. **Address Issues:** Fix issues identified in nightly reports
3. **Monitor Trends:** Track patterns in scheduled task results
4. **Adjust Schedule:** Modify schedule if needed for your timezone
5. **Set Alerts:** Configure alerts for critical issues
6. **Archive Reports:** Keep historical reports for trend analysis

---

## Timezone Considerations

The scheduler uses the system's default timezone. To change the timezone:

1. Set JVM timezone: `-Duser.timezone=America/New_York`
2. Or adjust schedule times in code to match your timezone

---

## Error Handling

### Task Failures
- Errors are logged with full stack traces
- Failed tasks don't stop the scheduler
- Next scheduled run will attempt again

### Scheduler Failures
- Scheduler shutdown is graceful
- Running tasks complete before shutdown
- Can be restarted after fixing issues

---

## Integration Points

The Deployment Scheduler integrates with:

1. **NightlyDeploymentAgent** - Daily builds and tests
2. **StagingSimulator** - Weekly staging simulation
3. **SelfImprovementEngine** - Monthly improvements
4. **CTOAgent** - Issue reporting
5. **SupervisorAgent** - Issue reporting
6. **DockerBuildTool** - Service builds
7. **TestRunnerTool** - Test execution
8. **HealthCheckTool** - Health monitoring

---

## Future Enhancements

- Configurable schedule times
- Multiple schedule profiles (dev, staging, production)
- Email/Slack notifications
- Dashboard for scheduled task results
- Historical trend analysis
- Custom schedule definitions
- Pause/resume functionality

---

## Zero-Impact Compliance

All scheduled tasks:
- ✅ Run in ZERO_IMPACT mode by default
- ✅ Only perform read-only or simulation operations
- ✅ Never deploy without explicit approval
- ✅ Provide full audit trails
- ✅ Respect Impact Mode settings
- ✅ Report all operations

The scheduler is designed to be safe, automated, and informative without risking production systems.



