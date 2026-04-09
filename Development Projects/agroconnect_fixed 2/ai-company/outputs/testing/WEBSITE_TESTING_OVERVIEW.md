# Website Comprehensive Testing System

## Overview

The AI Company now includes a comprehensive website testing system that tests AgroConnectWorld from all angles across all environments.

## Testing Agent: WebsiteTestAgent

### Responsibilities

1. **Functional Testing**
   - Test all user flows (buyer registration, supplier registration, product browsing, cart, checkout, orders, quotes)
   - Verify UI components render correctly
   - Test form validations and submissions
   - Test navigation and routing
   - Test authentication and authorization flows
   - Test role-based access (Buyer, Supplier, Admin)

2. **API Testing**
   - Test all REST endpoints (`/api/auth/*`, `/api/products/*`, `/api/quotes/*`, `/api/orders/*`, `/api/contact`)
   - Verify request/response formats
   - Test authentication (JWT tokens)
   - Test error handling (400, 401, 403, 404, 500)
   - Test data validation
   - Test CORS configuration

3. **Performance Testing**
   - Measure page load times
   - Test API response times
   - Check resource usage (CPU, memory)
   - Test under load (multiple concurrent requests)
   - Identify bottlenecks

4. **Security Testing**
   - Test authentication mechanisms
   - Test authorization (role-based access)
   - Test input validation (SQL injection, XSS)
   - Test CORS configuration
   - Test JWT token security
   - Test sensitive data exposure

5. **UI/UX Testing**
   - Test responsive design (mobile, tablet, desktop)
   - Test accessibility (WCAG compliance)
   - Test cross-browser compatibility
   - Test user experience flows
   - Test error messages and feedback

6. **Integration Testing**
   - Test frontend-backend integration
   - Test database connectivity
   - Test external service integration (OpenRouter API for chatbot)
   - Test Docker container health
   - Test service dependencies

7. **Regression Testing**
   - Verify existing features still work
   - Test for breaking changes
   - Test backward compatibility

## Test Tools

### 1. UITestTool
- `testPageLoad(String pageUrl)` - Test if a page loads successfully
- `testMultiplePages(String pageUrls)` - Test multiple pages
- `testUserFlow(String flowName, String pageUrls)` - Test user flow sequences

### 2. APITestTool
- `testAPIEndpoint(String endpointUrl, String method, String requestBody, String authToken)` - Test API endpoints
- `testAPIAuthentication(String protectedEndpoint, String authToken)` - Test authentication

### 3. PerformanceTestTool
- `measureResponseTime(String endpointUrl, int numberOfRequests)` - Measure response times
- `testConcurrentLoad(String endpointUrl, int concurrentRequests)` - Test concurrent load

### 4. SecurityTestTool
- `testCORS(String endpointUrl)` - Test CORS configuration
- `testAuthenticationSecurity(String protectedEndpoint)` - Test authentication security

### 5. HealthCheckTool (existing)
- `checkServiceHealth(String serviceUrl)` - Check service health
- `checkMultipleServices(String serviceUrls)` - Check multiple services
- `checkContainerHealth(String containerName)` - Check Docker container health

## Environments

### Development
- **URL**: `http://localhost:8080`
- **Purpose**: Local development and testing
- **Ports**: 8080 (nginx), 5432 (postgres), 6379 (redis)

### UAT
- **URL**: `http://uat.agroconnectworld.com` (configurable)
- **Purpose**: User Acceptance Testing
- **Ports**: 8080 (nginx), 5433 (postgres), 6380 (redis)

### Pre-Production
- **URL**: `http://preprod.agroconnectworld.com` (configurable)
- **Purpose**: Final testing before production
- **Ports**: 8080 (nginx), 5434 (postgres), 6381 (redis)

### Production
- **URL**: `https://agroconnectworld.com` (configurable)
- **Purpose**: Live production environment
- **Ports**: 80/443 (nginx), internal (postgres/redis)

## Usage

### Run Comprehensive Tests

```bash
cd ai-company
mvn exec:java -Dexec.mainClass="com.ai.company.tests.TestWebsiteComprehensive"
```

### Test Specific Environment

```java
WebsiteTestOrchestrator orchestrator = new WebsiteTestOrchestrator(chatModel);
String report = orchestrator.runEnvironmentTests("development", "http://localhost:8080");
```

### Test Specific Feature

```java
String report = orchestrator.testFeatureAcrossEnvironments(
    "User Registration", 
    environments
);
```

## Test Report Format

The test reports include:
- Test category
- Test name
- Status (PASS/FAIL/SKIP)
- Details and findings
- Recommendations
- Summary statistics

## Integration with AI Company

The WebsiteTestAgent integrates with:
- **SupervisorAgent**: For approval of test results
- **QAAgent**: For test plan creation
- **DevOpsAgent**: For deployment validation
- **DeploymentWorkflow**: For pre-deployment testing

## Zero-Impact Mode

All testing tools operate in **ZERO-IMPACT MODE**:
- Only read and test, never modify code or data
- Use read-only operations
- Report findings without making changes
- Safe to run in any environment

## Continuous Testing

The AI Company can run tests:
- Before deployments
- After deployments
- On schedule (via DeploymentScheduler)
- On-demand via API

## Next Steps

1. Configure environment URLs in `.env` files
2. Set up test credentials for each environment
3. Run initial comprehensive test suite
4. Integrate with CI/CD pipeline
5. Set up automated testing schedule



