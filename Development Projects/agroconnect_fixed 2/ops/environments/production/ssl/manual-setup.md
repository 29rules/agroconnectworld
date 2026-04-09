# Manual SSL Certificate Setup for Production

## Option 1: Let's Encrypt (Recommended)

### Prerequisites
- Domain `www.agroconnectworld.com` points to your server
- Domain `agroconnectworld.com` points to your server (for redirect)
- Port 80 is accessible from the internet
- Certbot installed

### Steps

1. **Install Certbot:**
   ```bash
   # macOS
   brew install certbot
   
   # Ubuntu/Debian
   sudo apt-get update
   sudo apt-get install certbot
   ```

2. **Obtain Certificate (covers both www and non-www):**
   ```bash
   sudo certbot certonly --standalone \
     -d www.agroconnectworld.com \
     -d agroconnectworld.com
   ```

3. **Copy Certificates:**
   ```bash
   mkdir -p ops/environments/production/ssl
   sudo cp /etc/letsencrypt/live/www.agroconnectworld.com/fullchain.pem \
          ops/environments/production/ssl/www.agroconnectworld.com.crt
   sudo cp /etc/letsencrypt/live/www.agroconnectworld.com/privkey.pem \
          ops/environments/production/ssl/www.agroconnectworld.com.key
   sudo chown $USER:$USER ops/environments/production/ssl/*.key
   ```

4. **Set Up Auto-Renewal:**
   ```bash
   # Test renewal
   sudo certbot renew --dry-run
   
   # Add to crontab (runs twice daily)
   (crontab -l 2>/dev/null; echo "0 0,12 * * * certbot renew --quiet --post-hook 'docker restart edge_service_prod'") | crontab -
   ```

## Option 2: Commercial Certificate (Recommended for Enterprise)

1. **Purchase Certificate:**
   - Recommended CAs: DigiCert, GlobalSign, Sectigo
   - Get wildcard certificate if needed: `*.agroconnectworld.com`

2. **Generate CSR:**
   ```bash
   openssl req -new -newkey rsa:2048 -nodes \
     -keyout www.agroconnectworld.com.key \
     -out www.agroconnectworld.com.csr \
     -subj "/C=US/ST=State/L=City/O=AgroConnectWorld/CN=www.agroconnectworld.com"
   ```

3. **Submit CSR to CA and receive certificate**

4. **Place in ssl/ directory:**
   ```bash
   cp certificate.crt ops/environments/production/ssl/www.agroconnectworld.com.crt
   cp www.agroconnectworld.com.key ops/environments/production/ssl/
   ```

## Verification

```bash
# Test certificate
openssl x509 -in www.agroconnectworld.com.crt -text -noout

# Test SSL connection
openssl s_client -connect www.agroconnectworld.com:443 -servername www.agroconnectworld.com

# Check certificate expiration
openssl x509 -in www.agroconnectworld.com.crt -noout -dates
```

## Security Best Practices

1. **Private Key Security:**
   - Set permissions: `chmod 600 *.key`
   - Never commit to version control
   - Store in secure key management system

2. **Certificate Renewal:**
   - Set up auto-renewal
   - Monitor expiration dates
   - Test renewal process quarterly

3. **Certificate Chain:**
   - Include intermediate certificates
   - Verify full chain: `openssl verify -CAfile chain.crt certificate.crt`



