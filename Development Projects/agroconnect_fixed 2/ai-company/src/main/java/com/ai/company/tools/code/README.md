# Code Tools Package

This package provides safe, controlled code manipulation tools for the AI Company agents.

## Tools Overview

### 1. CodeReaderTool.java
**Read-only file operations**

- `readFile(filePath)` - Read a single file
- `readFolder(folderPath)` - Read folder recursively
- `listFiles(folderPath)` - List files without reading contents

**Safety:**
- ✅ Read-only operations
- ✅ Path validation (only backend/, frontend/, ai-company/)
- ✅ No modifications

### 2. CodeWriterTool.java
**Write new files with approval**

- `writeFile(filePath, content, sessionId)` - Create new file

**Safety:**
- ✅ Requires Controlled-Impact Mode ON
- ✅ Requires Supervisor approval
- ✅ Only creates new files (does not overwrite)
- ✅ Path validation (only backend/, frontend/)

### 3. CodeModifierTool.java
**Modify existing files with validation**

- `insertAtLine(filePath, lineNumber, text, sessionId)` - Insert text
- `replaceLines(filePath, startLine, endLine, newText, sessionId)` - Replace text
- `deleteLines(filePath, startLine, endLine, sessionId)` - Delete text

**Safety:**
- ✅ Requires Controlled-Impact Mode ON
- ✅ Requires Supervisor approval
- ✅ Validates line numbers before applying
- ✅ Path validation (only backend/, frontend/)

### 4. CodeFormatterTool.java
**Format code files**

- `formatJavaFile(filePath)` - Format Java with Google Java Format
- `formatJsFile(filePath)` - Format JS/TS/React with Prettier

**Safety:**
- ✅ Requires Controlled-Impact Mode ON
- ✅ Path validation (only backend/, frontend/)
- ✅ Uses standard formatters

### 5. TestRunnerTool.java
**Run tests**

- `runBackendTests(servicePath)` - Run Maven tests
- `runFrontendTests()` - Run npm tests

**Safety:**
- ✅ Read-only operation (runs tests, doesn't modify)
- ✅ No Controlled-Impact Mode required
- ✅ Returns console output

### 6. GitCommitTool.java
**Git operations with approval**

- `stageChanges()` - Stage all changes
- `stageFiles(filePaths)` - Stage specific files
- `commitChanges(commitMessage, sessionId)` - Commit with approval
- `getGitStatus()` - Get repository status (read-only)

**Safety:**
- ✅ Requires Controlled-Impact Mode ON
- ✅ Requires Supervisor approval for commits
- ✅ **DO NOT push** (safety measure)
- ✅ Validates changes before committing

## Controlled-Impact Mode

All write/modify operations require **Controlled-Impact Mode** to be enabled:

```java
CodeWriterTool.setControlledImpactMode(true);
CodeModifierTool.setControlledImpactMode(true);
CodeFormatterTool.setControlledImpactMode(true);
GitCommitTool.setControlledImpactMode(true);
```

When OFF, all write operations are blocked with clear error messages.

## Supervisor Approval

Write operations require Supervisor approval:

1. Tool requests approval from SupervisorAgent
2. Supervisor validates against constraints
3. If violations detected, operation is rejected
4. If approved, operation proceeds

## Path Validation

All tools validate paths to ensure:
- Files are within permitted directories (backend/, frontend/, ai-company/)
- No access to system files or other projects
- Clear warnings for unauthorized paths

## Usage Example

```java
// Initialize tools
ChatLanguageModel chatModel = ...;
CodeReaderTool reader = new CodeReaderTool();
CodeWriterTool writer = new CodeWriterTool(chatModel);

// Enable Controlled-Impact Mode
CodeWriterTool.setControlledImpactMode(true);

// Read file (always allowed)
String content = reader.readFile("backend/gateway/pom.xml");

// Write file (requires approval)
String result = writer.writeFile(
    "backend/new-service/pom.xml",
    "<project>...</project>",
    "session-123"
);
```

## Safety Guarantees

1. **Zero-Impact by Default**: All write operations disabled unless Controlled-Impact Mode ON
2. **Supervisor Oversight**: All modifications require approval
3. **Path Restrictions**: Only permitted directories accessible
4. **No Push Operations**: Git commits never push automatically
5. **Validation First**: Changes validated before application

## Integration with Agents

These tools can be provided to agents via LangChain4j:

```java
CodeReaderTool reader = new CodeReaderTool();
CodeWriterTool writer = new CodeWriterTool(chatModel);

AiServices.builder(MyAgent.class)
    .chatLanguageModel(chatModel)
    .tools(reader, writer)
    .build();
```

Agents can then use these tools safely, with automatic Supervisor oversight.



