# 🐳 Docker Deployment Guide

Complete guide to deploy the Kanban Dashboard using Docker.

## 📋 Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- 4GB RAM minimum (8GB recommended)
- 20GB disk space

## 🚀 Quick Start (Development)

### 1. Clone and Setup

```bash
cd Dashboard
cp .env.example .env
```

### 2. Edit `.env` file

```bash
# Update these values
DB_PASSWORD=your_secure_password
JWT_SECRET=your-very-long-random-secret-key-256-bits
VITE_API_URL=http://localhost:8080/api/v1
```

### 3. Build and Run

```bash
# Build all services
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f
```

### 4. Access the Application

- **Frontend:** http://localhost
- **Backend API:** http://localhost:8080/api/v1
- **Swagger UI:** http://localhost:8080/api/v1/swagger-ui.html
- **Login:** admin@taskhub.com / admin

### 5. Stop Services

```bash
docker-compose down

# Stop and remove volumes (⚠️ deletes all data)
docker-compose down -v
```

---

## 🌐 Production Deployment

### 1. Prepare Environment

```bash
# Create production .env
cp .env.example .env.production

# Edit with production values
nano .env.production
```

**Important Production Values:**

```env
DB_PASSWORD=<strong-random-password>
JWT_SECRET=<256-bit-random-secret>
REDIS_PASSWORD=<redis-password>
VITE_API_URL=https://api.yourdomain.com/api/v1
SPRING_PROFILES_ACTIVE=prod
```

### 2. Build Production Images

```bash
# Build with production optimizations
docker-compose -f docker-compose.yml -f docker-compose.prod.yml build --no-cache
```

### 3. Deploy

```bash
# Start with production config
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 4. Initialize Database

```bash
# Wait for services to be healthy
docker-compose ps

# Create initial admin user
docker-compose exec backend java -jar app.jar --spring.profiles.active=prod
```

---

## ☁️ Cloud Deployment Options

### Option 1: AWS (ECS/Fargate)

1. **Push images to ECR:**
```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account>.dkr.ecr.us-east-1.amazonaws.com

# Tag and push
docker tag kanban-backend:latest <account>.dkr.ecr.us-east-1.amazonaws.com/kanban-backend:latest
docker push <account>.dkr.ecr.us-east-1.amazonaws.com/kanban-backend:latest
```

2. **Create ECS Task Definition** using images
3. **Setup RDS MySQL** for database
4. **Setup ElastiCache Redis**
5. **Configure Application Load Balancer**
6. **Update environment variables** in task definition

### Option 2: Google Cloud (Cloud Run)

```bash
# Build and push to GCR
gcloud builds submit --tag gcr.io/PROJECT-ID/kanban-backend ./Backend
gcloud builds submit --tag gcr.io/PROJECT-ID/kanban-frontend ./Frontend

# Deploy backend
gcloud run deploy kanban-backend \
  --image gcr.io/PROJECT-ID/kanban-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars DB_HOST=<cloud-sql-host>,JWT_SECRET=<secret>

# Deploy frontend
gcloud run deploy kanban-frontend \
  --image gcr.io/PROJECT-ID/kanban-frontend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

### Option 3: Azure (Container Instances)

```bash
# Login to Azure
az login

# Create resource group
az group create --name kanban-rg --location eastus

# Create container registry
az acr create --resource-group kanban-rg --name kanbanacr --sku Basic

# Push images
az acr build --registry kanbanacr --image kanban-backend:latest ./Backend
az acr build --registry kanbanacr --image kanban-frontend:latest ./Frontend

# Deploy with Azure Container Instances
az container create \
  --resource-group kanban-rg \
  --name kanban-backend \
  --image kanbanacr.azurecr.io/kanban-backend:latest \
  --dns-name-label kanban-api \
  --ports 8080
```

### Option 4: DigitalOcean App Platform

```bash
# Create app spec
doctl apps create --spec app.yaml

# Or use the UI:
# 1. Connect GitHub repository
# 2. Select Dockerfile deployment
# 3. Add environment variables
# 4. Deploy
```

### Option 5: Heroku

```bash
# Login to Heroku
heroku login

# Create app
heroku create kanban-dashboard

# Add MySQL addon
heroku addons:create jawsdb:kitefin

# Add Redis addon
heroku addons:create heroku-redis:hobby-dev

# Deploy
heroku container:push web --app kanban-dashboard
heroku container:release web --app kanban-dashboard
```

### Option 6: Railway.app

1. Connect GitHub repository
2. Select `Dockerfile` deployment
3. Add services: Backend, Frontend, MySQL, Redis
4. Configure environment variables
5. Deploy automatically on git push

---

## 🔧 Docker Commands Cheat Sheet

```bash
# View running containers
docker-compose ps

# View logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Restart a service
docker-compose restart backend

# Execute command in container
docker-compose exec backend bash
docker-compose exec mysql mysql -u root -p

# View resource usage
docker stats

# Clean up
docker system prune -a
docker volume prune

# Backup database
docker-compose exec mysql mysqldump -u root -p kanbandb > backup.sql

# Restore database
docker-compose exec -T mysql mysql -u root -p kanbandb < backup.sql
```

---

## 📊 Monitoring & Health Checks

### Check Service Health

```bash
# Backend health
curl http://localhost:8080/api/v1/actuator/health

# Frontend health
curl http://localhost/health

# MySQL
docker-compose exec mysql mysqladmin -u root -p ping

# Redis
docker-compose exec redis redis-cli ping
```

---

## 🔒 Security Best Practices

1. **Change default passwords** in `.env`
2. **Use strong JWT secret** (256-bit minimum)
3. **Enable HTTPS** with SSL certificates
4. **Restrict database access** to backend only
5. **Use Docker secrets** for sensitive data
6. **Regular security updates:**
   ```bash
   docker-compose pull
   docker-compose up -d
   ```
7. **Setup firewall rules** on cloud provider
8. **Enable rate limiting** on API
9. **Setup monitoring & alerts**

---

## 🐛 Troubleshooting

### Backend won't start
```bash
# Check logs
docker-compose logs backend

# Common issues:
# 1. Database not ready - wait for healthcheck
# 2. Port 8080 in use - change port in docker-compose.yml
# 3. Memory issue - increase Docker memory limit
```

### Frontend can't connect to backend
```bash
# Check VITE_API_URL in .env
# Should match backend URL

# Rebuild frontend with new env
docker-compose build frontend --no-cache
docker-compose up -d frontend
```

### Database connection failed
```bash
# Check MySQL is running
docker-compose ps mysql

# Check MySQL logs
docker-compose logs mysql

# Test connection
docker-compose exec backend sh
nc -zv mysql 3306
```

---

## 📈 Performance Tuning

### Backend (Spring Boot)

```yaml
environment:
  JAVA_OPTS: "-Xms2g -Xmx4g -XX:+UseG1GC"
```

### MySQL

```bash
# In docker-compose.yml
command: >
  --max_connections=200
  --innodb_buffer_pool_size=1G
  --query_cache_size=64M
```

### Redis

```bash
command: redis-server --maxmemory 512mb --maxmemory-policy allkeys-lru
```

---

## 🔄 Updates & Maintenance

```bash
# Pull latest code
git pull origin main

# Rebuild and restart
docker-compose build
docker-compose up -d

# Zero-downtime update (with load balancer)
docker-compose up -d --no-deps --build backend
docker-compose up -d --no-deps --build frontend
```

---

## 📝 Environment Variables Reference

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | MySQL hostname | mysql | Yes |
| `DB_PORT` | MySQL port | 3306 | Yes |
| `DB_NAME` | Database name | kanbandb | Yes |
| `DB_USER` | Database user | kanban | Yes |
| `DB_PASSWORD` | Database password | - | Yes |
| `JWT_SECRET` | JWT signing secret | - | Yes |
| `JWT_EXPIRATION` | Token expiry (ms) | 86400000 | No |
| `REDIS_HOST` | Redis hostname | redis | Yes |
| `VITE_API_URL` | Backend API URL | - | Yes |

---

## 📞 Support

For issues or questions:
- Check logs: `docker-compose logs -f`
- GitHub Issues: [Your Repo]
- Documentation: See `docs/` folder

---

**Happy Deploying! 🚀**
