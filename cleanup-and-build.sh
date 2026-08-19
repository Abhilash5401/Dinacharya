#!/bin/bash

echo "🧹 Docker Cleanup Script for Low-Resource Servers"
echo "=================================================="

# Check current disk usage
echo ""
echo "📊 Current Disk Usage:"
df -h

# Check Docker disk usage
echo ""
echo "🐳 Docker Disk Usage:"
docker system df

# Stop all containers
echo ""
echo "🛑 Stopping all containers..."
docker-compose down -v

# Remove all stopped containers
echo ""
echo "🗑️  Removing stopped containers..."
docker container prune -f

# Remove all unused images
echo ""
echo "🗑️  Removing unused images..."
docker image prune -af

# Remove all unused volumes
echo ""
echo "🗑️  Removing unused volumes..."
docker volume prune -f

# Remove build cache
echo ""
echo "🗑️  Removing build cache..."
docker builder prune -af

# Check disk usage after cleanup
echo ""
echo "📊 Disk Usage After Cleanup:"
df -h

echo ""
echo "🐳 Docker Disk Usage After Cleanup:"
docker system df

echo ""
echo "✅ Cleanup complete!"
echo ""
echo "💾 Free space now available. You can now build:"
echo "   docker-compose build --no-cache"
