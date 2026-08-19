# 🚀 Docker Build Instructions - FIXED VERSION

## ⚠️ Prerequisites

1. **Start Docker Desktop** - Make sure Docker Desktop is running
2. Check Docker is running:
   ```bash
   docker --version
   docker-compose --version
   ```

---

## 🔧 What Was Fixed

### Issue: `sh: vite: Permission denied`

**Root Cause:** Alpine Linux + npm binary permission issues

**Solution Applied:**
Changed `npm run build` → `npx vite build` in frontend/Dockerfile

This bypasses the permission issue by running vite directly through npx.

---

## 🏗️ Build Steps

### Step 1: Clean Previous Builds
```bash
cd d:\Projects\Dashboard
docker-compose down -v
docker system prune -f
```

### Step 2: Build All Services
```bash
docker-compose build --no-cache
```

**Expected Output:**
```
[+] Building 180.2s (25/25) FINISHED
 ✓ frontend build complete
 ✓ backend build complete
```

### Step 3: Start Services
```bash
docker-compose up -d
```

### Step 4: Wait for Health Checks (60 seconds)
```bash
# Watch logs
docker-compose logs -f

# Or check status
docker-compose ps
```

**Expected Output:**
```
NAME               STATUS
kanban-mysql       Up (healthy)
kanban-redis       Up (healthy)
kanban-backend     Up (healthy)
kanban-frontend    Up (healthy)
```

---

## ✅ Verify Build Success

### Check All Containers Running:
```bash
docker-compose ps
```

### Test Frontend:
```bash
curl http://localhost
# Should return HTML
```

### Test Backend:
```bash
curl http://localhost:8080/api/v1/actuator/health
# Should return: {"status":"UP"}
```

### Test MySQL:
```bash
docker-compose exec mysql mysqladmin -u root -pkanban ping
# Should return: mysqld is alive
```

### Test Redis:
```bash
docker-compose exec redis redis-cli ping
# Should return: PONG
```

---

## 🌐 Access Application

Once all services are healthy:

- **Frontend:** http://localhost
- **Backend API:** http://localhost:8080/api/v1
- **Swagger UI:** http://localhost:8080/api/v1/swagger-ui.html

### Login Credentials:
- **Email:** admin@taskhub.com
- **Password:** admin

---

## 🐛 Troubleshooting

### If Frontend Build Still Fails:

**Option 1: Try without Alpine (more compatible)**
Edit `frontend/Dockerfile`, change first line:
```dockerfile
FROM node:18-alpine AS build
↓
FROM node:18 AS build
```

**Option 2: Run vite directly with node**
Edit `frontend/Dockerfile`, change build line:
```dockerfile
RUN npx vite build
↓
RUN node node_modules/vite/bin/vite.js build
```

**Option 3: Add explicit permissions**
Edit `frontend/Dockerfile`, add before build:
```dockerfile
RUN chmod -R 755 node_modules/.bin
RUN npx vite build
```

### If Docker Desktop Not Running:
```
Error: failed to connect to docker API
```
**Fix:** Start Docker Desktop application, wait for it to fully start

### If Port 80 or 8080 Already in Use:
Edit `docker-compose.yml`:
```yaml
frontend:
  ports:
    - "3000:80"  # Changed from 80:80

backend:
  ports:
    - "8081:8080"  # Changed from 8080:8080
```

### If MySQL Fails to Start:
```bash
# Remove volumes and try again
docker-compose down -v
docker volume prune -f
docker-compose up -d
```

---

## 📊 Build Time Expectations

| Service | First Build | Cached Build |
|---------|------------|--------------|
| MySQL | ~10s | ~3s |
| Redis | ~5s | ~2s |
| Backend | 3-5 min | 30s |
| Frontend | 2-3 min | 20s |
| **TOTAL** | **5-8 min** | **1 min** |

---

## 🔄 Complete Rebuild Script

If you want to start completely fresh:

```bash
# Stop everything
docker-compose down -v

# Remove all Docker data (⚠️ This removes ALL Docker images/containers)
docker system prune -af --volumes

# Rebuild from scratch
cd d:\Projects\Dashboard
docker-compose build --no-cache
docker-compose up -d

# Wait and check
timeout /t 60
docker-compose ps
```

---

## 📝 File Changes Summary

### Files Modified to Fix Permission Issue:

1. **frontend/Dockerfile**
   - Changed: `RUN npm run build` → `RUN npx vite build`
   - Reason: Bypass Alpine Linux permission issues with npm binaries

2. **docker-compose.yml**
   - Fixed: `context: ./Frontend` → `context: ./frontend`
   - Reason: Case sensitivity on Linux systems

---

## ✨ After Successful Build

You should see:
```bash
$ docker-compose ps

NAME               IMAGE                    STATUS
kanban-mysql       mysql:8.0                Up (healthy)
kanban-redis       redis:7-alpine           Up (healthy)  
kanban-backend     dashboard-backend        Up (healthy)
kanban-frontend    dashboard-frontend       Up (healthy)
```

Open browser: **http://localhost** 🎉

---

## 🚀 Deploy to Cloud

Once working locally, you can deploy to:

### Option 1: Railway.app
```bash
# Install Railway CLI
npm install -g @railway/cli

# Login and deploy
railway login
railway up
```

### Option 2: DigitalOcean App Platform
1. Push code to GitHub
2. Connect repository in DigitalOcean
3. Choose "Docker Compose" as source
4. Deploy

### Option 3: AWS ECS with docker-compose
```bash
# Install ecs-cli
# Configure with your AWS credentials
ecs-cli compose up
```

### Option 4: Any VPS (Ubuntu/Debian)
```bash
# SSH to server
ssh user@your-server-ip

# Clone repo
git clone <your-repo-url>
cd Dashboard

# Install Docker & Docker Compose
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Run
docker-compose up -d
```

---

## 📞 Need Help?

If build still fails, share the **full error output**:
```bash
docker-compose build frontend --no-cache 2>&1 | tee build-error.log
```

Send the `build-error.log` file for debugging.
