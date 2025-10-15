#!/bin/bash

# Yoga Studio App Deployment Script
# For existing server with Docker services

set -e

echo "🚀 Starting Yoga Studio App deployment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
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

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check existing services
print_status "Checking existing Docker services..."
EXISTING_CONTAINERS=$(docker ps --format "table {{.Names}}\t{{.Ports}}" | grep -E ":(8080|5432|6379)" || true)
if [ ! -z "$EXISTING_CONTAINERS" ]; then
    print_warning "Found existing services using standard ports:"
    echo "$EXISTING_CONTAINERS"
    print_status "Using alternative ports: 18080, 15432, 16379"
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

# Build and start services
print_status "Building and starting services..."
docker-compose -f docker-compose.existing-server.yml up -d --build

# Wait for services to be healthy
print_status "Waiting for services to be healthy..."
sleep 30

# Check service status
print_status "Checking service status..."
docker-compose -f docker-compose.existing-server.yml ps

# Check if app is responding
print_status "Testing application health..."
if curl -f http://localhost:18080/actuator/health > /dev/null 2>&1; then
    print_status "✅ Application is running successfully!"
    print_status "🌐 Access your app at: http://$(curl -s ifconfig.me):18080"
    print_status "📊 Health check: http://$(curl -s ifconfig.me):18080/actuator/health"
else
    print_error "❌ Application health check failed!"
    print_status "Checking logs..."
    docker-compose -f docker-compose.existing-server.yml logs app
fi

print_status "Deployment completed!"
print_status "To view logs: docker-compose -f docker-compose.existing-server.yml logs -f"
print_status "To stop services: docker-compose -f docker-compose.existing-server.yml down"
