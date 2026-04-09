# Alerting Setup Guide

This guide explains how to configure alerting channels for the AgroConnectWorld monitoring stack.

---

## Slack Configuration

### Step 1: Create Slack Webhook

1. Go to https://api.slack.com/apps
2. Create a new app or select existing app
3. Navigate to **Incoming Webhooks**
4. Activate Incoming Webhooks
5. Click **Add New Webhook to Workspace**
6. Select channel: `#alerts-critical` or `#alerts-warning`
7. Copy the webhook URL (format: `https://hooks.slack.com/services/XXXXX/XXXXX/XXXXX`)

### Step 2: Configure Environment Variable

Add to `ops/monitoring/.env`:
```env
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/WEBHOOK/URL
SLACK_CHANNEL_CRITICAL=#alerts-critical
SLACK_CHANNEL_WARNING=#alerts-warning
```

### Step 3: Restart Alertmanager

```bash
cd ops/monitoring
docker compose -f docker-compose.monitoring.yml restart alertmanager
```

### Step 4: Test Alert

```bash
# Send test alert
curl -X POST http://localhost:9093/api/v1/alerts \
  -H "Content-Type: application/json" \
  -d '[{
    "labels": {
      "alertname": "TestAlert",
      "severity": "critical"
    },
    "annotations": {
      "description": "This is a test alert"
    }
  }]'
```

---

## Email Configuration

### Step 1: Gmail Setup

1. **Enable App Password:**
   - Go to Google Account → Security
   - Enable 2-Step Verification
   - Generate App Password for "Mail"
   - Copy the 16-character password

2. **Configure Environment Variables:**

Add to `ops/monitoring/.env`:
```env
SMTP_HOST=smtp.gmail.com:587
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=your-16-char-app-password
ALERT_EMAIL_FROM=alerts@agroconnectworld.com
ALERT_EMAIL_TO=devops@agroconnectworld.com
ONCALL_EMAIL=oncall@agroconnectworld.com
TEAM_EMAIL=team@agroconnectworld.com
```

### Step 2: Outlook/Office 365 Setup

Add to `ops/monitoring/.env`:
```env
SMTP_HOST=smtp-mail.outlook.com:587
SMTP_USER=your-email@outlook.com
SMTP_PASSWORD=your-password
ALERT_EMAIL_FROM=alerts@agroconnectworld.com
ALERT_EMAIL_TO=devops@agroconnectworld.com
```

### Step 3: Custom SMTP Server

Add to `ops/monitoring/.env`:
```env
SMTP_HOST=your-smtp-server.com:587
SMTP_USER=your-username
SMTP_PASSWORD=your-password
SMTP_FROM=alerts@agroconnectworld.com
```

### Step 4: Restart Alertmanager

```bash
cd ops/monitoring
docker compose -f docker-compose.monitoring.yml restart alertmanager
```

### Step 5: Test Email

```bash
# Send test alert
curl -X POST http://localhost:9093/api/v1/alerts \
  -H "Content-Type: application/json" \
  -d '[{
    "labels": {
      "alertname": "TestEmailAlert",
      "severity": "warning"
    },
    "annotations": {
      "description": "Testing email alert delivery"
    }
  }]'
```

Check your email inbox for the alert.

---

## PagerDuty Integration (Optional)

### Step 1: Create PagerDuty Service

1. Go to https://www.pagerduty.com
2. Create a new service
3. Add **Prometheus** integration
4. Copy the integration key

### Step 2: Configure Alertmanager

Add to `ops/monitoring/alertmanager/alertmanager.yml`:

```yaml
receivers:
  - name: 'on-call'
    pagerduty_configs:
      - service_key: '${PAGERDUTY_SERVICE_KEY}'
        description: '{{ .GroupLabels.alertname }}'
```

### Step 3: Set Environment Variable

Add to `ops/monitoring/.env`:
```env
PAGERDUTY_SERVICE_KEY=your-pagerduty-integration-key
```

---

## Verification

### Check Alertmanager Configuration

```bash
# View current configuration
curl http://localhost:9093/api/v1/status/config
```

### Test All Channels

1. **Test Slack:**
   ```bash
   # Send critical alert (should go to Slack)
   curl -X POST http://localhost:9093/api/v1/alerts \
     -H "Content-Type: application/json" \
     -d '[{"labels":{"alertname":"TestSlack","severity":"critical"}}]'
   ```

2. **Test Email:**
   ```bash
   # Send warning alert (should send email)
   curl -X POST http://localhost:9093/api/v1/alerts \
     -H "Content-Type: application/json" \
     -d '[{"labels":{"alertname":"TestEmail","severity":"warning"}}]'
   ```

### View Alert History

```bash
# View active alerts
curl http://localhost:9093/api/v1/alerts

# View silenced alerts
curl http://localhost:9093/api/v1/silences
```

---

## Troubleshooting

### Slack Not Receiving Alerts

1. Verify webhook URL is correct
2. Check Slack channel exists
3. Verify Alertmanager logs: `docker logs alertmanager`
4. Test webhook manually:
   ```bash
   curl -X POST $SLACK_WEBHOOK_URL \
     -H "Content-Type: application/json" \
     -d '{"text":"Test message"}'
   ```

### Email Not Sending

1. Verify SMTP credentials
2. Check firewall allows port 587
3. For Gmail: Ensure App Password is used (not regular password)
4. Check Alertmanager logs: `docker logs alertmanager`
5. Test SMTP connection:
   ```bash
   telnet smtp.gmail.com 587
   ```

### Alerts Not Firing

1. Check Prometheus alert rules: `curl http://localhost:9090/api/v1/rules`
2. Verify alert conditions are met
3. Check Prometheus targets: `curl http://localhost:9090/api/v1/targets`
4. View Prometheus logs: `docker logs prometheus`

---

## Best Practices

1. **Separate Channels:**
   - Critical alerts → On-call (immediate response)
   - Warning alerts → Team channel (monitoring)

2. **Escalation:**
   - Set up escalation policies in PagerDuty
   - Configure email escalation for critical alerts

3. **Alert Fatigue:**
   - Tune alert thresholds
   - Use inhibition rules to suppress duplicate alerts
   - Set appropriate `repeat_interval`

4. **Testing:**
   - Test alerts monthly
   - Verify all channels work
   - Document on-call procedures

---

**Last Updated:** 2025-11-28



