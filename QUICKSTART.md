# 🚀 Quick Start - Deploy in 5 Minutes

Get your Kanban Dashboard running in the cloud quickly!

## Prerequisites

- Docker & Docker Compose installed
- Domain name (optional, for production)
- Cloud account (AWS, GCP, Azure, DigitalOcean, etc.)

---

## Option 1: Local Development (30 seconds)

```bash
# 1. Clone repo
git clone <your-repo-url>
cd Dashboard

# 2. Copy environment file
cp .env.example .env

# 3. Start everything
docker-compose up -d

# 4. Wait for services (2-3 minutes)
docker-compose logs -f

# 5. Access at http://localhost
# Login: admin@taskhub.com / admin
```

**That's it!** 🎉

---

## Option 2: Cloud Deployment (5 minutes)

### Railway.app (Easiest - FREE)

1. **Push to GitHub**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin <your-github-repo>
   git push -u origin main
   ```

2. **Deploy on Railway**
   - Go to https://railway.app
   - Click "Start a New Project"
   - Select "Deploy from GitHub repo"
   - Select your repository
   - Railway auto-detects Dockerfile
   - Add services: MySQL, Redis
   - Set environment variables from `.env.example`
   - Click "Deploy"

3. **Done!** Railway gives you a URL automatically

---

### DigitalOcean (5 minutes)

```bash
# 1. Install doctl
brew install doctl  # Mac
# OR download from digitalocean.com

# 2. Login
doctl auth init

# 3. Create app
doctl apps create --spec - <<EOF
name: kanban-dashboard
services:
- name: backend
  dockerfile_path: Backend/Dockerfile
  github:
    repo: your-username/dashboard
    branch: main
  envs:
  - key: DB_PASSWORD
    value: your_password
  http_port: 8080
  
- name: frontend
  dockerfile_path: Frontend/Dockerfile
  github:
    repo: your-username/dashboard
    branch: main
  http_port: 80
  routes:
  - path: /

databases:
- name: mysql-db
  engine: MYSQL
  version: "8"
  
- name: redis-cache
  engine: REDIS
  version: "6"
EOF

# 4. Done! Check status
doctl apps list
```

---

### Heroku (Classic)

```bash
# 1. Login
heroku login

# 2. Create app
heroku create kanban-dashboard

# 3. Add database
heroku addons:create jawsdb:kitefin

# 4. Add Redis
heroku addons:create heroku-redis:hobby-dev

# 5. Set environment variables
heroku config:set JWT_SECRET=your-secret-key
heroku config:set SPRING_PROFILES_ACTIVE=prod

# 6. Deploy
git push heroku main

# 7. Open app
heroku open
```

---

### AWS Lightsail (Simple)

1. **Create Lightsail Container Service**
   ```bash
   aws lightsail create-container-service \
     --service-name kanban-dashboard \
     --power small \
     --scale 1
   ```

2. **Push images**
   ```bash
   # Build images
   docker-compose build

   # Tag for Lightsail
   docker tag kanban-backend:latest kanban-backend:latest
   docker tag kanban-frontend:latest kanban-frontend:latest

   # Push to Lightsail
   aws lightsail push-container-image \
     --service-name kanban-dashboard \
     --label backend \
     --image kanban-backend:latest

   aws lightsail push-container-image \
     --service-name kanban-dashboard \
     --label frontend \
     --image kanban-frontend:latest
   ```

3. **Deploy** via Lightsail console

---

## Option 3: One-Click Cloud Platforms

### Render.com
1. Connect GitHub repo
2. Select "Docker" deployment
3. Auto-detects Dockerfile
4. Add MySQL & Redis from marketplace
5. Deploy ✅

### Fly.io
```bash
# Install flyctl
curl -L https://fly.io/install.sh | sh

# Login
fly auth login

# Deploy
fly launch
fly deploy
```

### Google Cloud Run
```bash
# Submit builds
gcloud builds submit --tag gcr.io/PROJECT-ID/kanban-backend Backend/
gcloud builds submit --tag gcr.io/PROJECT-ID/kanban-frontend Frontend/

# Deploy
gcloud run deploy kanban-backend --image gcr.io/PROJECT-ID/kanban-backend
gcloud run deploy kanban-frontend --image gcr.io/PROJECT-ID/kanban-frontend
```

---

## 🔒 Security Checklist (Before Production)

```bash
# 1. Generate strong JWT secret
openssl rand -base64 64

# 2. Update .env file
JWT_SECRET=<paste-generated-secret>
DB_PASSWORD=<strong-password>

# 3. Enable HTTPS (automatic on most platforms)

# 4. Setup firewall rules
# - Allow 80, 443 (HTTP/HTTPS)
# - Block 3306, 6379 (MySQL, Redis) from public

# 5. Regular backups
make backup-db  # Creates backup
```

---

## 📊 Verify Deployment

```bash
# Check backend health
curl https://your-domain.com/api/v1/actuator/health

# Check frontend
curl https://your-domain.com/health

# Response should be: {"status":"UP"}
```

---

## 🎯 Next Steps

1. **Login:** admin@taskhub.com / admin
2. **Change admin password** (recommended)
3. **Create teams and users**
4. **Setup monitoring** (optional)
5. **Configure domain** (optional)

---

## 💰 Cost Estimates

| Platform | Free Tier | Paid (Monthly) |
|----------|-----------|----------------|
| Railway | $5 credit | $5-20 |
| Heroku | Hobby tier | $7-25 |
| DigitalOcean | $200 credit | $12-24 |
| Render | 750 hrs free | $7-25 |
| Fly.io | Generous free | $5-15 |
| AWS Lightsail | 1st month free | $10-20 |
| Google Cloud | $300 credit | $10-30 |

---

## 🆘 Troubleshooting

### Backend won't start
```bash
# Check logs
docker-compose logs backend

# Common fix: increase memory
docker-compose restart backend
```

### Database connection failed
```bash
# Verify MySQL is running
docker-compose ps mysql

# Restart MySQL
docker-compose restart mysql
```

### Frontend shows error
```bash
# Hard refresh browser
Ctrl + Shift + R

# Check API_URL in .env
echo $VITE_API_URL
```

---

## 📞 Support

- **Documentation:** See `README.docker.md`
- **Commands:** See `Makefile` (run `make help`)
- **Issues:** Check logs with `docker-compose logs -f`

---

**Happy Deploying! 🚀**

Choose your platform, follow the steps, and you'll be live in minutes!
