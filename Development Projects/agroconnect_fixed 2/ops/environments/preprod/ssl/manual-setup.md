# Manual SSL Certificate Setup for Staging

## Option 1: Let's Encrypt (Recommended)

### Prerequisites
- Domain `staging.agroconnectworld.com` points to your server
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

2. **Obtain Certificate:**
   ```bash
   sudo certbot certonly --standalone -d staging.agroconnectworld.com
   ```

3. **Copy Certificates:**
   ```bash
   mkdir -p ops/environments/staging/ssl
   sudo cp /etc/letsencrypt/live/staging.agroconnectworld.com/fullchain.pem \
          ops/environments/staging/ssl/staging.agroconnectworld.com.crt
   sudo cp /etc/letsencrypt/live/staging.agroconnectworld.com/privkey.pem \
          ops/environments/staging/ssl/staging.agroconnectworld.com.key
   sudo chown $USER:$USER ops/environments/staging/ssl/*.key
   ```

4. **Set Up Auto-Renewal:**
   ```bash
   sudo certbot renew --dry-run
   ```

## Option 2: Self-Signed (Testing Only)

```bash
cd ops/environments/staging/ssl
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout staging.agroconnectworld.com.key \
  -out staging.agroconnectworld.com.crt \
  -subj "/C=US/ST=State/L=City/O=AgroConnectWorld/CN=staging.agroconnectworld.com" \
  -addext "subjectAltName=DNS:staging.agroconnectworld.com"
```

⚠️ **Warning:** Self-signed certificates will show browser warnings.

## Option 3: Commercial Certificate

1. Purchase certificate from CA (DigiCert, GlobalSign, etc.)
2. Generate CSR:
   ```bash
   openssl req -new -newkey rsa:2048 -nodes \
     -keyout staging.agroconnectworld.com.key \
     -out staging.agroconnectworld.com.csr
   ```
3. Submit CSR to CA
4. Receive certificate and place in `ssl/` directory

## Verification

```bash
# Test certificate
openssl x509 -in staging.agroconnectworld.com.crt -text -noout

# Test SSL connection
openssl s_client -connect staging.agroconnectworld.com:443
```

