# Validation Rules

## Overview

The validation system ensures all agent outputs comply with zero-impact mode, structured output requirements, and content quality standards.

## Validation Layers

### 1. JSON Structure Validation

**Purpose**: Ensures valid JSON syntax and required fields.

**Rules**:
- Output must be valid JSON
- Root element must be a JSON object
- Required fields must be present
- Field types must match expected types
- Agent-specific fields must conform to schema

**Required Fields (All Agents)**:
- `agent` (string): Agent name
- `timestamp` (string): ISO8601 timestamp

**Agent-Specific Required Fields**:
- **CTO Agent**: `decision_type`, `review_summary`, `technical_assessment`
- **Architect Agent**: `architecture_proposal`, `compatibility_check`
- **Engineer Agent**: `implementation_spec`, `code_structure`
- **DevOps Agent**: `infrastructure_analysis`, `recommendations`
- **Full-Stack Agent**: `implementation_suggestion`, `integration_specs`
- **Product Manager Agent**: `product_spec`, `roadmap`
- **QA Agent**: `test_plan`, `test_coverage`

### 2. Zero-Impact Mode Compliance

**Purpose**: Ensures no violations of zero-impact mode.

**Rules**:
- Output must NOT suggest modifying existing code
- Output must NOT suggest changing production systems
- Output must NOT suggest database migrations
- Output must NOT suggest configuration changes
- Output should explicitly mention zero-impact or specification-only mode

**Forbidden Patterns**:
- "modify existing code"
- "change production"
- "update backend"
- "edit frontend"
- "delete file"
- "commit changes"
- "breaking change"
- "backward incompatible"
- "migration required"

**Validation**:
- Regex pattern matching against forbidden phrases
- Context-aware detection
- Explicit zero-impact statement check

### 3. Code Modification Prevention

**Purpose**: Prevents any attempts to modify code.

**Rules**:
- Output must NOT contain code modification keywords in modification context
- Output must NOT suggest file operations (create, modify, delete)
- Output must NOT suggest Git operations (commit, push)
- Output must NOT suggest deployment operations
- Output must NOT suggest database schema changes

**Forbidden Keywords (in modification context)**:
- modify/change/update/edit/delete/remove/alter + existing/production/current + code/file/class/method/service/config
- commit/push/deploy/migrate + change/modification/update
- rewrite/refactor/replace + existing/production
- docker/nginx/postgres/redis + config/modify/change

**Validation**:
- Pattern matching with context awareness
- Keyword detection in modification contexts
- Explicit code modification attempt detection

### 4. Structured Output Requirements

**Purpose**: Ensures consistent, structured output format.

**Rules**:
- Output must be valid JSON
- Must include agent identification
- Must include timestamp
- Must follow agent-specific schema
- Must have proper nesting and structure

**Validation**:
- JSON syntax validation
- Schema validation
- Field presence checks
- Type validation

### 5. Content Quality Checks

**Purpose**: Ensures output quality and completeness.

**Rules**:
- Output must not be empty
- Output must meet minimum length requirements
- Output must not contain placeholders ({{}}, TODO, FIXME)
- Output must contain agent-appropriate content
- Required sections must be present

**Minimum Requirements**:
- Minimum length: 100 characters (warning if less)
- No placeholder text
- Agent-specific content present

**Agent-Specific Content Requirements**:
- **CTO Agent**: Must contain approval/review/decision content
- **Architect Agent**: Must contain architecture/design content
- **Engineer Agent**: Must contain implementation/spec content
- **QA Agent**: Must contain test/testing content

### 6. Semantic Validation

**Purpose**: Validates semantic correctness of content.

**Rules**:
- Required fields must not be empty
- Required sections must be present
- Content must be meaningful (not just placeholders)
- Agent name must match expected agent

**Validation**:
- Empty field detection
- Missing section detection
- Content meaningfulness check
- Agent name verification

## Validation Result Structure

```json
{
  "valid": true,
  "errors": [
    {
      "field": "field_name",
      "message": "Error description"
    }
  ],
  "warnings": [
    {
      "field": "field_name",
      "message": "Warning description"
    }
  ],
  "compliance_check": {
    "zero_impact_compliant": true,
    "no_code_modifications": true,
    "structured_output": true,
    "compliance_message": "All compliance checks passed"
  }
}
```

## Validation Flow

1. **JSON Structure Validation**
   - Parse JSON
   - Check syntax
   - Validate required fields
   - Check field types

2. **Semantic Validation**
   - Check empty fields
   - Check missing sections
   - Validate content quality

3. **Zero-Impact Compliance**
   - Pattern matching for violations
   - Context-aware detection
   - Explicit compliance check

4. **Code Modification Prevention**
   - Pattern matching for modification attempts
   - Keyword detection
   - Context validation

5. **Structured Output Validation**
   - Schema validation
   - Format compliance
   - Agent-specific checks

6. **Content Quality Validation**
   - Length checks
   - Placeholder detection
   - Agent-specific content validation

## Error Severity

### Errors (Blocking)
- Invalid JSON syntax
- Missing required fields
- Zero-impact mode violations
- Code modification attempts
- Structured output failures

### Warnings (Non-Blocking)
- Missing recommended sections
- Short output length
- Placeholder content
- Agent name mismatches
- Content quality issues

## Usage Example

```java
AgentOutputValidator validator = new AgentOutputValidator();
ValidationResult result = validator.validate(agentOutput, "cto_agent");

if (result.isValid()) {
    // Output is valid, proceed
} else {
    // Handle errors
    for (ValidationResult.ValidationError error : result.getErrors()) {
        log.error("Validation error: {}", error);
    }
}

// Check compliance
ValidationResult.ComplianceCheck compliance = result.getComplianceCheck();
if (!compliance.isCompliant()) {
    log.warn("Compliance issue: {}", compliance.getComplianceMessage());
}
```

## Integration Points

### With Agents
- Agents should validate their own output before returning
- Validation errors should trigger output regeneration
- Compliance failures should be logged

### With Workflows
- Workflows should validate intermediate results
- Validation failures should stop workflow execution
- Compliance checks should be part of approval process

### With API
- API endpoints should validate agent responses
- Validation errors should return appropriate HTTP status
- Compliance information should be included in responses

## Best Practices

1. **Validate Early**: Validate output as soon as it's generated
2. **Fail Fast**: Stop processing on critical validation errors
3. **Log Warnings**: Log warnings for quality improvements
4. **Compliance First**: Zero-impact compliance is non-negotiable
5. **Structured Output**: Always enforce structured output format
6. **Content Quality**: Ensure meaningful, complete content

## Future Enhancements

1. **Custom Validation Rules**: Allow agent-specific custom rules
2. **Validation Templates**: Predefined validation templates
3. **Auto-Correction**: Attempt to fix minor validation issues
4. **Validation Metrics**: Track validation success rates
5. **ML-Based Validation**: Use ML for content quality assessment



