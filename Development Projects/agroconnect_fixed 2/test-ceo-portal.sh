#!/bin/bash

# CEO Portal Test Script
# Tests the CEO Portal functionality

echo "🧪 CEO Portal Test Suite"
echo "========================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Check if services are running
echo "1️⃣  Checking Docker Services..."
if command -v docker &> /dev/null; then
    GATEWAY=$(docker ps --filter "name=gateway" --format "{{.Status}}" 2>/dev/null)
    FRONTEND=$(docker ps --filter "name=frontend" --format "{{.Status}}" 2>/dev/null)
    AUTH=$(docker ps --filter "name=auth" --format "{{.Status}}" 2>/dev/null)
    
    if [ ! -z "$GATEWAY" ]; then
        echo -e "   ${GREEN}✅ Gateway: Running${NC}"
    else
        echo -e "   ${RED}❌ Gateway: Not running${NC}"
    fi
    
    if [ ! -z "$FRONTEND" ]; then
        echo -e "   ${GREEN}✅ Frontend: Running${NC}"
    else
        echo -e "   ${YELLOW}⚠️  Frontend: Not running (may be running via npm)${NC}"
    fi
    
    if [ ! -z "$AUTH" ]; then
        echo -e "   ${GREEN}✅ Auth Service: Running${NC}"
    else
        echo -e "   ${RED}❌ Auth Service: Not running${NC}"
    fi
else
    echo -e "   ${YELLOW}⚠️  Docker not available${NC}"
fi

echo ""

# Test 2: Check frontend files
echo "2️⃣  Checking Frontend Files..."
if [ -f "frontend/src/pages/ceo/CEODashboard.jsx" ]; then
    echo -e "   ${GREEN}✅ CEODashboard.jsx exists${NC}"
else
    echo -e "   ${RED}❌ CEODashboard.jsx missing${NC}"
fi

PANELS=("EngineeringPanel.jsx" "QAPanel.jsx" "ProductPanel.jsx" "ScrumPanel.jsx" "DevOpsPanel.jsx" "AgentChat.jsx" "Alerts.jsx")
for panel in "${PANELS[@]}"; do
    if [ -f "frontend/src/pages/ceo/$panel" ]; then
        echo -e "   ${GREEN}✅ $panel exists${NC}"
    else
        echo -e "   ${RED}❌ $panel missing${NC}"
    fi
done

echo ""

# Test 3: Check API endpoints
echo "3️⃣  Testing API Endpoints..."

# Test Gateway health
echo -n "   Testing Gateway health... "
GATEWAY_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null)
if [ "$GATEWAY_HEALTH" = "200" ]; then
    echo -e "${GREEN}✅ (HTTP $GATEWAY_HEALTH)${NC}"
else
    echo -e "${RED}❌ (HTTP $GATEWAY_HEALTH)${NC}"
fi

# Test Auth service
echo -n "   Testing Auth service... "
AUTH_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d '{"email":"test","password":"test"}' 2>/dev/null)
if [ "$AUTH_TEST" = "200" ] || [ "$AUTH_TEST" = "400" ] || [ "$AUTH_TEST" = "401" ]; then
    echo -e "${GREEN}✅ (HTTP $AUTH_TEST)${NC}"
else
    echo -e "${RED}❌ (HTTP $AUTH_TEST)${NC}"
fi

# Test AI Company API (if running)
echo -n "   Testing AI Company API... "
AI_API=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8087/actuator/health 2>/dev/null)
if [ "$AI_API" = "200" ]; then
    echo -e "${GREEN}✅ (HTTP $AI_API)${NC}"
else
    echo -e "${YELLOW}⚠️  Not running (HTTP $AI_API) - Start with: cd ai-company && mvn spring-boot:run${NC}"
fi

echo ""

# Test 4: Check frontend dev server
echo "4️⃣  Checking Frontend Dev Server..."
FRONTEND_PORT=$(lsof -ti:5173 2>/dev/null || lsof -ti:5174 2>/dev/null)
if [ ! -z "$FRONTEND_PORT" ]; then
    echo -e "   ${GREEN}✅ Frontend dev server running on port $FRONTEND_PORT${NC}"
else
    echo -e "   ${YELLOW}⚠️  Frontend dev server not running${NC}"
    echo "      Start with: cd frontend && npm run dev"
fi

echo ""

# Test 5: Check dependencies
echo "5️⃣  Checking Dependencies..."
if [ -f "frontend/package.json" ]; then
    if grep -q "bootstrap-icons" frontend/package.json; then
        echo -e "   ${GREEN}✅ bootstrap-icons installed${NC}"
    else
        echo -e "   ${RED}❌ bootstrap-icons not found in package.json${NC}"
        echo "      Install with: cd frontend && npm install bootstrap-icons"
    fi
fi

echo ""

# Summary
echo "📋 Test Summary"
echo "==============="
echo ""
echo "✅ To test the CEO Portal:"
echo ""
echo "1. Start services:"
echo "   docker compose --profile api up -d"
echo ""
echo "2. Start AI Company API (if needed):"
echo "   cd ai-company && mvn spring-boot:run"
echo ""
echo "3. Start frontend (if not running):"
echo "   cd frontend && npm run dev"
echo ""
echo "4. Promote user to CEO:"
echo "   curl -X POST 'http://localhost:8080/api/auth/promote-ceo?email=your@email.com'"
echo ""
echo "5. Login and access:"
echo "   http://localhost:5173/admin/ceo"
echo ""
echo "6. Test AI Company endpoints:"
echo "   curl -H 'Authorization: Bearer YOUR_TOKEN' http://localhost:8087/ai/status/ceo"
echo ""



