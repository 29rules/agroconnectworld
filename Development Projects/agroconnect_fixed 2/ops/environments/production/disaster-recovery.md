# Disaster Recovery Plan

**Last Updated:** 2025-11-28  
**Environment:** Production

---

## Recovery Objectives

- **RTO (Recovery Time Objective):** 4 hours
- **RPO (Recovery Point Objective):** 1 hour (last backup)

---

## Backup Strategy

### Database Backups
- **Frequency:** Daily at 2 AM UTC
- **Retention:** 30 days
- **Location:** `/backups` volume
- **Format:** Compressed SQL dumps per schema + full database dump

### Application Backups
- **Docker Images:** Tagged and stored in registry
- **Configuration:** Version controlled in Git
- **Volumes:** Backed up daily

---

## Recovery Procedures

### 1. Database Recovery

```bash
# Restore specific schema
pg_restore -h postgres -U $PGUSER -d agro_master \
  -n auth_service \
  /backups/auth_service_YYYYMMDD_HHMMSS.dump.gz

# Restore full database
gunzip /backups/full_backup_YYYYMMDD_HHMMSS.sql.gz
psql -h postgres -U $PGUSER -d agro_master < /backups/full_backup_YYYYMMDD_HHMMSS.sql
```

### 2. Service Recovery

```bash
# Stop all services
cd ops/environments/production
docker compose -f docker-compose.prod.yml down

# Restore from backup
# (Restore database, volumes, etc.)

# Start services
docker compose -f docker-compose.prod.yml up -d

# Verify health
./health-check.sh production all
```

### 3. Complete System Recovery

1. Provision new infrastructure
2. Restore database from latest backup
3. Deploy application from Git
4. Restore configuration files
5. Start all services
6. Verify functionality
7. Switch DNS to new infrastructure

---

## Contact Information

- **On-Call Engineer:** ${ONCALL_EMAIL}
- **DevOps Team:** devops@agroconnectworld.com
- **Escalation:** CTO

---

## Testing

- **Frequency:** Quarterly
- **Last Tested:** TBD
- **Next Test:** TBD



