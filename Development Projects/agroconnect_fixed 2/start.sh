#!/usr/bin/env bash
# =============================================================
# AgroConnectWorld — One-Command Startup Script
# =============================================================
# Usage:
#   ./start.sh              → full stack (Docker)
#   ./start.sh local        → local dev (no Docker, manual services)
#   ./start.sh down         → stop everything
#   ./start.sh seed         → load seed data only
#   ./start.sh status       → show service health
# =============================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
OPS_DIR="$SCRIPT_DIR/ops"
FRONTEND_DIR="$SCRIPT_DIR/frontend"
AI_DIR="$SCRIPT_DIR/ai-company"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
info()    { echo -e "${BLUE}[INFO]${NC}  $*"; }
success() { echo -e "${GREEN}[OK]${NC}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*"; exit 1; }

# ── Pre-flight checks ────────────────────────────────────────
check_deps() {
  command -v docker  >/dev/null 2>&1 || error "Docker is not installed. https://docs.docker.com/get-docker/"
  command -v docker compose version >/dev/null 2>&1 || error "Docker Compose v2 not found. Update Docker Desktop or install the plugin."
}

# ── Validate .env ────────────────────────────────────────────
check_env() {
  local env_file="$OPS_DIR/.env"
  if [[ ! -f "$env_file" ]]; then
    error ".env not found at $env_file\nRun: cp $OPS_DIR/.env.example $OPS_DIR/.env  and fill in values"
  fi

  # shellcheck disable=SC1090
  source "$env_file"

  if [[ -z "${OPENROUTER_API_KEY:-}" && -z "${OPENAI_API_KEY:-}" ]]; then
    warn "OPENROUTER_API_KEY is not set in ops/.env"
    warn "CEO portal AI features will not work."
    warn "Get a free key at https://openrouter.ai/ and set OPENROUTER_API_KEY in ops/.env"
    echo ""
  else
    success "API key found — AI Company will be enabled"
  fi

  if [[ "${ADMIN_SECRET:-change-me-to-a-strong-random-secret}" == "change-me-to-a-strong-random-secret" ]]; then
    warn "ADMIN_SECRET is still the default value. Please change it in ops/.env"
  fi
}

# ── Start full Docker stack ──────────────────────────────────
start_docker() {
  info "Starting AgroConnectWorld (Docker Compose)..."
  cd "$OPS_DIR"

  info "Starting database..."
  docker compose --profile db up -d
  wait_for "postgres_service" "PostgreSQL"

  info "Starting cache..."
  docker compose --profile cache up -d
  wait_for "redis_service" "Redis"

  info "Starting all backend services + AI company..."
  docker compose --profile api up -d --build

  info "Waiting for services to be healthy (this may take 60-90s on first run)..."
  local services=(auth_service product_service supplier_service quote_service order_service contact_service gateway_service ai_company_service)
  for svc in "${services[@]}"; do
    wait_for "$svc" "$svc"
  done

  info "Starting frontend + nginx..."
  docker compose up -d frontend nginx

  wait_for "frontend_service" "Frontend"
  wait_for "edge_service"     "Nginx"

  echo ""
  success "════════════════════════════════════════════════"
  success "  AgroConnectWorld is running!"
  success "════════════════════════════════════════════════"
  echo ""
  echo -e "  ${GREEN}App URL:${NC}         http://localhost:8080"
  echo -e "  ${GREEN}API Gateway:${NC}     http://localhost:8080/api"
  echo -e "  ${GREEN}AI Company API:${NC}  http://localhost:8087"
  echo -e "  ${GREEN}CEO Portal:${NC}      http://localhost:8080/admin/ceo"
  echo ""
  echo -e "  ${YELLOW}Demo Accounts (password: Password123!):${NC}"
  echo -e "    ceo@agroconnect.com      → CEO Portal"
  echo -e "    buyer@agroconnect.com    → Buyer Dashboard"
  echo -e "    supplier@agroconnect.com → Supplier Dashboard"
  echo -e "    admin@agroconnect.com    → Admin Dashboard"
  echo ""
  warn "If this is your first run, load seed data: ./start.sh seed"
  echo ""
}

# ── Wait for container health ────────────────────────────────
wait_for() {
  local container="$1"
  local label="$2"
  local max=30
  local count=0

  echo -n "  Waiting for $label..."
  while ! docker inspect --format='{{.State.Health.Status}}' "$container" 2>/dev/null | grep -q "healthy"; do
    # Also check if container is running (some have no healthcheck)
    if docker inspect --format='{{.State.Status}}' "$container" 2>/dev/null | grep -q "running"; then
      if ! docker inspect --format='{{.State.Health.Status}}' "$container" 2>/dev/null | grep -qE "starting|unhealthy"; then
        break
      fi
    fi
    sleep 3
    count=$((count+1))
    echo -n "."
    if [[ $count -ge $max ]]; then
      echo ""
      warn "$label health check timed out — check: docker logs $container"
      return
    fi
  done
  echo -e " ${GREEN}ready${NC}"
}

# ── Load seed data ───────────────────────────────────────────
load_seed() {
  info "Loading seed data..."
  local seed_file="$OPS_DIR/seed.sql"
  if [[ ! -f "$seed_file" ]]; then
    error "Seed file not found: $seed_file"
  fi

  # Wait a moment for schema creation
  sleep 3
  docker exec -i postgres_service psql -U agro -d agro_master < "$seed_file" && \
    success "Seed data loaded successfully!" || \
    warn "Seed data may have partially loaded (rows might already exist)"
}

# ── Stop everything ──────────────────────────────────────────
stop_all() {
  info "Stopping AgroConnectWorld..."
  cd "$OPS_DIR"
  docker compose --profile api --profile db --profile cache --profile storage down
  success "All services stopped"
}

# ── Show status ──────────────────────────────────────────────
show_status() {
  echo ""
  echo -e "${BLUE}═══════════════ Service Status ═══════════════${NC}"
  local services=(
    "edge_service:Nginx"
    "frontend_service:Frontend"
    "gateway_service:Gateway"
    "auth_service:Auth"
    "product_service:Product"
    "supplier_service:Supplier"
    "quote_service:Quote"
    "order_service:Order"
    "contact_service:Contact"
    "ai_company_service:AI Company"
    "postgres_service:PostgreSQL"
    "redis_service:Redis"
  )

  for entry in "${services[@]}"; do
    local container="${entry%%:*}"
    local label="${entry##*:}"
    local status
    status=$(docker inspect --format='{{.State.Status}}' "$container" 2>/dev/null || echo "not found")
    local health
    health=$(docker inspect --format='{{.State.Health.Status}}' "$container" 2>/dev/null || echo "")

    if [[ "$status" == "running" ]]; then
      if [[ "$health" == "healthy" || -z "$health" ]]; then
        echo -e "  ${GREEN}✓${NC} $label ($container)"
      elif [[ "$health" == "starting" ]]; then
        echo -e "  ${YELLOW}~${NC} $label — starting..."
      else
        echo -e "  ${RED}✗${NC} $label — $health"
      fi
    else
      echo -e "  ${RED}✗${NC} $label — $status"
    fi
  done
  echo ""
}

# ── Local dev mode ───────────────────────────────────────────
start_local() {
  info "Local dev mode — start Postgres manually or via Docker, then run each service."
  echo ""
  echo "1. Start just the database:"
  echo "   cd ops && docker compose --profile db --profile cache up -d"
  echo ""
  echo "2. Start each backend service (in separate terminals):"
  for svc in auth-service product-service supplier-service quote-service order-service contact-service; do
    echo "   cd backend/$svc && SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/agro_master mvn spring-boot:run"
  done
  echo ""
  echo "3. Start gateway:"
  echo "   cd backend/gateway && mvn spring-boot:run"
  echo ""
  echo "4. Start AI Company:"
  echo "   cd ai-company && export OPENROUTER_API_KEY=your-key && mvn spring-boot:run"
  echo ""
  echo "5. Start frontend:"
  echo "   cd frontend && npm install && npm run dev"
  echo ""
  echo "6. Load seed data:"
  echo "   ./start.sh seed"
}

# ── Main ─────────────────────────────────────────────────────
MODE="${1:-up}"

case "$MODE" in
  up|start|"")
    check_deps
    check_env
    start_docker
    ;;
  seed)
    load_seed
    ;;
  down|stop)
    stop_all
    ;;
  status)
    show_status
    ;;
  local)
    start_local
    ;;
  *)
    echo "Usage: $0 [up|down|seed|status|local]"
    exit 1
    ;;
esac
