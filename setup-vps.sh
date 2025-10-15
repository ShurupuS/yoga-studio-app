#!/bin/bash

# VPS Setup Script for GitHub Actions Deployment
# Run this script on your VPS to prepare it for automated deployment

set -e

echo "🚀 Setting up VPS for GitHub Actions deployment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if running as root
if [ "$EUID" -ne 0 ]; then
    print_error "Please run as root or with sudo"
    exit 1
fi

# Update system
print_status "Updating system packages..."
apt update && apt upgrade -y

# Install required packages
print_status "Installing required packages..."
apt install -y git curl wget unzip

# Install Docker if not present
if ! command -v docker &> /dev/null; then
    print_status "Installing Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sh get-docker.sh
    rm get-docker.sh
fi

# Install Docker Compose if not present
if ! command -v docker-compose &> /dev/null; then
    print_status "Installing Docker Compose..."
    curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
fi

# Create project directory
print_status "Creating project directory..."
mkdir -p /opt/yoga-studio
cd /opt/yoga-studio

# Clone repository
print_status "Cloning repository..."
if [ ! -d ".git" ]; then
    git clone https://github.com/ShurupuS/yoga-studio-app.git .
else
    print_status "Repository already exists, pulling latest changes..."
    git pull origin main
fi

# Create environment file
print_status "Creating environment file..."
if [ ! -f ".env" ]; then
    cp env.example .env
    print_warning "Please edit .env file with your secure passwords!"
    print_warning "Run: nano .env"
fi

# Create backup directory
print_status "Creating backup directory..."
mkdir -p /opt/yoga-studio/backups

# Set up SSH key for GitHub Actions
print_status "Setting up SSH for GitHub Actions..."
mkdir -p /home/github-actions/.ssh
chown -R github-actions:github-actions /home/github-actions/.ssh
chmod 700 /home/github-actions/.ssh

print_warning "IMPORTANT: You need to add your SSH public key to GitHub Secrets!"
print_warning "1. Generate SSH key pair: ssh-keygen -t rsa -b 4096 -C 'github-actions'"
print_warning "2. Add private key to GitHub Secrets as VPS_SSH_KEY"
print_warning "3. Add public key to authorized_keys on VPS"

# Create systemd service for auto-restart
print_status "Creating systemd service..."
cat > /etc/systemd/system/yoga-studio.service << EOF
[Unit]
Description=Yoga Studio App
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/opt/yoga-studio
ExecStart=/usr/local/bin/docker-compose -f docker-compose.lightweight.yml up -d
ExecStop=/usr/local/bin/docker-compose -f docker-compose.lightweight.yml down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
EOF

# Enable service
systemctl daemon-reload
systemctl enable yoga-studio.service

# Set up log rotation
print_status "Setting up log rotation..."
cat > /etc/logrotate.d/yoga-studio << EOF
/opt/yoga-studio/logs/*.log {
    daily
    missingok
    rotate 7
    compress
    delaycompress
    notifempty
    create 644 root root
}
EOF

# Create monitoring script
print_status "Creating monitoring script..."
cat > /opt/yoga-studio/monitor.sh << 'EOF'
#!/bin/bash
# Simple health check script

APP_URL="http://localhost:18080/actuator/health"
LOG_FILE="/opt/yoga-studio/logs/health.log"

# Create logs directory if it doesn't exist
mkdir -p /opt/yoga-studio/logs

# Check if app is responding
if curl -f "$APP_URL" > /dev/null 2>&1; then
    echo "$(date): App is healthy" >> "$LOG_FILE"
else
    echo "$(date): App is down, restarting..." >> "$LOG_FILE"
    cd /opt/yoga-studio
    docker-compose -f docker-compose.lightweight.yml restart app
fi
EOF

chmod +x /opt/yoga-studio/monitor.sh

# Set up cron job for monitoring
print_status "Setting up monitoring cron job..."
echo "*/5 * * * * /opt/yoga-studio/monitor.sh" | crontab -

# Clean up
print_status "Cleaning up..."
docker system prune -f

print_status "✅ VPS setup completed!"
print_status "Next steps:"
print_status "1. Edit .env file: nano /opt/yoga-studio/.env"
print_status "2. Add SSH key to GitHub Secrets"
print_status "3. Test deployment by pushing to main branch"
print_status "4. Monitor logs: tail -f /opt/yoga-studio/logs/health.log"
