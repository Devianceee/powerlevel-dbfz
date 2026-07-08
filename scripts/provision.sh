#!/bin/bash
set -e

echo "🚀 Starting Containerized Compose Provisioning..."

# 1. CONFIGURE SWAP SPACE
if [ ! -f /swapfile ]; then
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
    sudo sysctl vm.swappiness=10
    echo 'vm.swappiness=10' | sudo tee -a /etc/sysctl.conf
fi

# 2. INSTALL HOST DEPENDENCIES & CADDY
sudo apt update
sudo apt install -y docker.io docker-compose-v2 rclone gzip debian-keyring debian-archive-keyring apt-transport-https curl

if [ ! -f /etc/apt/sources.list.d/caddy-stable.list ]; then
    curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.gpg' | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
    curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.list' | sudo tee /etc/apt/sources.list.d/caddy-stable.list
    sudo apt update
fi
sudo apt install -y caddy
sudo systemctl enable --now docker

# 3. CONFIGURE CADDY REVERSE PROXY
cat << EOF | sudo tee /etc/caddy/Caddyfile
powerlevel.info, www.powerlevel.info {
    reverse_proxy localhost:8080
}
EOF
sudo systemctl restart caddy

# 4. CONFIGURE RCLONE CREDENTIALS FOR CLOUDFLARE R2
sudo mkdir -p /etc/rclone
cat << EOF | sudo tee /etc/rclone.conf
[r2]
type = s3
provider = Cloudflare
access_key_id = ${R2_ACCESS_KEY_ID}
secret_access_key = ${R2_SECRET_KEY}
endpoint = https://${R2_ACCOUNT_ID}.r2.cloudflarestorage.com
region = auto
acl = private
EOF
sudo chmod 600 /etc/rclone.conf

# 5. WRITE SYSTEM ENVIRONMENT VARIABLES FOR DOCKER COMPOSE
sudo mkdir -p /opt/powerlevel
cat << EOF | sudo tee /opt/powerlevel/.env
IMAGE_NAME=${IMAGE_NAME}
DBFZ_STEAM_ID=${DBFZ_STEAM_ID}
EOF
sudo chmod 600 /opt/powerlevel/.env

# 6. GENERATE CONTAINERIZED DATABASE BACKUP SCRIPT
sudo mkdir -p /opt/powerlevel/backups
cat << 'EOF' | sudo tee /usr/local/bin/db_backup.sh
#!/bin/bash
BACKUP_DIR="/opt/powerlevel/backups"
FILENAME="$BACKUP_DIR/powerlevel_$(date +%Y-%m-%d_%H%M%S).sql.gz"

# Execute pg_dump INSIDE the running docker container
docker exec -t powerlevel-postgres pg_dump -U powerlevel powerlevel | gzip > "$FILENAME"

# Ship offsite to Cloudflare R2
rclone --config /etc/rclone.conf copy "$FILENAME" "r2:powerlevel-db-backups/"
find "$BACKUP_DIR" -type f -name "*.sql.gz" -mtime +7 -delete
EOF
sudo chmod +x /usr/local/bin/db_backup.sh

# Setup cron on host system
if ! sudo crontab -l 2>/dev/null | grep -q "db_backup.sh"; then
    (sudo crontab -l 2>/dev/null; echo "0 3 * * * /usr/local/bin/db_backup.sh") | sudo crontab -
fi

# 7. GENERATE SYSTEMD SERVICE WRAPPER FOR DOCKER COMPOSE
cat << EOF | sudo tee /etc/systemd/system/powerlevel.service
[Unit]
Description=Powerlevel Docker Compose Application
After=docker.service
Requires=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/opt/powerlevel
ExecStart=/usr/bin/docker compose up -d --remove-orphans
ExecStop=/usr/bin/docker compose down
ExecReload=/usr/bin/docker compose pull && /usr/bin/docker compose up -d

[Install]
WantedBy=multi-user-target
EOF

sudo systemctl daemon-reload
sudo systemctl enable powerlevel.service

echo "✅ Provisioning Complete!"