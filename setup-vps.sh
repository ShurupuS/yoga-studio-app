#!/bin/bash

# SAFE VPS Setup Script for GitHub Actions Deployment
# This script is designed to NOT interfere with existing Docker services

set -e

echo "🚀 Setting up VPS for GitHub Actions deployment (SAFE MODE)..."

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

# Check existing Docker services
print_status "Checking existing Docker services..."
EXISTING_CONTAINERS=$(docker ps --format "{{.Names}}" | wc -l)
print_status "Found $EXISTING_CONTAINERS existing containers"

if [ $EXISTING_CONTAINERS -gt 0 ]; then
    print_warning "Existing Docker containers detected:"
    docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Ports}}"
    print_warning "This script will NOT affect your existing containers!"
fi

# Check port conflicts
print_status "Checking for port conflicts..."
CONFLICT_PORTS=()
for port in 18080 15432 16379; do
    if netstat -tulpn | grep ":$port " > /dev/null; then
        CONFLICT_PORTS+=($port)
    fi
done

if [ ${#CONFLICT_PORTS[@]} -gt 0 ]; then
    print_error "Port conflicts detected: ${CONFLICT_PORTS[*]}"
    print_error "Please free these ports or modify the script"
    exit 1
fi

# Update system (safe)
print_status "Updating system packages (safe mode)..."
apt update

# Install required packages (only if not present)
print_status "Installing required packages..."
for package in git curl wget unzip; do
    if ! dpkg -l | grep -q "^ii  $package "; then
        apt install -y $package
    else
        print_status "$package already installed"
    fi
done

# Install Docker if not present
if ! command -v docker &> /dev/null; then
    print_status "Installing Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sh get-docker.sh
    rm get-docker.sh
else
    print_status "Docker already installed"
fi

# Install Docker Compose if not present
if ! command -v docker-compose &> /dev/null; then
    print_status "Installing Docker Compose..."
    curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
else
    print_status "Docker Compose already installed"
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
else
    print_status ".env file already exists"
fi

# Create backup directory
print_status "Creating backup directory..."
mkdir -p /opt/yoga-studio/backups

# Set up SSH key for GitHub Actions (only if not exists)
print_status "Setting up SSH for GitHub Actions..."

# Create github-actions user if it doesn't exist
if ! id "github-actions" &>/dev/null; then
    print_status "Creating github-actions user..."
    useradd -m -s /bin/bash github-actions
    usermod -aG docker github-actions
    print_status "User github-actions created and added to docker group"
fi

if [ ! -d "/home/github-actions/.ssh" ]; then
    mkdir -p /home/github-actions/.ssh
    chown -R github-actions:github-actions /home/github-actions/.ssh
    chmod 700 /home/github-actions/.ssh
    print_status "SSH directory created"
else
    print_status "SSH directory already exists"
fi

print_warning "IMPORTANT: You need to add your SSH public key to GitHub Secrets!"
print_warning "1. Generate SSH key pair: sudo -u github-actions ssh-keygen -t rsa -b 4096 -C 'github-actions' -f /home/github-actions/.ssh/id_rsa"
print_warning "2. Add private key to GitHub Secrets as VPS_SSH_KEY: sudo cat /home/github-actions/.ssh/id_rsa"
print_warning "3. Add public key to authorized_keys: sudo cat /home/github-actions/.ssh/id_rsa.pub >> /home/github-actions/.ssh/authorized_keys"

# Create systemd service for auto-restart (only if not exists)
if [ ! -f "/etc/systemd/system/yoga-studio.service" ]; then
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
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
EOF

    # Enable service
    systemctl daemon-reload
    systemctl enable yoga-studio.service
    print_status "Systemd service created and enabled"
else
    print_status "Systemd service already exists"
fi

# Set up log rotation (only if not exists)
if [ ! -f "/etc/logrotate.d/yoga-studio" ]; then
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
    print_status "Log rotation configured"
else
    print_status "Log rotation already configured"
fi

# Create monitoring script (only if not exists)
if [ ! -f "/opt/yoga-studio/monitor.sh" ]; then
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
    docker-compose restart app
fi
EOF

    chmod +x /opt/yoga-studio/monitor.sh
    print_status "Monitoring script created"
else
    print_status "Monitoring script already exists"
fi

# Set up cron job for monitoring (only if not exists)
if ! crontab -l | grep -q "monitor.sh"; then
    print_status "Setting up monitoring cron job..."
    (crontab -l 2>/dev/null; echo "*/5 * * * * /opt/yoga-studio/monitor.sh") | crontab -
    print_status "Cron job added"
else
    print_status "Cron job already exists"
fi

# Show final status
print_status "✅ VPS setup completed safely!"
print_status "📊 Summary:"
print_status "- Project directory: /opt/yoga-studio"
print_status "- App port: 18080"
print_status "- PostgreSQL port: 15432"
print_status "- Redis port: 16379"
print_status "- Existing containers: $EXISTING_CONTAINERS (untouched)"

print_status "Next steps:"
print_status "1. Edit .env file: nano /opt/yoga-studio/.env"
print_status "2. Add SSH key to GitHub Secrets"
print_status "3. Test deployment by pushing to main branch"
print_status "4. Monitor logs: tail -f /opt/yoga-studio/logs/health.log"

print_warning "Your existing Docker services are safe and will continue running!"
