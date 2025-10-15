#!/bin/bash

# Lightweight Yoga Studio App Deployment Script
# For low-resource servers

set -e

echo "🚀 Starting lightweight Yoga Studio App deployment..."

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

# Check system resources
print_status "Checking system resources..."
TOTAL_MEM=$(free -m | awk 'NR==2{printf "%.0f", $2}')
USED_MEM=$(free -m | awk 'NR==2{printf "%.0f", $3}')
FREE_MEM=$((TOTAL_MEM - USED_MEM))

print_status "Total memory: ${TOTAL_MEM}MB, Used: ${USED_MEM}MB, Free: ${FREE_MEM}MB"

if [ $FREE_MEM -lt 300 ]; then
    print_warning "Low memory available (${FREE_MEM}MB). Consider upgrading your server."
    print_warning "This deployment will use memory limits to prevent OOM."
fi

# Check disk space
DISK_USAGE=$(df / | awk 'NR==2 {print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 85 ]; then
    print_warning "Disk usage is high (${DISK_USAGE}%). Cleaning up..."
    docker system prune -f
fi

# Check if .env file exists
if [ ! -f ".env" ]; then
    print_warning ".env file not found. Creating from example..."
    cp env.example .env
    print_warning "Please edit .env file with your secure passwords before continuing!"
    print_warning "Run: nano .env"
    exit 1
fi

# Check if ports are available
print_status "Checking port availability..."
for port in 18080 15432 16379; do
    if netstat -tulpn | grep ":$port " > /dev/null; then
        print_error "Port $port is already in use!"
        exit 1
    fi
done

# Pull latest changes
print_status "Pulling latest changes from repository..."
git pull origin main

# Build lightweight image
print_status "Building lightweight application image..."
docker build -f backend/Dockerfile.lightweight -t yoga-app:lightweight ./backend

# Start services with memory limits
print_status "Starting services with memory limits..."
docker-compose -f docker-compose.lightweight.yml up -d

# Wait for services to be healthy
print_status "Waiting for services to be healthy (this may take a while on low-resource servers)..."
sleep 60

# Check service status
print_status "Checking service status..."
docker-compose -f docker-compose.lightweight.yml ps

# Check memory usage
print_status "Current memory usage:"
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}"

# Check if app is responding
print_status "Testing application health..."
if curl -f http://localhost:18080/actuator/health > /dev/null 2>&1; then
    print_status "✅ Application is running successfully!"
    print_status "🌐 Access your app at: http://$(curl -s ifconfig.me):18080"
    print_status "📊 Health check: http://$(curl -s ifconfig.me):18080/actuator/health"
    print_status "💾 Memory usage: $(docker stats --no-stream --format '{{.MemUsage}}' yoga_app)"
else
    print_error "❌ Application health check failed!"
    print_status "Checking logs..."
    docker-compose -f docker-compose.lightweight.yml logs app
    print_status "Checking memory usage..."
    docker stats --no-stream
fi

print_status "Deployment completed!"
print_status "To view logs: docker-compose -f docker-compose.lightweight.yml logs -f"
print_status "To stop services: docker-compose -f docker-compose.lightweight.yml down"
