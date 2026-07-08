#!/bin/bash
set -e

echo "🚀 Starting Containerized VPS Provisioning..."

# 1. CONFIGURE SWAP SPACE
if [ ! -f /swapfile ]; then
    echo "Creating 2GB Swap Insurance File..."
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
    sudo sysctl vm.swappiness=10
    echo 'vm.swappiness=10' | sudo tee -a /etc/sysctl.conf
fi

# 2. INSTALL HOST DEPENDENCIES
sudo apt update
sudo apt install -y docker.io postgresql rclone gzip
sudo systemctl enable --now docker

# 3. TUNE POSTGRESQL FOR 4GB RAM INSTANCE
PG_CONF=$(find /etc/postgresql/ -name postgresql.conf | head -n 1)
PG_DIR=$(dirname "$PG_CONF")

cat << EOF | sudo tee "$PG_DIR/leaderboard.conf"
# Dedicated resource allocation for your local database
max_connections = 40
shared_buffers = 1GB
effective_cache_size = 3GB
work_mem = 16MB
maintenance_work_mem = 256MB
EOF

if ! grep -q "include = 'leaderboard.conf'" "$PG_CONF"; then
    echo "include = 'leaderboard.conf'" | sudo tee -a "$PG_CONF"
    sudo systemctl restart postgresql
fi

# 4. CONFIGURE RCLONE CREDENTIALS FOR CLOUDFLARE R2
sudo mkdir -p /etc/rclone
cat << EOF | sudo tee /etc/rclone.conf
[r2]
type = s3
provider = Cloudflare
access_key_id = ${R2_ACCESS_KEY}
secret_access_key = ${R2_SECRET_KEY}
endpoint = https://${R2_ACCOUNT_ID}.r2.cloudflarestorage.com
region = auto
acl = private
EOF
sudo chown postgres:postgres /etc/rclone.conf
sudo chmod 600 /etc/rclone.conf

# 5. GENERATE THE nightly BACKUP SCRIPT & CRON
sudo mkdir -p /var/lib/postgresql/backups
sudo chown postgres:postgres /var/lib/postgresql/backups

cat << 'EOF' | sudo tee /usr/local/bin/db_backup.sh
#!/bin/bash
BACKUP_DIR="/var/lib/postgresql/backups"
DB_NAME="leaderboard" # Update this to your actual database name
FILENAME="$BACKUP_DIR/${DB_NAME}_$(date +%Y-%m-%d_%H%M%S).sql.gz"

# Dump and compress locally
pg_dump "$DB_NAME" | gzip > "$FILENAME"

# Securely copy up to Cloudflare R2 (Additive only - safe from server failures)
rclone --config /etc/rclone.conf copy "$FILENAME" "r2:my-leaderboard-backups/"

# Clean up local files older than 7 days to preserve VPS disk space
find "$BACKUP_DIR" -type f -name "*.sql.gz" -mtime +7 -delete
EOF
sudo chmod +x /usr/local/bin/db_backup.sh

# Register the cron job to run at 3:00 AM under the postgres system user
if ! sudo crontab -u postgres -l 2>/dev/null | grep -q "db_backup.sh"; then
    (sudo crontab -u postgres -l 2>/dev/null; echo "0 3 * * * /usr/local/bin/db_backup.sh") | sudo crontab -u postgres -
fi

# 6. GENERATE SYSTEMD SERVICE WRAPPER FOR DOCKER
# --network="host" lets the container talk to local Postgres on localhost:5432 out of the box
cat << EOF | sudo tee /etc/systemd/system/leaderboard.service
[Unit]
Description=Scala Leaderboard Container
After=docker.service postgresql.service
Requires=docker.service

[Service]
TimeoutStartSec=0
Restart=always
ExecStartPre=-/usr/bin/docker stop leaderboard
ExecStartPre=-/usr/bin/docker rm leaderboard
ExecStart=/usr/bin/docker run --name leaderboard --network="host" ghcr.io/${IMAGE_NAME}:latest

[Install]
WantedBy=multi-user-target
EOF

sudo systemctl daemon-reload
sudo systemctl enable leaderboard.service

echo "✅ Provisioning Complete!"