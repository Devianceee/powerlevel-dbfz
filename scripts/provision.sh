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

# 2. INSTALL HOST DEPENDENCIES
sudo apt update
sudo apt install -y \
    docker.io \
    docker-compose-v2 \
    rclone \
    gzip \
    curl \
    gnupg \
    debian-keyring \
    debian-archive-keyring \
    apt-transport-https \
    ufw


# 3. INSTALL CADDY
if ! command -v caddy >/dev/null 2>&1; then

    echo "Installing Caddy..."

    curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' \
        | sudo gpg --dearmor --batch --yes \
        -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg


    curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' \
        | sudo tee /etc/apt/sources.list.d/caddy-stable.list


    sudo apt update

    sudo apt install -y caddy

fi

echo "Caddy version:"
caddy version

# 4. ENABLE SERVICES
sudo systemctl enable --now docker
sudo systemctl enable --now caddy


# 5. FIREWALL
sudo ufw allow OpenSSH
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw --force enable


# 6. CONFIGURE CADDY
echo "Configuring Caddy..."
cat <<EOF | sudo tee /etc/caddy/Caddyfile
powerlevel.info, www.powerlevel.info {
    reverse_proxy localhost:8080
}
EOF

sudo caddy validate --config /etc/caddy/Caddyfile

sudo systemctl restart caddy


# 7. CONFIGURE RCLONE R2
sudo mkdir -p /etc/rclone

cat <<EOF | sudo tee /etc/rclone.conf
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


# 8. WRITE DOCKER COMPOSE ENV
sudo mkdir -p /opt/powerlevel

cat <<EOF | sudo tee /opt/powerlevel/.env
IMAGE_NAME=${IMAGE_NAME}
DBFZ_STEAM_ID=${DBFZ_STEAM_ID}
EOF

sudo chmod 600 /opt/powerlevel/.env


# 9. DATABASE BACKUP SCRIPT
sudo mkdir -p /opt/powerlevel/backups
cat <<'EOF' | sudo tee /usr/local/bin/db_backup.sh
#!/bin/bash

BACKUP_DIR="/opt/powerlevel/backups"
FILENAME="$BACKUP_DIR/powerlevel_$(date +%Y-%m-%d_%H%M%S).sql.gz"

docker exec -t powerlevel-postgres \
    pg_dump -U powerlevel powerlevel \
    | gzip > "$FILENAME"


rclone \
    --config /etc/rclone.conf \
    copy "$FILENAME" "r2:powerlevel-db-backups/"

find "$BACKUP_DIR" \
    -type f \
    -name "*.sql.gz" \
    -mtime +7 \
    -delete
EOF

sudo chmod +x /usr/local/bin/db_backup.sh


if ! sudo crontab -l 2>/dev/null | grep -q "db_backup.sh"; then

    (
        sudo crontab -l 2>/dev/null
        echo "0 3 * * * /usr/local/bin/db_backup.sh"
    ) | sudo crontab -
fi


# 10. SYSTEMD DOCKER COMPOSE SERVICE
cat <<EOF | sudo tee /etc/systemd/system/powerlevel.service
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
ExecReload=/bin/sh -c '/usr/bin/docker compose pull && /usr/bin/docker compose up -d'

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable powerlevel.service

echo "✅ Provisioning Complete!"