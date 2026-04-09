#!/bin/bash
# SSL Certificate Setup Script for Staging
# This script helps set up SSL certificates using Let's Encrypt

set -e

DOMAIN="staging.agroconnectworld.com"
EMAIL="${SSL_EMAIL:-admin@agroconnectworld.com}"
SSL_DIR="./ssl"

echo "=== SSL Certificate Setup for Staging ==="
echo "Domain: $DOMAIN"
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

# Method 1: Let's Encrypt (Recommended for production)
setup_letsencrypt() {
    echo "🔒 Setting up Let's Encrypt certificate..."
    
    # Standalone mode (requires port 80 to be free)
    certbot certonly \
        --standalone \
        --preferred-challenges http \
        -d "$DOMAIN" \
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

# Method 2: DNS Challenge (for servers behind firewall)
setup_letsencrypt_dns() {
    echo "🔒 Setting up Let's Encrypt certificate via DNS challenge..."
    
    certbot certonly \
        --manual \
        --preferred-challenges dns \
        -d "$DOMAIN" \
        --email "$EMAIL" \
        --agree-tos \
        --non-interactive || {
        echo "❌ DNS challenge setup failed"
        echo "Please set up certificates manually (see manual-setup.md)"
        exit 1
    }
}

# Method 3: Self-signed certificate (for testing)
setup_self_signed() {
    echo "🔒 Generating self-signed certificate for testing..."
    
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
        -keyout "$SSL_DIR/${DOMAIN}.key" \
        -out "$SSL_DIR/${DOMAIN}.crt" \
        -subj "/C=US/ST=State/L=City/O=AgroConnectWorld/CN=$DOMAIN" \
        -addext "subjectAltName=DNS:$DOMAIN" || {
        echo "❌ Self-signed certificate generation failed"
        exit 1
    }
    
    echo "✅ Self-signed certificate generated"
    echo "⚠️  WARNING: Self-signed certificates are for testing only!"
    echo "   Browsers will show security warnings."
}

# Main
case "${1:-letsencrypt}" in
    letsencrypt)
        setup_letsencrypt
        ;;
    dns)
        setup_letsencrypt_dns
        ;;
    self-signed)
        setup_self_signed
        ;;
    *)
        echo "Usage: $0 {letsencrypt|dns|self-signed}"
        exit 1
        ;;
esac

echo ""
echo "✅ SSL certificate setup completed!"
echo "Certificate: $SSL_DIR/${DOMAIN}.crt"
echo "Private Key: $SSL_DIR/${DOMAIN}.key"
echo ""
echo "Next steps:"
echo "1. Update nginx config to use these certificates"
echo "2. Restart nginx container"
echo "3. Test SSL: openssl s_client -connect $DOMAIN:443"



