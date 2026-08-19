# 🚀 Kanban Dashboard - EC2 Deployment

**Production-ready Task Management System with Excel/Word Import**

---

## ✅ **What's Configured:**

Your application is **fully optimized** for AWS EC2 deployment:

- ✅ Resource limits for t3.medium (2 vCPU, 4GB RAM)
- ✅ Secure configuration (DB/Redis not exposed)
- ✅ API proxying through Nginx (no CORS)
- ✅ File upload support (10MB limit)
- ✅ Health checks for all services
- ✅ Task Import feature (Excel/Word) included

---

## 📚 **Documentation:**

| File | Purpose | Read Time |
|------|---------|-----------|
| **[EC2_QUICK_START.md](./EC2_QUICK_START.md)** | Deploy in 5 steps | 5 min |
| **[EC2_DEPLOYMENT_GUIDE.md](./EC2_DEPLOYMENT_GUIDE.md)** | Complete deployment guide | 15 min |
| **[EC2_CHANGES_SUMMARY.md](./EC2_CHANGES_SUMMARY.md)** | What changed and why | 5 min |

---

## ⚡ **Quick Deploy (5 Steps):**

### **1. Launch EC2 Instance**
```
Type: t3.medium
vCPU: 2
RAM: 4 GB
Storage: 30 GB gp3
OS: Ubuntu 22.04 LTS
Ports: 22, 80, 443
```

### **2. Install Docker**
```bash
ssh -i key.pem ubuntu@your-ec2-ip
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
exit && ssh -i key.pem ubuntu@your-ec2-ip
sudo apt install docker-compose-plugin git -y
```

### **3. Deploy Application**
```bash
git clone <your-repo> Dashboard
cd Dashboard
cp .env.example .env
nano .env  # Change DB_PASSWORD and JWT_SECRET!
```

### **4. Build & Start**
```bash
docker compose build --no-cache
docker compose up -d
```

### **5. Verify**
```bash
# Wait 90 seconds for startup
docker compose ps  # All should show (healthy)

# Access your app
http://your-ec2-public-ip
```

**Login:** admin@taskhub.com / admin

---

## 🎯 **Architecture:**

```
                     Internet
                        │
                        ▼
                  [EC2 Instance]
                   Port 80/443
                        │
                        ▼
              ┌─────────────────┐
              │  Nginx (Port 80)│
              │   (Frontend)    │
              └────────┬────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
        ▼              ▼              ▼
   Static Files    /api/v1/*      /ws/*
                       │              │
                       ▼              ▼
              ┌─────────────────────────┐
              │  Spring Boot (Internal) │
              │  Port 8080 (Backend)    │
              └──────────┬──────────────┘
                         │
            ┌────────────┼────────────┐
            ▼                         ▼
    ┌──────────────┐         ┌──────────────┐
    │    MySQL     │         │    Redis     │
    │ Port 3306    │         │  Port 6379   │
    │ (Internal)   │         │ (Internal)   │
    └──────────────┘         └──────────────┘
```

---

## 📊 **Resource Usage:**

| Service | CPU | Memory | Notes |
|---------|-----|--------|-------|
| MySQL | 0.5 | 512 MB | Database with connection limits |
| Redis | 0.25 | 256 MB | Cache with LRU eviction |
| Backend | 1.0 | 768 MB | Java application (main service) |
| Frontend | 0.25 | 128 MB | Nginx serving static files |
| **Total** | **2.0** | **~1.7 GB** | Leaves ~2GB free on t3.medium |

---

## 🔒 **Security:**

| Service | Port | Access |
|---------|------|--------|
| **Frontend** | 80, 443 | 🌍 Public (Internet) |
| **Backend** | 8080 | 🔒 Internal only |
| **MySQL** | 3306 | 🔒 Localhost only |
| **Redis** | 6379 | 🔒 Localhost only |

**Result:** Only HTTP/HTTPS exposed to internet, everything else internal!

---

## 📁 **File Upload Support:**

The Task Import feature is configured for production:

- **Max file size:** 10 MB
- **Timeout:** 300 seconds
- **Supported formats:** .xlsx, .docx
- **Location:** Task Management page → "Import Tasks"

**Nginx handles:**
- File upload buffering
- Timeout management
- Size validation
- Progress tracking

---

## 💰 **Cost Estimate:**

| Instance | Monthly Cost | Use Case |
|----------|-------------|----------|
| t3.small | $15 | Development/Testing |
| **t3.medium** | **$30** | **Production (Recommended)** |
| t3.large | $60 | High traffic (overkill) |

**Recommended:** t3.medium for $30/month

---

## 🎯 **Features Included:**

### **Core Features:**
- ✅ Task management (CRUD)
- ✅ Team collaboration
- ✅ User management
- ✅ Role-based access control
- ✅ Real-time updates (WebSocket)
- ✅ Performance analytics
- ✅ Attendance tracking

### **Import Feature:**
- ✅ Excel (.xlsx) import
- ✅ Word (.docx) import
- ✅ Drag & drop upload
- ✅ Template download
- ✅ Batch task creation
- ✅ Error reporting
- ✅ Partial success handling

---

## 📋 **Environment Variables:**

Edit `.env` before deployment:

```bash
# REQUIRED: Change these!
DB_PASSWORD=YourSecurePassword123!
JWT_SECRET=your-random-256-bit-secret-minimum-32-characters

# Frontend (leave as default for Nginx proxy)
VITE_API_URL=/api/v1

# Optional
SPRING_PROFILES_ACTIVE=prod
```

---

## 🔧 **Common Commands:**

```bash
# Check status
docker compose ps

# View logs
docker compose logs -f

# Restart all services
docker compose restart

# Restart specific service
docker compose restart backend

# Update application
git pull
docker compose down
docker compose build --no-cache
docker compose up -d

# Backup database
docker compose exec mysql mysqldump -u kanban -p kanbandb > backup.sql

# Restore database
docker compose exec -T mysql mysql -u kanban -p kanbandb < backup.sql

# Clean Docker cache
docker system prune -f

# Monitor resources
docker stats
```

---

## 🐛 **Troubleshooting:**

### **Services not starting?**
```bash
docker compose logs  # Check errors
docker compose down && docker compose up -d  # Restart
```

### **Out of memory?**
```bash
free -h  # Check memory
docker stats  # Check container usage

# Add swap:
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

### **Can't access from browser?**
1. Check Security Group allows port 80
2. Check services: `docker compose ps`
3. Check logs: `docker compose logs frontend`
4. Test locally: `curl http://localhost`

### **Import not working?**
1. Check file size < 10MB
2. Check logs: `docker compose logs backend`
3. Verify format: .xlsx or .docx
4. Check Nginx logs: `docker compose logs frontend`

---

## 📖 **API Documentation:**

Once deployed, access Swagger UI:

```
http://your-ec2-ip/api/v1/swagger-ui.html
```

### **Import Endpoints:**
```
POST /api/v1/import/tasks/excel/{teamId}
POST /api/v1/import/tasks/word/{teamId}
GET  /api/v1/import/template/excel
GET  /api/v1/import/template/info
```

---

## 🌐 **Add Domain & SSL:**

### **Quick Setup with CloudFlare (Easiest):**
1. Point domain to EC2 IP in CloudFlare
2. Enable proxy (orange cloud)
3. SSL/TLS → Full
4. Done! CloudFlare handles SSL

### **Manual SSL with Let's Encrypt:**
See [EC2_DEPLOYMENT_GUIDE.md](./EC2_DEPLOYMENT_GUIDE.md) for detailed steps.

---

## 📈 **Monitoring:**

### **View Logs:**
```bash
docker compose logs -f backend   # Backend logs
docker compose logs -f frontend  # Nginx logs
docker compose logs -f mysql     # Database logs
```

### **Resource Usage:**
```bash
docker stats        # Container stats
htop               # System resources
df -h              # Disk usage
free -h            # Memory usage
```

### **Health Checks:**
```bash
# All services
docker compose ps

# Backend health
curl http://localhost:8080/api/v1/actuator/health

# Frontend health
curl http://localhost/health
```

---

## ✅ **Production Checklist:**

Before going live:

- [ ] EC2 instance launched (t3.medium)
- [ ] Security groups configured
- [ ] Docker installed
- [ ] Application deployed
- [ ] `.env` configured with secure passwords
- [ ] All services showing (healthy)
- [ ] Can access via browser
- [ ] Login works
- [ ] Task Import tested
- [ ] Domain configured (optional)
- [ ] SSL enabled (optional)
- [ ] Backup strategy planned
- [ ] Monitoring setup

---

## 📞 **Support:**

| Issue | Documentation |
|-------|---------------|
| **Quick deploy** | [EC2_QUICK_START.md](./EC2_QUICK_START.md) |
| **Full guide** | [EC2_DEPLOYMENT_GUIDE.md](./EC2_DEPLOYMENT_GUIDE.md) |
| **What changed** | [EC2_CHANGES_SUMMARY.md](./EC2_CHANGES_SUMMARY.md) |
| **Docker details** | [DOCKER_REVIEW.md](./DOCKER_REVIEW.md) |
| **Import feature** | [TASK_IMPORT_GUIDE.md](./TASK_IMPORT_GUIDE.md) |

---

## 🎉 **You're Ready!**

Everything is configured and tested for EC2 deployment:

✅ Optimized for t3.medium instance  
✅ Resource limits configured  
✅ Security hardened  
✅ API proxying enabled  
✅ File uploads supported  
✅ Health checks working  
✅ Task Import feature included  

**Deploy time:** ~15-20 minutes  
**Monthly cost:** ~$30 (t3.medium)  

**Start deploying:** [EC2_QUICK_START.md](./EC2_QUICK_START.md) 🚀
