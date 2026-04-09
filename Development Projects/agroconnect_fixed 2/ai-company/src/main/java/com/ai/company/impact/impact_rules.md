# Impact Mode Rules and Operations

This document defines the rules and allowed/forbidden operations for each impact mode in the AI Company system.

## Impact Modes

### 1. ZERO_IMPACT (Default)

**Description:** No code modifications allowed. Read-only operations only.

**Allowed Operations:**
- ✅ Read files (`CodeReaderTool.readFile()`)
- ✅ Read folders (`CodeReaderTool.readFolder()`)
- ✅ List files (`CodeReaderTool.listFiles()`)
- ✅ Run tests (`TestRunnerTool.runBackendTests()`, `TestRunnerTool.runFrontendTests()`)
- ✅ Get git status (`GitCommitTool.getGitStatus()`)
- ✅ All agent operations that produce specifications/documentation only

**Forbidden Operations:**
- ❌ Write files (`CodeWriterTool.writeFile()`)
- ❌ Modify files (`CodeModifierTool.insertAtLine()`, `replaceLines()`, `deleteLines()`)
- ❌ Format files (`CodeFormatterTool.formatJavaFile()`, `formatJsFile()`)
- ❌ Stage changes (`GitCommitTool.stageChanges()`, `stageFiles()`)
- ❌ Commit changes (`GitCommitTool.commitChanges()`)
- ❌ Any operation that modifies existing code
- ❌ Any operation that creates new files
- ❌ Any operation that changes infrastructure

**Required Validations:**
- None (all modifications blocked)

**Mode Change:**
- Can only be changed by SupervisorAgent
- Default mode on system startup

---

### 2. CONTROLLED_IMPACT

**Description:** Limited code modifications allowed with strict validation requirements.

**Allowed Operations:**
- ✅ All ZERO_IMPACT operations
- ✅ Write new files (`CodeWriterTool.writeFile()`) - with approval
- ✅ Modify existing files (`CodeModifierTool.*`) - with approval
- ✅ Format files (`CodeFormatterTool.*`) - with approval
- ✅ Stage changes (`GitCommitTool.stageChanges()`, `stageFiles()`) - with approval
- ✅ Commit changes (`GitCommitTool.commitChanges()`) - with approval

**Forbidden Operations:**
- ❌ Modifications outside `backend/` or `frontend/` directories
- ❌ Infrastructure changes (Docker, Nginx, Postgres configs)
- ❌ Modifications without patch diff validation
- ❌ Modifications without test validation
- ❌ Modifications without Supervisor approval
- ❌ Git push operations (never allowed)

**Required Validations:**

1. **Patch Diff Validation:**
   - Must generate patch diff before modification
   - Diff must be reviewed by SupervisorAgent
   - Diff must show only intended changes

2. **Test Validation:**
   - Backend modifications: Must run `mvn test` for affected service
   - Frontend modifications: Must run `npm run test`
   - Tests must pass (exit code 0)
   - Test failures result in warnings (operation may proceed with caution)

3. **Supervisor Approval:**
   - All modifications require SupervisorAgent approval
   - Supervisor checks for zero-impact violations
   - Supervisor checks for safety issues
   - Supervisor checks for code modification attempts

4. **Path Validation:**
   - Files must be within `backend/` or `frontend/` directories
   - No access to system files or other projects
   - Clear warnings for unauthorized paths

**Mode Change:**
- Can only be changed by SupervisorAgent
- Supervisor must explicitly enable this mode
- Should be used for controlled development work

---

### 3. FULL_IMPACT

**Description:** Unrestricted code modifications allowed (still requires Supervisor approval and logging).

**Allowed Operations:**
- ✅ All CONTROLLED_IMPACT operations
- ✅ All file operations (write, modify, delete)
- ✅ All formatting operations
- ✅ All git operations (except push)
- ✅ Infrastructure modifications (with extreme caution)

**Forbidden Operations:**
- ❌ Git push operations (never allowed - safety measure)
- ❌ Operations without Supervisor approval (still required)
- ❌ Operations that violate zero-impact constraints on existing microservices

**Required Validations:**

1. **Supervisor Approval:**
   - Still required for all modifications
   - Supervisor performs safety checks
   - Supervisor can still reject unsafe operations

2. **Audit Logging:**
   - All operations are logged
   - Full audit trail maintained
   - Operations tracked with timestamps and agent names

3. **Path Validation:**
   - Still enforced (backend/, frontend/ only)
   - Warnings for unauthorized paths

**Mode Change:**
- Can only be changed by SupervisorAgent
- Should be used with extreme caution
- Typically for emergency fixes or authorized major changes

---

## Operation Flow

### For Write Operations:

```
1. Agent requests write operation
   ↓
2. ImpactGuard.validateOperation() called
   ↓
3. Check current ImpactMode
   ↓
4. If ZERO_IMPACT → REJECT
   ↓
5. If CONTROLLED_IMPACT → 
   - Generate patch diff
   - Run tests
   - Request Supervisor approval
   - If all pass → ALLOW
   ↓
6. If FULL_IMPACT →
   - Request Supervisor approval
   - Log operation
   - If approved → ALLOW
   ↓
7. Execute operation
   ↓
8. Log result
```

### For Read Operations:

```
1. Agent requests read operation
   ↓
2. ImpactGuard.validateOperation() called
   ↓
3. Read operations always allowed (all modes)
   ↓
4. Execute operation
```

## Mode Change Process

```
1. SupervisorAgent decides to change mode
   ↓
2. SupervisorAgent calls ImpactModeManager.setMode()
   ↓
3. ImpactModeManager validates caller is SupervisorAgent
   ↓
4. If authorized → Change mode
   ↓
5. Log mode change with timestamp and agent name
   ↓
6. Notify all tools of mode change
```

## Safety Guarantees

1. **Default Safety:** System always starts in ZERO_IMPACT mode
2. **Authorization:** Only SupervisorAgent can change modes
3. **Validation:** All modifications go through ImpactGuard
4. **Approval:** All modifications require Supervisor approval (except ZERO_IMPACT where they're blocked)
5. **Audit:** All operations are logged
6. **No Push:** Git push operations are never allowed automatically

## Best Practices

1. **Start in ZERO_IMPACT:** Always begin in the safest mode
2. **Use CONTROLLED_IMPACT for Development:** When making controlled changes
3. **Avoid FULL_IMPACT:** Only use for emergency situations
4. **Always Validate:** Run tests and generate diffs before committing
5. **Monitor Changes:** Review audit logs regularly
6. **Reset After Work:** Return to ZERO_IMPACT after completing work

## Integration with Tools

All code-writing tools must:
1. Check ImpactModeManager before executing
2. Call ImpactGuard.validateOperation()
3. Respect validation results
4. Request Supervisor approval if required
5. Log all operations

Example:
```java
ImpactGuard guard = new ImpactGuard();
ImpactGuard.ValidationResult result = guard.validateOperation("WRITE", filePath);

if (!result.isAllowed()) {
    return "ERROR: " + result.getMessage();
}

// Proceed with operation...
```



