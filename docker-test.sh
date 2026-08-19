#!/bin/bash

# Docker setup verification script
echo "🔍 Verifying Docker setup..."
echo "================================"

# Check if required files exist
echo ""
echo "📁 Checking file structure..."

check_file() {
    if [ -f "$1" ]; then
        echo "  ✅ $1"
        return 0
    else
        echo "  ❌ $1 (NOT FOUND)"
        return 1
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo "  ✅ $1/"
        return 0
    else
        echo "  ❌ $1/ (NOT FOUND)"
        return 1
    fi
}

# Check directories
check_dir "Backend"
check_dir "frontend"
check_dir "src"

# Check files
check_file "pom.xml"
check_file "Backend/Dockerfile"
check_file "frontend/Dockerfile"
check_file "frontend/nginx.conf"
check_file "docker-compose.yml"
check_file ".env.example"

echo ""
echo "🐳 Docker setup verification:"
if docker --version > /dev/null 2>&1; then
    echo "  ✅ Docker installed: $(docker --version)"
else
    echo "  ❌ Docker not installed"
    exit 1
fi

if docker-compose --version > /dev/null 2>&1; then
    echo "  ✅ Docker Compose installed: $(docker-compose --version)"
else
    echo "  ❌ Docker Compose not installed"
    exit 1
fi

echo ""
echo "✨ Verification complete!"
echo ""
echo "🚀 Ready to deploy! Run:"
echo "   docker-compose up -d"
