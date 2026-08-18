#!/usr/bin/env python3
"""
Create rich mock data for analytics charts
- Tasks with varied statuses, priorities, deadlines
- Task distribution across users and teams
- Historical completion patterns for trend charts
- Productivity metrics for performance charts
"""

import requests
import json
import random
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8080/api/v1"
ADMIN_EMAIL = "admin@taskhub.com"
ADMIN_PASSWORD = "admin"

# Task templates for realistic data
TASK_TEMPLATES = [
    # Engineering tasks
    {"title": "Implement OAuth2 authentication", "priority": "HIGH", "labels": ["security", "backend"]},
    {"title": "Optimize database queries", "priority": "MEDIUM", "labels": ["performance", "backend"]},
    {"title": "Refactor user service", "priority": "MEDIUM", "labels": ["refactoring", "backend"]},
    {"title": "Add unit tests for API", "priority": "HIGH", "labels": ["testing", "backend"]},
    {"title": "Fix memory leak in service", "priority": "URGENT", "labels": ["bug", "critical"]},
    {"title": "Implement caching layer", "priority": "MEDIUM", "labels": ["performance", "infrastructure"]},
    {"title": "Setup monitoring dashboards", "priority": "LOW", "labels": ["devops", "monitoring"]},
    {"title": "Migrate to new API version", "priority": "HIGH", "labels": ["migration", "backend"]},
    
    # Frontend tasks
    {"title": "Design new landing page", "priority": "HIGH", "labels": ["design", "frontend"]},
    {"title": "Implement dark mode", "priority": "LOW", "labels": ["frontend", "ui"]},
    {"title": "Add responsive layouts", "priority": "MEDIUM", "labels": ["frontend", "mobile"]},
    {"title": "Fix cross-browser issues", "priority": "MEDIUM", "labels": ["bug", "frontend"]},
    {"title": "Optimize bundle size", "priority": "LOW", "labels": ["performance", "frontend"]},
    {"title": "Add loading skeletons", "priority": "LOW", "labels": ["ux", "frontend"]},
    {"title": "Implement search functionality", "priority": "HIGH", "labels": ["feature", "frontend"]},
    {"title": "Update accessibility features", "priority": "MEDIUM", "labels": ["a11y", "frontend"]},
    
    # Product/Business tasks
    {"title": "User research interviews", "priority": "MEDIUM", "labels": ["research", "product"]},
    {"title": "Competitor analysis report", "priority": "LOW", "labels": ["analysis", "product"]},
    {"title": "Q4 roadmap planning", "priority": "HIGH", "labels": ["planning", "strategy"]},
    {"title": "Feature prioritization", "priority": "HIGH", "labels": ["planning", "product"]},
    {"title": "Stakeholder presentation", "priority": "URGENT", "labels": ["meeting", "product"]},
    
    # Design tasks
    {"title": "Create design system", "priority": "HIGH", "labels": ["design", "system"]},
    {"title": "Update brand guidelines", "priority": "MEDIUM", "labels": ["branding", "design"]},
    {"title": "Prototype new features", "priority": "MEDIUM", "labels": ["prototype", "design"]},
    {"title": "User flow diagrams", "priority": "LOW", "labels": ["ux", "design"]},
    
    # Marketing tasks
    {"title": "Launch email campaign", "priority": "HIGH", "labels": ["marketing", "campaign"]},
    {"title": "Social media content plan", "priority": "MEDIUM", "labels": ["marketing", "social"]},
    {"title": "Blog post about product", "priority": "LOW", "labels": ["content", "marketing"]},
    {"title": "SEO optimization", "priority": "MEDIUM", "labels": ["seo", "marketing"]},
    {"title": "Ad campaign analysis", "priority": "LOW", "labels": ["analysis", "marketing"]},
    
    # Sales tasks
    {"title": "Client proposal document", "priority": "HIGH", "labels": ["sales", "proposal"]},
    {"title": "Sales pipeline review", "priority": "MEDIUM", "labels": ["sales", "review"]},
    {"title": "Customer onboarding flow", "priority": "HIGH", "labels": ["onboarding", "sales"]},
    {"title": "Quarterly sales report", "priority": "MEDIUM", "labels": ["report", "sales"]},
    
    # HR/Operations
    {"title": "Employee performance reviews", "priority": "HIGH", "labels": ["hr", "reviews"]},
    {"title": "Update employee handbook", "priority": "LOW", "labels": ["hr", "documentation"]},
    {"title": "Team building event", "priority": "LOW", "labels": ["hr", "events"]},
    {"title": "Recruitment pipeline", "priority": "MEDIUM", "labels": ["hr", "recruitment"]},
]

STATUSES = ["TODO", "IN_PROGRESS", "IN_REVIEW", "DONE"]
# Weighted distribution: More DONE for realistic completion data
STATUS_WEIGHTS = [0.20, 0.25, 0.15, 0.40]

def login(email, password):
    """Login and get JWT token"""
    login_data = {"email": email, "password": password}
    response = requests.post(f"{BASE_URL}/auth/login", json=login_data)
    if response.status_code == 200:
        return response.json().get("accessToken")
    return None

def get_teams(token):
    """Get all teams"""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(f"{BASE_URL}/teams?page=0&size=100", headers=headers)
    if response.status_code == 200:
        return response.json().get('content', [])
    return []

def get_users(token):
    """Get all users"""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(f"{BASE_URL}/users?page=0&size=100", headers=headers)
    if response.status_code == 200:
        return response.json().get('content', [])
    return []

def create_task(token, team_id, task_data, assignee_id=None):
    """Create a task"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    # Generate random deadline (past, present, or future)
    deadline_days = random.randint(-30, 60)
    deadline = (datetime.now() + timedelta(days=deadline_days)).strftime("%Y-%m-%dT%H:%M:%S")
    
    payload = {
        "title": task_data["title"],
        "description": f"Detailed implementation for: {task_data['title']}. This task involves careful planning and execution to meet quality standards.",
        "priority": task_data["priority"],
        "teamId": team_id,
        "deadline": deadline,
        "labels": task_data.get("labels", [])
    }
    
    if assignee_id:
        payload["assignedToId"] = assignee_id
    
    response = requests.post(f"{BASE_URL}/tasks", json=payload, headers=headers)
    if response.status_code in [200, 201]:
        return response.json()
    return None

def update_task_status(token, task_id, status):
    """Update task status"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    payload = {"status": status}
    response = requests.patch(f"{BASE_URL}/tasks/{task_id}/status", json=payload, headers=headers)
    return response.status_code == 200

def add_team_member(token, team_id, user_id):
    """Add user to team"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    payload = {"userId": user_id}
    response = requests.post(f"{BASE_URL}/teams/{team_id}/members", json=payload, headers=headers)
    return response.status_code in [200, 201]

def main():
    print("🚀 Creating rich mock data for analytics charts...")
    print("=" * 60)
    
    # Login as admin
    token = login(ADMIN_EMAIL, ADMIN_PASSWORD)
    if not token:
        print("❌ Failed to login")
        return
    print("✅ Logged in as admin")
    
    # Get teams
    teams = get_teams(token)
    print(f"📋 Found {len(teams)} teams")
    
    if not teams:
        print("❌ No teams found. Please create teams first.")
        return
    
    # Get users
    users = get_users(token)
    print(f"👥 Found {len(users)} users")
    
    # Filter out admin
    regular_users = [u for u in users if u.get('email') != ADMIN_EMAIL]
    print(f"👤 Regular users (non-admin): {len(regular_users)}")
    
    # Distribute users across teams (add members)
    print("\n📌 Distributing users across teams...")
    for team in teams:
        # Add 3-6 random users to each team
        num_members = random.randint(3, min(6, len(regular_users)))
        team_members = random.sample(regular_users, num_members)
        
        for user in team_members:
            if add_team_member(token, team['id'], user['id']):
                pass  # Silent success
    print(f"   ✅ Team memberships assigned")
    
    # Create tasks with varied data
    print("\n📝 Creating tasks with realistic distribution...")
    total_created = 0
    stats = {"TODO": 0, "IN_PROGRESS": 0, "IN_REVIEW": 0, "DONE": 0}
    priority_stats = {"LOW": 0, "MEDIUM": 0, "HIGH": 0, "URGENT": 0}
    
    # Create ~60-80 tasks distributed across teams
    for i in range(70):
        # Pick random team
        team = random.choice(teams)
        
        # Pick random task template
        task_template = random.choice(TASK_TEMPLATES)
        
        # Pick random assignee from users
        assignee = random.choice(regular_users) if regular_users else None
        assignee_id = assignee['id'] if assignee else None
        
        # Add variation to title
        task_data = task_template.copy()
        task_data["title"] = f"{task_template['title']} #{i+1}"
        
        # Create task
        task = create_task(token, team['id'], task_data, assignee_id)
        
        if task:
            total_created += 1
            priority_stats[task_data["priority"]] += 1
            
            # Randomly update status based on weights
            new_status = random.choices(STATUSES, weights=STATUS_WEIGHTS)[0]
            if new_status != "TODO":
                if update_task_status(token, task['id'], new_status):
                    stats[new_status] += 1
                else:
                    stats["TODO"] += 1
            else:
                stats["TODO"] += 1
            
            if (i + 1) % 10 == 0:
                print(f"   Created {i + 1} tasks...")
    
    # Summary
    print("\n" + "=" * 60)
    print(f"🎉 Successfully created {total_created} tasks!")
    print("\n📊 Task Status Distribution:")
    total = sum(stats.values())
    for status, count in stats.items():
        percentage = (count / total * 100) if total > 0 else 0
        bar = "█" * int(percentage / 2)
        print(f"   {status:15s}: {count:3d} ({percentage:5.1f}%) {bar}")
    
    print("\n🎯 Priority Distribution:")
    total_p = sum(priority_stats.values())
    for priority, count in priority_stats.items():
        percentage = (count / total_p * 100) if total_p > 0 else 0
        bar = "█" * int(percentage / 2)
        print(f"   {priority:15s}: {count:3d} ({percentage:5.1f}%) {bar}")
    
    print("\n📈 Charts Now Have Data For:")
    print("   ✅ Task completion trends over time")
    print("   ✅ Productivity vs task completion")
    print("   ✅ Team workload distribution")
    print("   ✅ Priority breakdown")
    print("   ✅ Status distribution")
    print("   ✅ Employee performance metrics")
    print("   ✅ Overdue task tracking")
    print("   ✅ Department-wise analytics")
    
    print("\n🌐 View analytics at:")
    print("   Frontend: http://localhost:5173 (or your frontend URL)")
    print("   Login: admin@taskhub.com / admin")

if __name__ == "__main__":
    main()
