#!/bin/bash

# Deployment script for Kanban Dashboard
# Usage: ./deploy.sh [environment]
# Example: ./deploy.sh production

set -e

ENVIRONMENT=${1:-development}
COMPOSE_FILE="docker-compose.yml"

echo "🚀 Deploying Kanban Dashboard - Environment: $ENVIRONMENT"
echo "=================================================="

# Load environment variables
if [ -f ".env.$ENVIRONMENT" ]; then
    echo "📄 Loading .env.$ENVIRONMENT"
    export $(cat .env.$ENVIRONMENT | xargs)
elif [ -f ".env" ]; then
    echo "📄 Loading .env"
    export $(cat .env | xargs)
else
    echo "⚠️  No environment file found. Using defaults."
fi

# Stop existing containers
echo ""
echo "🛑 Stopping existing containers..."
docker-compose down

# Pull latest images (if using pre-built images)
if [ "$ENVIRONMENT" == "production" ]; then
    echo ""
    echo "📥 Pulling latest images..."
    docker-compose pull
fi

# Build images
echo ""
echo "🔨 Building Docker images..."
if [ "$ENVIRONMENT" == "production" ]; then
    docker-compose -f $COMPOSE_FILE -f docker-compose.prod.yml build --no-cache
else
    docker-compose build
fi

# Start services
echo ""
echo "▶️  Starting services..."
if [ "$ENVIRONMENT" == "production" ]; then
    docker-compose -f $COMPOSE_FILE -f docker-compose.prod.yml up -d
else
    docker-compose up -d
fi

# Wait for services to be healthy
echo ""
echo "⏳ Waiting for services to be healthy..."
sleep 10

# Check service status
echo ""
echo "📊 Service Status:"
docker-compose ps

# Run database migrations (if needed)
echo ""
echo "🔄 Running database migrations..."
# Uncomment if you have migration scripts
# docker-compose exec backend java -jar app.jar --spring.profiles.active=prod migrate

# Health checks
echo ""
echo "🏥 Health Checks:"
echo "  Backend:  $(curl -sf http://localhost:8080/api/v1/actuator/health | jq -r '.status' 2>/dev/null || echo 'NOT_READY')"
echo "  Frontend: $(curl -sf http://localhost/health 2>/dev/null && echo 'HEALTHY' || echo 'NOT_READY')"

# Show logs
echo ""
echo "📋 Recent logs:"
docker-compose logs --tail=20

echo ""
echo "✅ Deployment complete!"
echo ""
echo "🌐 Access the application:"
if [ "$ENVIRONMENT" == "production" ]; then
    echo "  Frontend: https://yourdomain.com"
    echo "  Backend:  https://api.yourdomain.com"
else
    echo "  Frontend: http://localhost"
    echo "  Backend:  http://localhost:8080/api/v1"
    echo "  Swagger:  http://localhost:8080/api/v1/swagger-ui.html"
fi
echo ""
echo "📚 View logs: docker-compose logs -f"
echo "🛑 Stop services: docker-compose down"
