# Promote User to CEO - Quick Guide

## User Email
**anurag.bishnoi100@gmail.com**

---

## Method 1: Via API Endpoint (Recommended)

### Via Gateway:
```bash
curl -X POST "http://localhost:8080/api/auth/promote-ceo?email=anurag.bishnoi100@gmail.com"
```

### Direct to Auth Service (if gateway blocks it):
```bash
curl -X POST "http://localhost:8081/api/auth/promote-ceo?email=anurag.bishnoi100@gmail.com"
```

**Note:** If you get 403, the endpoint might be blocked. Use Method 2 or 3.

---

## Method 2: Via Database (Direct)

### Access PostgreSQL:
```bash
docker exec -it postgres psql -U your_user -d agro_master
```

### Update User Role:
```sql
UPDATE users SET role='CEO' WHERE email='anurag.bishnoi100@gmail.com';
```

### Verify:
```sql
SELECT email, role FROM users WHERE email='anurag.bishnoi100@gmail.com';
```

---

## Method 3: Via Docker Exec

```bash
docker exec -it auth_service sh
# Then inside container:
curl -X POST "http://localhost:8081/api/auth/promote-ceo?email=anurag.bishnoi100@gmail.com"
```

---

## After Promotion

1. **Logout** from the application (if logged in)
2. **Login again** to get a new token with CEO role
3. **Access CEO Portal:** `http://localhost:5173/admin/ceo`

---

## Verify Promotion

### Check via Login:
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"anurag.bishnoi100@gmail.com","password":"yourpassword"}'
```

The response should include:
```json
{
  "token": "...",
  "role": "CEO"
}
```

---

## Troubleshooting

### Issue: 403 Forbidden on promote endpoint
**Solution:** The endpoint might be protected. Use Method 2 (database) or ensure the endpoint is public.

### Issue: User not found
**Solution:** Make sure the user exists. Register first if needed:
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"anurag.bishnoi100@gmail.com","password":"yourpassword","name":"Anurag","phone":"1234567890","role":"BUYER"}'
```

Then promote to CEO.

### Issue: Role not updating after promotion
**Solution:** 
1. Make sure you logout and login again
2. Check token in browser DevTools → Application → Local Storage
3. Decode JWT token and verify role claim is "CEO"

---

## Quick Test

After promotion and re-login:
1. Open: `http://localhost:5173/admin/ceo`
2. Should see CEO Portal with dark header
3. All navigation tabs should work
4. AI Company API should connect (if running)



