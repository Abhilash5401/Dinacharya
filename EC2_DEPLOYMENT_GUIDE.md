# 🚀 AWS EC2 Deployment Guide - Kanban Dashboard

Complete guide to deploy your Kanban Dashboard with Task Import feature on AWS EC2.

---

## 📋 **Table of Contents**

1. [EC2 Instance Requirements](#ec2-instance-requirements)
2. [Security Group Configuration](#security-group-configuration)
3. [Server Setup](#server-setup)
4. [Deploy Application](#deploy-application)
5. [Domain & SSL Setup](#domain--ssl-setup)
6. [Monitoring & Maintenance](#monitoring--maintenance)

---

## 🖥️ **EC2 Instance Requirements**

### **Recommended Instance Types:**

| Instance Type | vCPU | RAM | Cost/Month | Use Case |
|---------------|------|-----|------------|----------|
| **t3.small** | 2 | 2 GB | ~$15 | Development/Testing |
| **t3.medium** | 2 | 4 GB | ~$30 | **Production (Recommended)** |
| **t3.large** | 2 | 8 GB | ~$60 | High traffic |

**Minimum:** t3.small (but will be slow)  
**Recommended:** **t3.medium** for smooth performance

### **Storage:**
- **Minimum:** 20 GB gp3 SSD
- **Recommended:** 30 GB gp3 SSD
- Type: General Purpose SSD (gp3)

### **Operating System:**
- **Ubuntu 22.04 LTS** (Recommended)
- Or Ubuntu 20.04 LTS
- Or Amazon Linux 2023

---

## 🔒 **Security Group Configuration**

Create/Edit security group with these **Inbound Rules**:

| Type | Protocol | Port | Source | Purpose |
|------|----------|------|--------|---------|
| SSH | TCP | 22 | Your IP | Remote access |
| HTTP | TCP | 80 | 0.0.0.0/0 | Web access |
| HTTPS | TCP | 443 | 0.0.0.0/0 | Secure web (SSL) |
| Custom TCP | TCP | 8080 | 0.0.0.0/0 | Backend API (optional) |

**Important:** 
- For production, restrict SSH to your IP only
- Don't expose MySQL (3306) or Redis (6379) ports

---

## 🛠️ **Server Setup**

### **Step 1: Connect to EC2**

```bash
# Download your .pem key from AWS
chmod 400 your-key.pem
ssh -i your-key.pem ubuntu@your-ec2-ip
```

### **Step 2: Update System**

```bash
sudo apt update && sudo apt upgrade -y
```

### **Step 3: Install Docker**

```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Logout and login again
exit
# SSH back in
ssh -i your-key.pem ubuntu@your-ec2-ip

# Verify Docker
docker --version
```

### **Step 4: Install Docker Compose**

```bash
# Install Docker Compose
sudo apt install docker-compose-plugin -y

# Verify
docker compose version
```

### **Step 5: Install Git**

```bash
sudo apt install git -y
git --version
```

---

## 🚢 **Deploy Application**

### **Step 1: Clone Repository**

```bash
cd ~
git clone <your-repo-url> Dashboard
cd Dashboard
```

**Or upload files via SCP:**
```bash
# From your local machine:
scp -i your-key.pem -r d:\Projects\Dashboard ubuntu@your-ec2-ip:~/
```

### **Step 2: Create Environment File**

```bash
cd ~/Dashboard
cp .env.example .env
nano .env
```

**Edit `.env` file:**

```bash
# Database Configuration
DB_NAME=kanbandb
DB_USER=kanban
DB_PASSWORD=YourSecurePassword123!  # CHANGE THIS!

# JWT Configuration (IMPORTANT: Change these!)
JWT_SECRET=your-super-secret-256-bit-key-change-this-to-random-string-min-32-chars
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Redis Configuration
REDIS_HOST=redis
REDIS_PORT=6379

# Frontend API URL (use /api/v1 for nginx proxy)
VITE_API_URL=/api/v1

# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Cloudinary (Optional - for future file uploads)
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
```

**Save:** `Ctrl+O`, `Enter`, `Ctrl+X`

### **Step 3: Build Docker Images**

```bash
cd ~/Dashboard

# This takes 5-10 minutes on t3.medium
docker compose build --no-cache
```

**Expected output:**
```
[+] Building 350.2s (45/45) FINISHED
 ✓ frontend (200s)
 ✓ backend (300s)
```

### **Step 4: Start Services**

```bash
docker compose up -d
```

### **Step 5: Wait for Services** 

Wait 90-120 seconds for all services to become healthy:

```bash
# Watch status (Ctrl+C to exit)
watch docker compose ps

# Or check once
docker compose ps
```

**Expected output:**
```
NAME               STATUS
kanban-mysql       Up (healthy)
kanban-redis       Up (healthy)
kanban-backend     Up (healthy)
kanban-frontend    Up (healthy)
```

### **Step 6: Verify Deployment**

```bash
# Check backend health
curl http://localhost:8080/api/v1/actuator/health

# Check frontend
curl http://localhost

# View logs
docker compose logs -f backend
docker compose logs -f frontend
```

### **Step 7: Access Application**

Open browser: `http://your-ec2-public-ip`

**Login with:**
- Email: `admin@taskhub.com`
- Password: `admin`

---

## 🌐 **Domain & SSL Setup**

### **Option 1: Using Elastic IP + Route 53**

#### **1. Allocate Elastic IP**
```
AWS Console → EC2 → Elastic IPs → Allocate
Associate with your EC2 instance
```

#### **2. Configure Route 53**
```
Create Hosted Zone → yourdomain.com
Add A Record → Points to Elastic IP
```

#### **3. Install Certbot for SSL**

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Stop containers temporarily
cd ~/Dashboard
docker compose down

# Get SSL certificate
sudo certbot certonly --standalone -d yourdomain.com -d www.yourdomain.com

# Certificates saved to:
# /etc/letsencrypt/live/yourdomain.com/fullchain.pem
# /etc/letsencrypt/live/yourdomain.com/privkey.pem
```

#### **4. Update Nginx Configuration**

Create `frontend/nginx-ssl.conf`:

```bash
nano ~/Dashboard/frontend/nginx-ssl.conf
```

```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    
    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;
    
    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/json;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # API proxy
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # File upload settings
        client_max_body_size 10M;
        proxy_connect_timeout 300;
        proxy_send_timeout 300;
        proxy_read_timeout 300;
    }

    # Static files
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # React routing
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Health check
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

#### **5. Update docker-compose.yml for SSL**

```bash
nano ~/Dashboard/docker-compose.yml
```

Add SSL certificate mounts to frontend service:

```yaml
  frontend:
    # ... existing config ...
    volumes:
      - /etc/letsencrypt:/etc/letsencrypt:ro
```

Update frontend Dockerfile to use new config:

```bash
nano ~/Dashboard/frontend/Dockerfile
```

Change the nginx config copy line:

```dockerfile
# Copy SSL-enabled nginx config
COPY nginx-ssl.conf /etc/nginx/conf.d/default.conf
```

#### **6. Rebuild and Restart**

```bash
cd ~/Dashboard
docker compose down
docker compose build frontend --no-cache
docker compose up -d
```

#### **7. Auto-renew SSL Certificate**

```bash
# Test renewal
sudo certbot renew --dry-run

# Add cron job for auto-renewal
sudo crontab -e

# Add this line:
0 0 1 * * certbot renew --quiet --post-hook "cd /home/ubuntu/Dashboard && docker compose restart frontend"
```

---

### **Option 2: Using CloudFlare (Easier)**

1. Point your domain to EC2 IP in CloudFlare DNS
2. Enable CloudFlare proxy (orange cloud)
3. SSL/TLS → Full (not Strict)
4. CloudFlare handles SSL for you!

---

## 📊 **Monitoring & Maintenance**

### **View Logs**

```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql

# Last 100 lines
docker compose logs --tail=100
```

### **Check Resource Usage**

```bash
# Docker stats
docker stats

# System resources
htop
# or
free -h
df -h
```

### **Restart Services**

```bash
# Restart all
docker compose restart

# Restart specific service
docker compose restart backend
docker compose restart frontend
```

### **Update Application**

```bash
cd ~/Dashboard

# Pull latest code
git pull

# Rebuild and restart
docker compose down
docker compose build --no-cache
docker compose up -d
```

### **Backup Database**

```bash
# Backup
docker compose exec mysql mysqldump -u kanban -p12345678 kanbandb > backup-$(date +%Y%m%d).sql

# Restore
docker compose exec -T mysql mysql -u kanban -p12345678 kanbandb < backup-20240815.sql
```

### **Clean Up Docker**

```bash
# Remove unused images
docker image prune -f

# Remove unused volumes
docker volume prune -f

# Full cleanup (careful!)
docker system prune -af --volumes
```

---

## 🔥 **Troubleshooting**

### **Issue: Out of Memory**

```bash
# Check memory
free -h

# Add swap
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### **Issue: Out of Disk Space**

```bash
# Check disk usage
df -h

# Clean Docker
docker system prune -af --volumes

# Check large files
du -sh /* | sort -h
```

### **Issue: Port Already in Use**

```bash
# Find process using port 80
sudo lsof -i :80

# Kill process
sudo kill -9 <PID>

# Or use different port in docker-compose.yml
```

### **Issue: Services Not Starting**

```bash
# Check logs
docker compose logs

# Check health
docker compose ps

# Restart
docker compose down
docker compose up -d

# Check specific service
docker compose logs backend
```

### **Issue: Cannot Access from Browser**

1. Check Security Group allows port 80/443
2. Check if services are running: `docker compose ps`
3. Check firewall: `sudo ufw status`
4. Check nginx logs: `docker compose logs frontend`

---

## 📈 **Performance Optimization**

### **1. Enable Swap Memory**

Already covered above - adds virtual memory.

### **2. Adjust JVM Settings**

Edit `docker-compose.yml`:

```yaml
backend:
  environment:
    JAVA_OPTS: "-Xms256m -Xmx512m"  # For t3.small
    # JAVA_OPTS: "-Xms256m -Xmx768m"  # For t3.medium (current)
    # JAVA_OPTS: "-Xms512m -Xmx1536m"  # For t3.large
```

### **3. Redis Memory Limit**

Already configured in docker-compose.yml:
- Max memory: 256MB
- Eviction policy: LRU (least recently used)

### **4. MySQL Tuning**

Create `mysql-custom.cnf`:

```bash
mkdir -p ~/Dashboard/mysql-conf
nano ~/Dashboard/mysql-conf/custom.cnf
```

```ini
[mysqld]
innodb_buffer_pool_size=256M
max_connections=100
query_cache_size=0
innodb_log_file_size=64M
```

Update docker-compose.yml:

```yaml
mysql:
  volumes:
    - mysql_data:/var/lib/mysql
    - ./mysql-conf:/etc/mysql/conf.d
```

---

## ✅ **Deployment Checklist**

Before going live:

- [ ] EC2 instance running (t3.medium recommended)
- [ ] Security groups configured (ports 80, 443, 22)
- [ ] Docker and Docker Compose installed
- [ ] Application cloned/uploaded to server
- [ ] `.env` file configured with secure passwords
- [ ] JWT_SECRET changed to random string
- [ ] DB_PASSWORD changed from default
- [ ] Docker images built successfully
- [ ] All services showing (healthy)
- [ ] Can access via EC2 public IP
- [ ] Domain configured (optional)
- [ ] SSL certificate installed (optional)
- [ ] Backup strategy in place
- [ ] Monitoring configured

---

## 🎯 **Quick Command Reference**

```bash
# Deploy/Update
cd ~/Dashboard
git pull
docker compose down
docker compose build --no-cache
docker compose up -d

# Check status
docker compose ps
docker compose logs -f

# Restart
docker compose restart

# Backup DB
docker compose exec mysql mysqldump -u kanban -p kanbandb > backup.sql

# Clean up
docker system prune -f

# View resources
docker stats
free -h
df -h
```

---

## 🚀 **You're Ready to Deploy!**

Your configuration is now optimized for EC2 deployment with:

✅ Resource limits for small instances  
✅ Security (localhost-only MySQL/Redis)  
✅ API proxying through Nginx  
✅ File upload support (10MB limit)  
✅ Health checks for all services  
✅ Optimized memory usage  

**Start deploying:** Follow the steps above! 🎉
