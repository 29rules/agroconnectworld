# CEO Portal Test Guide

## 🧪 Testing Checklist

### Prerequisites

1. **Services Running:**
   - ✅ Gateway: `http://localhost:8080`
   - ✅ Frontend: `http://localhost:5173` (dev) or `http://localhost:8080` (production)
   - ✅ Auth Service: Running via Gateway
   - ⚠️ AI Company API: `http://localhost:8087` (needs to be started)

2. **Dependencies:**
   - ✅ Bootstrap Icons installed
   - ✅ All CEO panel components exist

---

## Step-by-Step Testing

### Step 1: Start AI Company API (if not running)

```bash
cd ai-company
mvn spring-boot:run
```

The API will start on port `8087`.

---

### Step 2: Promote User to CEO Role

**Option A: Using curl**
```bash
curl -X POST "http://localhost:8080/api/auth/promote-ceo?email=your@email.com"
```

**Option B: Using browser/Postman**
- URL: `POST http://localhost:8080/api/auth/promote-ceo?email=your@email.com`
- Response should be:
```json
{
  "message": "User promoted to CEO successfully",
  "email": "your@email.com",
  "role": "CEO"
}
```

---

### Step 3: Login to Get New Token

**Using curl:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"your@email.com","password":"yourpassword"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "CEO"
}
```

**Save the token for API testing.**

---

### Step 4: Access CEO Portal

1. Open browser: `http://localhost:5173/admin/ceo`
2. If not logged in, you'll be redirected to `/login`
3. Login with your CEO account
4. You should see the CEO Portal with:
   - ✅ Dark header with "CEO Portal" branding
   - ✅ Navigation tabs (Overview, Engineering, QA, Product, Scrum, DevOps, AI Chat, Alerts)
   - ✅ Icons for each section
   - ✅ Clean white content area

---

### Step 5: Test Navigation Tabs

Click through each tab and verify:

1. **Overview Tab:**
   - ✅ Key metrics cards (Revenue, Users, Orders, Products)
   - ✅ Secondary metrics (Suppliers, Buyers, Quotes, Orders)
   - ✅ Recent Activity section
   - ✅ Quick Actions section

2. **Engineering Tab:**
   - ✅ Engineering metrics displayed
   - ✅ Code commits, PRs, test coverage
   - ✅ Recent commits table
   - ✅ Active projects list

3. **QA Tab:**
   - ✅ QA metrics displayed
   - ✅ Test statistics
   - ✅ Test suites breakdown
   - ✅ Recent bugs table

4. **Product Tab:**
   - ✅ Product metrics displayed
   - ✅ Product status breakdown
   - ✅ Top products table
   - ✅ Category performance

5. **Scrum Tab:**
   - ✅ Scrum metrics displayed
   - ✅ Active sprints
   - ✅ Burndown chart data
   - ✅ Backlog items

6. **DevOps Tab:**
   - ✅ DevOps metrics displayed
   - ✅ Deployment statistics
   - ✅ Infrastructure resources
   - ✅ Service health monitoring

7. **AI Chat Tab:**
   - ✅ Agent selection sidebar
   - ✅ Chat interface
   - ✅ Message input
   - ✅ Send button

8. **Alerts Tab:**
   - ✅ Alert statistics
   - ✅ Critical alerts table
   - ✅ Warning alerts table
   - ✅ Info alerts table
   - ✅ Recently resolved alerts

---

### Step 6: Test AI Company API Endpoints

**Test with Authorization Header:**

```bash
# Replace YOUR_TOKEN with the token from Step 3

# Test CEO Status
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8087/ai/status/ceo

# Test Engineering Status
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8087/ai/status/engineering

# Test QA Status
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8087/ai/status/qa

# Test Product Status
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8087/ai/status/product

# Test Scrum Status
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8087/ai/status/scrum

# Test DevOps Status
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8087/ai/status/devops

# Test CTO Chat
curl -X POST -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message":"What is our system architecture?","sessionId":"test-123"}' \
  http://localhost:8087/ai/ctochat
```

**Expected Responses:**
- ✅ All endpoints return `200 OK` with JSON data
- ✅ Without token: `401 Unauthorized`
- ✅ With non-CEO token: `403 Forbidden`

---

### Step 7: Test Security

**Test 1: Access without login**
- Navigate to `http://localhost:5173/admin/ceo` without logging in
- ✅ Should redirect to `/login`

**Test 2: Access with non-CEO role**
- Login with a user that has `BUYER` or `SUPPLIER` role
- Try to access `/admin/ceo`
- ✅ Should show "Access Denied" message

**Test 3: API without token**
```bash
curl http://localhost:8087/ai/status/ceo
```
- ✅ Should return `401 Unauthorized`

**Test 4: API with invalid token**
```bash
curl -H "Authorization: Bearer invalid_token" \
  http://localhost:8087/ai/status/ceo
```
- ✅ Should return `401 Unauthorized`

**Test 5: API with non-CEO token**
- Login with non-CEO user, get token
- Use that token to access AI Company API
- ✅ Should return `403 Forbidden`

---

### Step 8: Test Responsive Design

1. **Desktop View:**
   - ✅ All tabs visible in navigation
   - ✅ Cards display in grid layout
   - ✅ Tables are readable

2. **Tablet View:**
   - Resize browser to tablet width (768px)
   - ✅ Navigation tabs scroll horizontally
   - ✅ Cards stack appropriately
   - ✅ Tables remain readable

3. **Mobile View:**
   - Resize browser to mobile width (375px)
   - ✅ Navigation tabs scroll horizontally
   - ✅ Cards stack vertically
   - ✅ Tables scroll horizontally

---

### Step 9: Test Data Loading

1. **Check Loading States:**
   - ✅ Each panel shows spinner while loading
   - ✅ Loading state disappears when data loads

2. **Check Error Handling:**
   - Stop AI Company API
   - Refresh CEO Portal
   - ✅ Panels show placeholder data or error message
   - ✅ No crashes or blank screens

---

### Step 10: Test AI Chat

1. **Select CTO Agent:**
   - ✅ Agent selection works
   - ✅ Active agent highlighted

2. **Send Message:**
   - Type a message
   - Click Send
   - ✅ Message appears in chat
   - ✅ Loading indicator shows
   - ✅ Response appears when received

3. **Test Error Handling:**
   - Stop AI Company API
   - Try to send message
   - ✅ Error message displayed
   - ✅ No crashes

---

## 🐛 Common Issues & Solutions

### Issue: "Access Denied" even after promotion

**Solution:**
1. Make sure you logged out and logged back in after promotion
2. Check token in browser DevTools → Application → Local Storage
3. Verify token contains `"role":"CEO"` when decoded

### Issue: AI Company API returns 401

**Solution:**
1. Check if AI Company API is running: `curl http://localhost:8087/actuator/health`
2. Verify JWT secret matches in `ai-company/src/main/resources/application.properties`
3. Make sure token is included in Authorization header

### Issue: Frontend shows "Network error"

**Solution:**
1. Check if AI Company API is running
2. Verify `VITE_AI_API_BASE_URL` in frontend `.env` or default to `http://localhost:8087`
3. Check browser console for CORS errors
4. Verify AI Company API has CORS configured

### Issue: Icons not showing

**Solution:**
1. Verify bootstrap-icons is installed: `npm list bootstrap-icons`
2. Check if CSS is imported: `import 'bootstrap-icons/font/bootstrap-icons.css'`
3. Rebuild frontend if needed

---

## ✅ Success Criteria

The CEO Portal is working correctly if:

- ✅ All services are running
- ✅ User can be promoted to CEO role
- ✅ CEO Portal is accessible at `/admin/ceo`
- ✅ All navigation tabs work
- ✅ All panels display data
- ✅ AI Company API endpoints respond with data
- ✅ Security restrictions work (non-CEO users blocked)
- ✅ Responsive design works on all screen sizes
- ✅ Loading states and error handling work
- ✅ AI Chat interface works

---

## 📊 Test Results Template

```
Date: ___________
Tester: ___________

Services Status:
[ ] Gateway running
[ ] Frontend running
[ ] Auth Service running
[ ] AI Company API running

Functionality:
[ ] User promotion works
[ ] CEO Portal accessible
[ ] All tabs navigate correctly
[ ] All panels display data
[ ] AI Company API responds
[ ] Security restrictions work
[ ] Responsive design works
[ ] Error handling works
[ ] AI Chat works

Issues Found:
_______________________________________
_______________________________________
_______________________________________

Notes:
_______________________________________
_______________________________________
```

---

## 🚀 Quick Test Commands

```bash
# Run full test suite
./test-ceo-portal.sh

# Test specific endpoint
curl -H "Authorization: Bearer TOKEN" http://localhost:8087/ai/status/ceo

# Check services
docker ps | grep -E "gateway|frontend|auth"

# View logs
docker logs gateway_service
docker logs frontend_service
docker logs auth_service
```

---

**Happy Testing! 🎉**



