#!/bin/sh
# Automated Backup Script for Production Database
# Runs daily via cron

set -e

BACKUP_DIR="/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

echo "=== Starting Database Backup ==="
echo "Timestamp: $TIMESTAMP"
echo ""

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

# Backup all schemas
for schema in auth_service product_service supplier_service quote_service order_service contact_service; do
    echo "Backing up schema: $schema"
    pg_dump -h "$PGHOST" -U "$PGUSER" -d "$PGDATABASE" \
        -n "$schema" \
        -F c \
        -f "$BACKUP_DIR/${schema}_${TIMESTAMP}.dump" || {
        echo "❌ Backup failed for schema: $schema"
        exit 1
    }
done

# Full database backup
echo "Creating full database backup..."
pg_dumpall -h "$PGHOST" -U "$PGUSER" \
    -f "$BACKUP_DIR/full_backup_${TIMESTAMP}.sql" || {
    echo "❌ Full backup failed"
    exit 1
}

# Compress backups
echo "Compressing backups..."
gzip "$BACKUP_DIR"/*.dump "$BACKUP_DIR"/*.sql 2>/dev/null || true

# Cleanup old backups (keep last 30 days)
echo "Cleaning up old backups (keeping last $RETENTION_DAYS days)..."
find "$BACKUP_DIR" -name "*.dump.gz" -mtime +$RETENTION_DAYS -delete
find "$BACKUP_DIR" -name "*.sql.gz" -mtime +$RETENTION_DAYS -delete

echo "✅ Backup completed successfully"
echo "Backup location: $BACKUP_DIR"



