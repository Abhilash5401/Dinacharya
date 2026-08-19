# ⚡ EC2 Quick Start Guide

**Fast deployment guide for AWS EC2**

---

## 📋 **Changes Made for EC2:**

| File | Change | Reason |
|------|--------|--------|
| **docker-compose.yml** | Added resource limits | Optimize for small instances |
| **docker-compose.yml** | MySQL/Redis localhost only | Security (no external access) |
| **docker-compose.yml** | Reduced memory settings | t3.medium compatibility |
| **frontend/Dockerfile** | Added ARG VITE_API_URL | Dynamic API URL |
| **frontend/nginx.conf** | Enabled API proxy | Single-port access |
| **frontend/nginx.conf** | Added file upload settings | Support 10MB files |

---

## 🚀 **Deploy in 5 Steps:**

### **1. Launch EC2**
```
Instance: t3.medium (2 vCPU, 4GB RAM)
Storage: 30GB gp3 SSD
OS: Ubuntu 22.04 LTS
Security Group: Allow ports 22, 80, 443
```

### **2. Install Docker**
```bash
ssh -i key.pem ubuntu@your-ip
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
exit && ssh -i key.pem ubuntu@your-ip
sudo apt install docker-compose-plugin git -y
```

### **3. Clone & Configure**
```bash
git clone <your-repo> Dashboard
cd Dashboard
cp .env.example .env
nano .env  # Change DB_PASSWORD and JWT_SECRET!
```

### **4. Build & Run**
```bash
docker compose build --no-cache
docker compose up -d
```

### **5. Verify**
```bash
# Wait 90 seconds
docker compose ps  # All should be (healthy)
curl http://localhost  # Should return HTML
```

**Access:** `http://your-ec2-ip`  
**Login:** admin@taskhub.com / admin

---

## 🔧 **Resource Limits (Configured)**

| Service | CPU | Memory | Why |
|---------|-----|--------|-----|
| MySQL | 0.5 | 512MB | Database with limits |
| Redis | 0.25 | 256MB | Cache with LRU eviction |
| Backend | 1.0 | 1GB | Java app (main resource) |
| Frontend | 0.25 | 128MB | Static files only |

**Total:** ~2GB RAM (fits t3.medium with room to spare)

---

## 🔒 **Security (Configured)**

✅ MySQL: Bound to 127.0.0.1 (not accessible externally)  
✅ Redis: Bound to 127.0.0.1 (not accessible externally)  
✅ Backend: Internal network only  
✅ Frontend: Nginx reverse proxy for API  

**Only port 80/443 exposed to internet**

---

## 🌐 **API Routing (Configured)**

**Before:** Frontend → Backend:8080  
**After:** Frontend → Nginx → Backend (internal)

```
Browser → http://your-ip/
        → http://your-ip/api/v1/* → Backend
```

**Benefit:** Single entry point, no CORS issues

---

## 📊 **File Upload Support (Configured)**

Nginx configured for Task Import feature:
- Max file size: **10MB**
- Timeout: **300 seconds**
- Supported: `.xlsx`, `.docx`

---

## 🎯 **Important Commands**

```bash
# Check status
docker compose ps

# View logs
docker compose logs -f

# Restart
docker compose restart

# Update app
git pull && docker compose down && docker compose build && docker compose up -d

# Backup database
docker compose exec mysql mysqldump -u kanban -p kanbandb > backup.sql

# Clean Docker
docker system prune -f
```

---

## ⚠️ **Before Going Live:**

1. **Change passwords in `.env`:**
   ```bash
   DB_PASSWORD=YourSecure123!
   JWT_SECRET=your-random-256-bit-secret-key-here
   ```

2. **Update security group:**
   - SSH (22): Restrict to your IP
   - HTTP (80): Open to 0.0.0.0/0
   - HTTPS (443): Open to 0.0.0.0/0
   - **Don't expose:** 3306, 6379, 8080

3. **Enable swap (for small instances):**
   ```bash
   sudo fallocate -l 2G /swapfile
   sudo chmod 600 /swapfile
   sudo mkswap /swapfile
   sudo swapon /swapfile
   ```

---

## 🐛 **Troubleshooting**

### Out of Memory?
```bash
free -h  # Check memory
docker stats  # Check containers
# Add swap (see above)
```

### Out of Disk?
```bash
df -h  # Check disk
docker system prune -af  # Clean up
```

### Service Won't Start?
```bash
docker compose logs <service>  # Check logs
docker compose restart <service>  # Restart it
```

### Can't Access from Browser?
1. Check security group allows port 80
2. Check services: `docker compose ps`
3. Check logs: `docker compose logs frontend`

---

## 📈 **Instance Sizing Guide**

| Instance | RAM | Use Case | Notes |
|----------|-----|----------|-------|
| t3.small | 2GB | Dev/Test | Will be slow, add 2GB swap |
| **t3.medium** | **4GB** | **Production** | **Recommended** |
| t3.large | 8GB | High traffic | Overkill for most cases |

---

## ✅ **You're Ready!**

**Everything is configured for EC2 deployment.**

**Next steps:**
1. Launch t3.medium EC2 instance
2. Follow 5-step deploy guide above
3. Access your app at `http://your-ec2-ip`
4. (Optional) Add domain + SSL

**Full guide:** [EC2_DEPLOYMENT_GUIDE.md](./EC2_DEPLOYMENT_GUIDE.md)

---

**Deploy time:** ~15 minutes  
**Cost:** ~$30/month (t3.medium)  
**Features:** All working including Task Import! ✅
