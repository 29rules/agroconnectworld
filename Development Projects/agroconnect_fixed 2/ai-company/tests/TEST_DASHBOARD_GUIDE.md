# AI Company Test Dashboard Guide

## Overview

The **AI System Test Dashboard** (`RunAllTests.java`) is a comprehensive test runner that executes all system tests for the AgroConnectWorld AI Company meta-layer and provides a color-coded, structured report of test results.

## Quick Start

### Running the Dashboard

```bash
cd ai-company
export OPENAI_API_KEY="your-api-key-here"
mvn -q exec:java -Dexec.mainClass="com.ai.company.tests.RunAllTests"
```

### Alternative: Direct Java Execution

```bash
cd ai-company
export OPENAI_API_KEY="your-api-key-here"
mvn dependency:build-classpath -DincludeScope=compile -q -Dmdep.outputFile=classpath.txt
CLASSPATH=$(cat classpath.txt):target/classes
java -cp "$CLASSPATH" com.ai.company.tests.RunAllTests
```

## Test Suite Components

The dashboard runs the following test classes:

1. **TestAgents.java**
   - Tests all individual AI agents (CTO, Architect, Engineer, DevOps, Full-Stack, QA, Product Manager, Supervisor)
   - Validates agent initialization and basic functionality
   - Ensures agents respond correctly to test queries

2. **TestOrchestrator.java**
   - Tests the MultiAgentOrchestrator pipeline
   - Validates end-to-end workflow execution
   - Tests sequential agent coordination

3. **TestPlanner.java**
   - Tests the Task Planner system
   - Validates task decomposition and graph generation
   - Ensures proper task assignment to agents

4. **TestSupervisor.java**
   - Tests Supervisor Agent safety validation
   - Validates constraint enforcement
   - Ensures unsafe requests are properly blocked

## Output Format

### Color Coding

- **Green** (`[PASS]`) - Test passed successfully
- **Red** (`[FAIL]`) - Test failed with error
- **Yellow** (`[Running]`) - Test currently executing
- **Blue** (`[INFO]`, Titles) - Informational messages and section headers

### Example Output

```
--------------------------------------------------------------------------------
                        AI COMPANY TEST DASHBOARD
--------------------------------------------------------------------------------

[INFO]    Zero-Impact Mode: ENABLED
[INFO]    All tests operate in read-only mode
[INFO]    No modifications to existing AgroConnectWorld code

[Running] Agent Tests...
[PASS]    All agents responded.

[Running] Orchestrator Test...
[PASS]    Workflow executed successfully.

[Running] Task Planner Test...
[PASS]    Task graph generated.

[Running] Supervisor Safety Test...
[PASS]    Supervisor blocked unsafe request.

--------------------------------------------------------------------------------
                                SUMMARY
--------------------------------------------------------------------------------

Total Tests: 4
Passed: 4
Failed: 0

[PASS]    All systems operational. Zero-impact mode verified.

--------------------------------------------------------------------------------
```

## Adding New Tests

To add a new test to the dashboard:

### Step 1: Create Test Class

Create a new test class in `/ai-company/tests/`:

```java
package com.ai.company.tests;

public class TestYourFeature {
    public static void main(String[] args) {
        try {
            // Your test logic here
            System.out.println("Test passed");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
```

### Step 2: Add to Dashboard

Edit `RunAllTests.java` and add a new test call:

```java
runTest("Your Feature Test", "TestYourFeature", "Your feature works correctly");
```

### Step 3: Test Requirements

Your test class must:
- Have a `public static void main(String[] args)` method
- Exit with code 0 on success
- Exit with code 1 (or throw exception) on failure
- Be in package `com.ai.company.tests`

## Interpreting Output

### Success Indicators

- **All tests show `[PASS]`** - System is fully operational
- **Summary shows "All systems operational"** - Zero-impact mode verified
- **No red error messages** - All components functioning correctly

### Failure Indicators

- **Any test shows `[FAIL]`** - That specific component has an issue
- **Summary shows failed count > 0** - Some tests need attention
- **Red error messages** - Review the specific error for details

### Common Issues

1. **"Test class not found"**
   - Ensure test class is in `com.ai.company.tests` package
   - Verify class name matches exactly (case-sensitive)

2. **"Test class missing main method"**
   - Ensure test class has `public static void main(String[] args)`

3. **"OPENAI_API_KEY not found"**
   - Set environment variable: `export OPENAI_API_KEY="your-key"`

4. **"Test failed: Connection timeout"**
   - Check internet connection
   - Verify API key is valid
   - Check API rate limits

5. **"Test failed: NullPointerException"**
   - Review test logic for null checks
   - Ensure all dependencies are initialized

## Troubleshooting

### Test Execution Issues

**Problem:** Tests fail with ClassNotFoundException
```bash
# Solution: Recompile all classes
cd ai-company
mvn clean compile
```

**Problem:** Tests fail with API errors
```bash
# Solution: Verify API key and network
export OPENAI_API_KEY="your-key"
curl -H "Authorization: Bearer $OPENAI_API_KEY" https://api.openai.com/v1/models
```

**Problem:** Tests hang or timeout
```bash
# Solution: Increase timeout in test class
.timeout(Duration.ofSeconds(120))
```

### Output Formatting Issues

**Problem:** Colors not displaying
- ANSI colors require a terminal that supports them
- Use `TERM=xterm-256color` if needed
- Colors are optional - functionality works without them

**Problem:** Output is garbled
- Ensure terminal supports UTF-8
- Check terminal encoding settings

### Zero-Impact Mode Verification

The dashboard verifies zero-impact mode by:
- Running all tests in read-only mode
- Ensuring no file system modifications
- Validating no real microservice calls
- Confirming all outputs are specifications only

If any test attempts to modify existing code, it should fail with a clear error message.

## Best Practices

1. **Keep Tests Independent**
   - Each test should be self-contained
   - Don't rely on execution order
   - Clean up any temporary state

2. **Handle Exceptions Gracefully**
   - Catch and report specific errors
   - Provide actionable error messages
   - Exit with appropriate codes

3. **Maintain Test Performance**
   - Keep test execution time reasonable
   - Use appropriate timeouts
   - Avoid unnecessary API calls

4. **Document Test Purpose**
   - Add clear JavaDoc comments
   - Explain what each test validates
   - Note any special requirements

## Advanced Usage

### Running Individual Tests

You can run individual tests directly:

```bash
# Run only TestAgents
mvn exec:java -Dexec.mainClass="com.ai.company.tests.TestAgents"

# Run only TestOrchestrator
mvn exec:java -Dexec.mainClass="com.ai.company.tests.TestOrchestrator"
```

### Custom Test Execution

Modify `RunAllTests.java` to:
- Skip specific tests
- Add custom test logic
- Integrate with CI/CD systems
- Generate test reports

### Integration with CI/CD

Example GitHub Actions workflow:

```yaml
name: AI Company Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Run Test Dashboard
        env:
          OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
        run: |
          cd ai-company
          mvn -q exec:java -Dexec.mainClass="com.ai.company.tests.RunAllTests"
```

## Support

For issues or questions:
1. Check this guide first
2. Review test class JavaDoc
3. Examine error messages carefully
4. Verify environment setup

## Zero-Impact Mode Guarantee

All tests in this dashboard:
- ✅ Operate in read-only mode
- ✅ Do not modify existing AgroConnectWorld code
- ✅ Do not call real microservices
- ✅ Only generate specifications and documentation
- ✅ Are safe to run in any environment

The dashboard explicitly verifies and reports zero-impact mode compliance.



