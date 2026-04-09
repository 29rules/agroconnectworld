# CEO Role Implementation Guide

## ✅ Complete Implementation Summary

This document outlines the enterprise-grade CEO role-based access control system implemented across the AgroConnectWorld platform.

---

## 1. Auth Service Updates

### Role Enum
**File:** `backend/auth-service/src/main/java/com/agroconnectworld/auth/entity/Role.java`

Added `CEO` to the Role enum:
```java
public enum Role {
    ADMIN,
    BUYER,
    SUPPLIER,
    CEO  // ✅ Added
}
```

### Promote to CEO Endpoint
**File:** `backend/auth-service/src/main/java/com/agroconnectworld/auth/controller/AuthController.java`

Added temporary promotion endpoint:
```java
@PostMapping("/promote-ceo")
public ResponseEntity<Map<String, String>> promoteToCeo(@RequestParam String email)
```

**Usage:**
```bash
POST /api/auth/promote-ceo?email=your@email.com
```

**⚠️ WARNING:** This endpoint should be removed or secured in production. It's for testing purposes only.

---

## 2. Gateway Security

### AuthFilter Updates
**File:** `backend/gateway/src/main/java/com/agroconnectworld/gateway/AuthFilter.java`

- Added CEO-only endpoint patterns: `/admin/ceo`
- Validates JWT token and checks for `CEO` role
- Returns `403 Forbidden` for non-CEO users
- Adds user email and role to request headers for downstream services

**Protected Routes:**
- `/admin/ceo/**` - Requires CEO role
- `/api/orders` - Requires authentication
- `/api/quotes` - Requires authentication

---

## 3. Frontend Protection

### ProtectedRoute Component
**File:** `frontend/src/components/ProtectedRoute.jsx`

Already supports role-based access control:
- Checks `requiredRole` or `requiredRoles` array
- Validates user role from AuthContext
- Shows access denied message for unauthorized users

### Route Configuration
**File:** `frontend/src/App.jsx`

CEO dashboard route is protected:
```jsx
<Route 
    path="/admin/ceo" 
    element={
        <ProtectedRoute requiredRoles={['CEO']}>
            <CEODashboard />
        </ProtectedRoute>
    } 
/>
```

### API Service Updates
**File:** `frontend/src/services/api.js`

All AI Company API calls now include Authorization header:
```javascript
const token = getAuthToken();
headers: {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` }),
}
```

---

## 4. AI Company Backend Security

### SecurityFilter
**File:** `ai-company/src/main/java/com/ai/company/api/SecurityFilter.java`

- Validates JWT tokens for all `/ai/**` endpoints
- Requires `CEO` role for access
- Returns `401 Unauthorized` for missing/invalid tokens
- Returns `403 Forbidden` for non-CEO users
- Adds user email and role to request attributes

### JWT Dependencies
**File:** `ai-company/pom.xml`

Added JWT dependencies:
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

### JWT Secret Configuration
**File:** `ai-company/src/main/resources/application.properties`

The SecurityFilter uses the same JWT secret as the auth-service:
```properties
jwt.secret=${JWT_SECRET:2PSi6ESWyf1OImIRWl4FaQ1QhsTpvmU2yi0CJJGqy6Q=}
```

---

## 5. Security Flow

### Complete Request Flow

1. **User Login**
   - User logs in via `/api/auth/login`
   - Receives JWT token with role claim

2. **Promote to CEO** (One-time)
   - Call `/api/auth/promote-ceo?email=your@email.com`
   - User role updated to `CEO`
   - Re-login to get new token with CEO role

3. **Access CEO Dashboard**
   - Frontend: `/admin/ceo` route protected by `ProtectedRoute`
   - Gateway: Validates token and CEO role for `/admin/ceo/**`
   - Frontend renders CEO dashboard

4. **AI Company API Calls**
   - Frontend includes `Authorization: Bearer <token>` header
   - AI Company `SecurityFilter` validates token
   - Checks for `CEO` role
   - Allows access to `/ai/**` endpoints

---

## 6. Testing the Implementation

### Step 1: Promote Your User
```bash
curl -X POST "http://localhost:8080/api/auth/promote-ceo?email=your@email.com"
```

### Step 2: Login Again
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"your@email.com","password":"yourpassword"}'
```

Save the token from the response.

### Step 3: Access CEO Dashboard
1. Open browser: `http://localhost:5173/admin/ceo`
2. Login if not already logged in
3. Dashboard should load if you have CEO role

### Step 4: Test AI Company API
```bash
curl -X GET "http://localhost:8087/ai/status/ceo" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Should return CEO status data.

---

## 7. Security Features

### ✅ Multi-Layer Protection

1. **Frontend Route Protection**
   - `ProtectedRoute` component checks role
   - Redirects to login if not authenticated
   - Shows access denied for wrong role

2. **Gateway Protection**
   - Validates JWT token
   - Checks role claim
   - Blocks unauthorized access

3. **AI Company Protection**
   - SecurityFilter validates all `/ai/**` requests
   - Requires CEO role
   - Logs access attempts

### ✅ Token Validation

- JWT tokens validated at multiple layers
- Token expiration checked
- Role claim verified
- Consistent error responses

### ✅ Error Handling

- `401 Unauthorized` - Missing/invalid token
- `403 Forbidden` - Valid token but wrong role
- Clear error messages for debugging

---

## 8. Configuration

### Environment Variables

**Auth Service:**
- `JWT_SECRET` - Secret key for JWT signing (must match across services)

**Gateway:**
- `JWT_SECRET` - Must match auth-service secret
- `SERVER_PORT` - Gateway port (default: 8080)

**AI Company:**
- `JWT_SECRET` - Must match auth-service secret
- `AI_COMPANY_PORT` - AI Company API port (default: 8087)
- `OPENAI_API_KEY` or `OPENROUTER_API_KEY` - For AI agents

**Frontend:**
- `VITE_AI_API_BASE_URL` - AI Company API base URL (default: http://localhost:8087)

---

## 9. Production Considerations

### ⚠️ Security Recommendations

1. **Remove Promote Endpoint**
   - Remove `/api/auth/promote-ceo` in production
   - Use database migration to set CEO role
   - Or implement admin-only promotion endpoint

2. **Rate Limiting**
   - Add rate limiting to prevent brute force attacks
   - Implement on gateway and AI Company API

3. **Token Refresh**
   - Implement token refresh mechanism
   - Set appropriate token expiration times

4. **Audit Logging**
   - Log all CEO dashboard access attempts
   - Track AI Company API usage
   - Monitor for suspicious activity

5. **HTTPS**
   - Use HTTPS in production
   - Secure cookie storage for tokens

---

## 10. Troubleshooting

### Issue: "Access Denied" on CEO Dashboard

**Check:**
1. User role is `CEO` (not `ROLE_CEO`)
2. Token includes role claim
3. Token is not expired
4. JWT secret matches across services

### Issue: "401 Unauthorized" from AI Company API

**Check:**
1. Token is included in Authorization header
2. Token format: `Bearer <token>`
3. Token is valid and not expired
4. JWT secret matches in AI Company

### Issue: "403 Forbidden" from AI Company API

**Check:**
1. User role is `CEO` (check token claims)
2. Token was issued after role promotion
3. User re-logged in after promotion

---

## 11. Summary

✅ **CEO role added to Role enum**
✅ **Promote endpoint created**
✅ **Gateway protects `/admin/ceo/**` routes**
✅ **Frontend route protected**
✅ **AI Company API secured**
✅ **JWT validation at all layers**
✅ **Consistent error handling**

The CEO role-based access control system is now fully implemented and ready for use!



