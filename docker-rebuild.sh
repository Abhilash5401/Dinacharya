#!/bin/bash

# Complete rebuild script after fixes
echo "🔧 Rebuilding Docker containers with fixes..."
echo "=============================================="

# Stop and clean everything
echo ""
echo "🛑 Stopping existing containers..."
docker-compose down -v

# Clean Docker build cache
echo ""
echo "🧹 Cleaning Docker cache..."
docker builder prune -f

# Rebuild everything
echo ""
echo "🔨 Building all services (this may take 5-8 minutes)..."
docker-compose build --no-cache

# Start services
echo ""
echo "▶️  Starting services..."
docker-compose up -d

# Wait for services
echo ""
echo "⏳ Waiting for services to be healthy (60 seconds)..."
sleep 60

# Check status
echo ""
echo "📊 Service Status:"
docker-compose ps

# Show recent logs
echo ""
echo "📋 Recent Logs:"
docker-compose logs --tail=50

# Health check
echo ""
echo "🏥 Health Check:"
echo "  MySQL:    $(docker-compose exec -T mysql mysqladmin -u root -pkanban ping 2>/dev/null | grep 'alive' && echo '✅ HEALTHY' || echo '⏳ STARTING')"
echo "  Redis:    $(docker-compose exec -T redis redis-cli ping 2>/dev/null | grep 'PONG' && echo '✅ HEALTHY' || echo '⏳ STARTING')"
echo "  Backend:  $(curl -sf http://localhost:8080/api/v1/actuator/health 2>/dev/null | grep -q 'UP' && echo '✅ HEALTHY' || echo '⏳ STARTING')"
echo "  Frontend: $(curl -sf http://localhost/ 2>/dev/null | grep -q 'html' && echo '✅ HEALTHY' || echo '⏳ STARTING')"

echo ""
echo "=============================================="
echo "✅ Build complete!"
echo ""
echo "🌐 Access your application:"
echo "   Frontend: http://localhost"
echo "   Backend:  http://localhost:8080/api/v1"
echo "   Swagger:  http://localhost:8080/api/v1/swagger-ui.html"
echo ""
echo "🔑 Login with:"
echo "   Email: admin@taskhub.com"
echo "   Password: admin"
echo ""
echo "📋 View logs: docker-compose logs -f"
echo "🛑 Stop: docker-compose down"
