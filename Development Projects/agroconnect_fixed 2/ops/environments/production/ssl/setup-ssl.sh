#!/bin/bash
# SSL Certificate Setup Script for Production
# This script helps set up SSL certificates using Let's Encrypt

set -e

DOMAIN="www.agroconnectworld.com"
DOMAIN_ALT="agroconnectworld.com"  # Also cover non-www
EMAIL="${SSL_EMAIL:-admin@agroconnectworld.com}"
SSL_DIR="./ssl"

echo "=== SSL Certificate Setup for Production ==="
echo "Domain: $DOMAIN"
echo "Alt Domain: $DOMAIN_ALT"
echo "Email: $EMAIL"
echo ""

# Create SSL directory
mkdir -p "$SSL_DIR"

# Check if certbot is available
if ! command -v certbot &> /dev/null; then
    echo "⚠️  Certbot not found. Installing certbot..."
    echo "Please install certbot:"
    echo "  macOS: brew install certbot"
    echo "  Linux: sudo apt-get install certbot"
    echo ""
    echo "Or use manual certificate setup (see manual-setup.md)"
    exit 1
fi

# Method 1: Let's Encrypt (Recommended)
setup_letsencrypt() {
    echo "🔒 Setting up Let's Encrypt certificate..."
    
    # Standalone mode (requires port 80 to be free)
    certbot certonly \
        --standalone \
        --preferred-challenges http \
        -d "$DOMAIN" \
        -d "$DOMAIN_ALT" \
        --email "$EMAIL" \
        --agree-tos \
        --non-interactive \
        --cert-path "$SSL_DIR" \
        --key-path "$SSL_DIR" || {
        echo "❌ Let's Encrypt setup failed"
        echo "Trying DNS challenge method..."
        setup_letsencrypt_dns
    }
    
    # Copy certificates to SSL directory
    if [ -f "/etc/letsencrypt/live/$DOMAIN/fullchain.pem" ]; then
        cp "/etc/letsencrypt/live/$DOMAIN/fullchain.pem" "$SSL_DIR/${DOMAIN}.crt"
        cp "/etc/letsencrypt/live/$DOMAIN/privkey.pem" "$SSL_DIR/${DOMAIN}.key"
        echo "✅ Certificates copied to $SSL_DIR"
    fi
}

# Method 2: DNS Challenge
setup_letsencrypt_dns() {
    echo "🔒 Setting up Let's Encrypt certificate via DNS challenge..."
    
    certbot certonly \
        --manual \
        --preferred-challenges dns \
        -d "$DOMAIN" \
        -d "$DOMAIN_ALT" \
        --email "$EMAIL" \
        --agree-tos \
        --non-interactive || {
        echo "❌ DNS challenge setup failed"
        echo "Please set up certificates manually"
        exit 1
    }
}

# Method 3: Auto-renewal setup
setup_auto_renewal() {
    echo "🔄 Setting up auto-renewal..."
    
    # Create renewal script
    cat > "$SSL_DIR/renew.sh" <<'EOF'
#!/bin/bash
# Auto-renewal script for SSL certificates

certbot renew --quiet --post-hook "docker restart edge_service_prod"

# Copy renewed certificates
cp /etc/letsencrypt/live/www.agroconnectworld.com/fullchain.pem ./ssl/www.agroconnectworld.com.crt
cp /etc/letsencrypt/live/www.agroconnectworld.com/privkey.pem ./ssl/www.agroconnectworld.com.key
EOF
    
    chmod +x "$SSL_DIR/renew.sh"
    
    # Add to crontab (runs twice daily, Let's Encrypt recommends)
    (crontab -l 2>/dev/null; echo "0 0,12 * * * $SSL_DIR/renew.sh") | crontab -
    
    echo "✅ Auto-renewal configured"
}

# Main
case "${1:-letsencrypt}" in
    letsencrypt)
        setup_letsencrypt
        setup_auto_renewal
        ;;
    dns)
        setup_letsencrypt_dns
        setup_auto_renewal
        ;;
    *)
        echo "Usage: $0 {letsencrypt|dns}"
        exit 1
        ;;
esac

echo ""
echo "✅ SSL certificate setup completed!"
echo "Certificate: $SSL_DIR/${DOMAIN}.crt"
echo "Private Key: $SSL_DIR/${DOMAIN}.key"
echo ""
echo "⚠️  IMPORTANT: Keep private keys secure!"
echo "   Never commit private keys to version control."



