# Makefile for Kanban Dashboard Docker operations

.PHONY: help build up down restart logs clean backup restore

help: ## Show this help message
	@echo "Kanban Dashboard - Docker Commands"
	@echo "=================================="
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'

build: ## Build all Docker images
	docker-compose build

build-no-cache: ## Build all images without cache
	docker-compose build --no-cache

up: ## Start all services
	docker-compose up -d

up-prod: ## Start services in production mode
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

down: ## Stop all services
	docker-compose down

down-v: ## Stop all services and remove volumes (⚠️ deletes data)
	docker-compose down -v

restart: ## Restart all services
	docker-compose restart

restart-backend: ## Restart backend service
	docker-compose restart backend

restart-frontend: ## Restart frontend service
	docker-compose restart frontend

logs: ## View logs from all services
	docker-compose logs -f

logs-backend: ## View backend logs
	docker-compose logs -f backend

logs-frontend: ## View frontend logs
	docker-compose logs -f frontend

logs-db: ## View database logs
	docker-compose logs -f mysql

ps: ## Show running containers
	docker-compose ps

stats: ## Show container resource usage
	docker stats

shell-backend: ## Open shell in backend container
	docker-compose exec backend sh

shell-frontend: ## Open shell in frontend container
	docker-compose exec frontend sh

shell-db: ## Open MySQL shell
	docker-compose exec mysql mysql -u root -p

backup-db: ## Backup database to backups/backup-YYYYMMDD.sql
	@mkdir -p backups
	docker-compose exec -T mysql mysqldump -u root -p${DB_PASSWORD} kanbandb > backups/backup-$(shell date +%Y%m%d).sql
	@echo "✅ Database backed up to backups/backup-$(shell date +%Y%m%d).sql"

restore-db: ## Restore database from latest backup
	@FILE=$$(ls -t backups/*.sql | head -1); \
	docker-compose exec -T mysql mysql -u root -p${DB_PASSWORD} kanbandb < $$FILE; \
	echo "✅ Database restored from $$FILE"

clean: ## Remove all containers, volumes, and images
	docker-compose down -v --rmi all

prune: ## Clean up Docker system (unused containers, networks, images)
	docker system prune -af

health: ## Check health of all services
	@echo "Backend:  $$(curl -sf http://localhost:8080/api/v1/actuator/health | jq -r '.status' 2>/dev/null || echo 'NOT_READY')"
	@echo "Frontend: $$(curl -sf http://localhost/health 2>/dev/null && echo 'HEALTHY' || echo 'NOT_READY')"
	@echo "MySQL:    $$(docker-compose exec mysql mysqladmin -u root -p${DB_PASSWORD} ping 2>/dev/null | grep -q 'alive' && echo 'HEALTHY' || echo 'NOT_READY')"
	@echo "Redis:    $$(docker-compose exec redis redis-cli ping 2>/dev/null || echo 'NOT_READY')"

dev: ## Start in development mode
	docker-compose up

prod: ## Deploy in production mode
	./deploy.sh production

test-backend: ## Run backend tests
	docker-compose exec backend mvn test

lint-backend: ## Run backend linter
	docker-compose exec backend mvn checkstyle:check

test-frontend: ## Run frontend tests
	docker-compose exec frontend npm test

lint-frontend: ## Run frontend linter
	docker-compose exec frontend npm run lint
