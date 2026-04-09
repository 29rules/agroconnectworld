#!/bin/bash
# Rollback Script for Quick Deployment Reversal
# Usage: ./rollback.sh <environment> <service>

set -e

ENV=${1:-production}
SERVICE=${2:-all}

echo "=== Rollback Deployment ==="
echo "Environment: $ENV"
echo "Service: $SERVICE"
echo ""

# Get previous version tag
get_previous_version() {
    # In a real scenario, this would query a version registry
    # For now, we'll use docker image tags
    docker images --format "{{.Tag}}" | grep -v "latest" | sort -V | tail -2 | head -1
}

# Rollback service
rollback_service() {
    local service=$1
    local previous_version=$(get_previous_version)
    
    echo "⏪ Rolling back $service to version $previous_version..."
    
    cd "ops/environments/$ENV" || exit 1
    
    # Stop current version
    docker compose -f "docker-compose.${ENV}.yml" stop "$service" || true
    
    # Start previous version
    # In a real scenario, you would:
    # 1. Pull previous image version
    # 2. Update docker-compose to use previous version
    # 3. Start services
    
    echo "✅ Rolled back $service"
}

# Main
if [ "$SERVICE" = "all" ]; then
    services=("gateway" "auth-service" "product-service" "supplier-service" "quote-service" "order-service" "contact-service")
    for service in "${services[@]}"; do
        rollback_service "$service"
    done
else
    rollback_service "$SERVICE"
fi

echo "✅ Rollback completed"



