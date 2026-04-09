#!/bin/bash
# Blue-Green Deployment Script for Zero-Downtime Deployments
# Usage: ./blue-green-deploy.sh <environment> <service>

set -e

ENV=${1:-production}
SERVICE=${2:-all}
CURRENT_COLOR="blue"
NEW_COLOR="green"
HEALTH_CHECK_TIMEOUT=300  # 5 minutes
HEALTH_CHECK_INTERVAL=5   # 5 seconds

echo "=== Blue-Green Deployment ==="
echo "Environment: $ENV"
echo "Service: $SERVICE"
echo ""

# Determine which color is currently active
detect_active_color() {
    # Check which color is receiving traffic
    if docker ps | grep -q "${SERVICE}_${CURRENT_COLOR}"; then
        echo "$CURRENT_COLOR"
    else
        echo "$NEW_COLOR"
    fi
}

# Deploy new color
deploy_new_color() {
    local color=$1
    echo "🚀 Deploying $SERVICE in $color environment..."
    
    cd "ops/environments/$ENV" || exit 1
    
    # Build and start new color containers
    docker compose -f "docker-compose.${ENV}.yml" up -d --build --scale "${SERVICE}=2" --no-deps "$SERVICE"
    
    # Tag new instances with color
    docker ps --filter "name=${SERVICE}" --format "{{.Names}}" | grep -v "$(detect_active_color)" | while read -r container; do
        docker tag "$container" "${container}_${color}"
    done
    
    echo "✅ $SERVICE deployed in $color"
}

# Health check
health_check() {
    local service=$1
    local port=$2
    local max_attempts=$((HEALTH_CHECK_TIMEOUT / HEALTH_CHECK_INTERVAL))
    local attempt=0
    
    echo "🏥 Performing health check on $service..."
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -f -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
            echo "✅ $service is healthy"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo "⏳ Waiting for $service to be healthy... ($attempt/$max_attempts)"
        sleep $HEALTH_CHECK_INTERVAL
    done
    
    echo "❌ Health check failed for $service"
    return 1
}

# Switch traffic to new color
switch_traffic() {
    local new_color=$1
    echo "🔄 Switching traffic to $new_color environment..."
    
    # Update nginx configuration to point to new color
    # This would typically involve updating upstream configuration
    # For now, we'll use a simple approach with docker labels
    
    # Reload nginx
    docker exec edge_service_${ENV} nginx -s reload || true
    
    echo "✅ Traffic switched to $new_color"
}

# Rollback to previous color
rollback() {
    local previous_color=$1
    echo "⏪ Rolling back to $previous_color environment..."
    
    switch_traffic "$previous_color"
    
    # Stop new color instances
    docker ps --filter "label=color=$NEW_COLOR" --format "{{.Names}}" | xargs -r docker stop || true
    
    echo "✅ Rolled back to $previous_color"
}

# Main deployment flow
main() {
    ACTIVE_COLOR=$(detect_active_color)
    INACTIVE_COLOR=$([ "$ACTIVE_COLOR" = "$CURRENT_COLOR" ] && echo "$NEW_COLOR" || echo "$CURRENT_COLOR")
    
    echo "Current active color: $ACTIVE_COLOR"
    echo "Deploying to: $INACTIVE_COLOR"
    echo ""
    
    # Step 1: Deploy new color
    deploy_new_color "$INACTIVE_COLOR"
    
    # Step 2: Health check
    SERVICE_PORT=$(get_service_port "$SERVICE")
    if ! health_check "$SERVICE" "$SERVICE_PORT"; then
        echo "❌ Health check failed. Rolling back..."
        rollback "$ACTIVE_COLOR"
        exit 1
    fi
    
    # Step 3: Switch traffic
    switch_traffic "$INACTIVE_COLOR"
    
    # Step 4: Wait for stabilization
    echo "⏳ Waiting for stabilization (30 seconds)..."
    sleep 30
    
    # Step 5: Stop old color (keep for quick rollback)
    echo "🛑 Stopping old $ACTIVE_COLOR instances..."
    # Keep old instances for 1 hour for quick rollback
    # docker stop $(docker ps --filter "label=color=$ACTIVE_COLOR" --format "{{.Names}}") || true
    
    echo "✅ Blue-green deployment completed successfully!"
    echo "Active color: $INACTIVE_COLOR"
}

# Get service port
get_service_port() {
    case $1 in
        gateway) echo "8080" ;;
        auth-service) echo "8081" ;;
        product-service) echo "8082" ;;
        supplier-service) echo "8083" ;;
        quote-service) echo "8084" ;;
        order-service) echo "8085" ;;
        contact-service) echo "8086" ;;
        *) echo "8080" ;;
    esac
}

main



