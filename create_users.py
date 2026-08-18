#!/usr/bin/env python3
"""
Create 20 mock user profiles with different departments
"""

import requests
import json
import random

# Configuration
BASE_URL = "http://localhost:8080/api/v1"

# Mock user data
DEPARTMENTS = [
    "Engineering", "Product Management", "Design", "Marketing", 
    "Sales", "Customer Support", "Human Resources", "Finance",
    "Operations", "Legal", "Data Science", "Security"
]

FIRST_NAMES = [
    "Emma", "Liam", "Olivia", "Noah", "Ava", "Ethan", "Sophia", "Mason",
    "Isabella", "William", "Mia", "James", "Charlotte", "Benjamin", "Amelia",
    "Lucas", "Harper", "Henry", "Evelyn", "Alexander", "Abigail", "Michael",
    "Emily", "Daniel", "Elizabeth", "Matthew", "Sofia", "Jackson", "Avery", "David"
]

LAST_NAMES = [
    "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
    "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson",
    "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Thompson", "White",
    "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson", "Walker"
]

SKILLS = [
    "JavaScript", "Python", "Java", "React", "Angular", "Vue.js", "Node.js",
    "TypeScript", "SQL", "MongoDB", "AWS", "Docker", "Kubernetes", "Git",
    "Agile", "Scrum", "Project Management", "UI/UX Design", "Figma", "Sketch",
    "Data Analysis", "Machine Learning", "DevOps", "CI/CD", "Testing", "Security",
    "Leadership", "Communication", "Problem Solving", "Team Collaboration"
]

BIOS = [
    "Passionate about building scalable solutions and mentoring teams.",
    "Full-stack developer with 5+ years of experience in modern web technologies.",
    "Creative problem solver focused on user experience and design thinking.",
    "Data-driven professional with expertise in analytics and business intelligence.",
    "Strategic thinker with a track record of delivering high-impact projects.",
    "Experienced leader dedicated to fostering collaborative team environments.",
    "Detail-oriented specialist committed to quality and continuous improvement.",
    "Innovative technologist exploring cutting-edge solutions and best practices.",
    "Customer-focused professional with strong communication and interpersonal skills.",
    "Results-driven expert passionate about operational excellence and efficiency."
]

def register_user(email, password, name, department):
    """Register a new user"""
    user_data = {
        "email": email,
        "password": password,
        "name": name,
        "department": department
    }
    
    try:
        response = requests.post(f"{BASE_URL}/auth/register", json=user_data)
        if response.status_code == 201 or response.status_code == 200:
            print(f"✅ Created user: {name} ({department})")
            return response.json()
        else:
            print(f"⚠️  User {email} might already exist or error: {response.status_code}")
            return None
    except Exception as e:
        print(f"❌ Failed to create user {name}: {e}")
        return None

def update_user_profile(token, user_id, bio, skills):
    """Update user profile with bio and skills"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    profile_data = {
        "bio": bio,
        "skills": skills
    }
    
    try:
        response = requests.put(f"{BASE_URL}/users/{user_id}", json=profile_data, headers=headers)
        if response.status_code == 200:
            return True
        else:
            print(f"⚠️  Failed to update profile for user {user_id}: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ Error updating profile: {e}")
        return False

def login(email, password):
    """Login and get token"""
    login_data = {
        "email": email,
        "password": password
    }
    
    try:
        response = requests.post(f"{BASE_URL}/auth/login", json=login_data)
        if response.status_code == 200:
            return response.json().get("accessToken")
        return None
    except:
        return None

def main():
    print("🚀 Creating 20 mock user profiles...")
    print("=" * 60)
    
    created_users = []
    
    # Create 20 users
    for i in range(20):
        first_name = random.choice(FIRST_NAMES)
        last_name = random.choice(LAST_NAMES)
        name = f"{first_name} {last_name}"
        email = f"{first_name.lower()}.{last_name.lower()}{i}@company.com"
        password = "password123"
        department = random.choice(DEPARTMENTS)
        
        # Register user
        user_data = register_user(email, password, name, department)
        
        if user_data:
            # Login as the user to get their token
            token = login(email, password)
            
            if token:
                # Get user ID from the response
                user_id = user_data.get('user', {}).get('id') or user_data.get('id')
                
                if user_id:
                    # Add bio and skills
                    bio = random.choice(BIOS)
                    user_skills = random.sample(SKILLS, k=random.randint(3, 7))
                    
                    if update_user_profile(token, user_id, bio, user_skills):
                        print(f"   📝 Updated profile with {len(user_skills)} skills")
                    
                    created_users.append({
                        "name": name,
                        "email": email,
                        "department": department,
                        "skills": user_skills
                    })
    
    print("\n" + "=" * 60)
    print(f"🎉 Successfully created {len(created_users)} users!")
    print("\n📊 Department Distribution:")
    
    # Count users per department
    dept_count = {}
    for user in created_users:
        dept = user['department']
        dept_count[dept] = dept_count.get(dept, 0) + 1
    
    for dept, count in sorted(dept_count.items(), key=lambda x: x[1], reverse=True):
        print(f"   {dept}: {count} users")
    
    print("\n📧 Sample User Credentials:")
    print("   All passwords: password123")
    print("\n   Sample logins:")
    for user in created_users[:5]:
        print(f"   - {user['email']}")
    
    print("\n✅ All users can now login and be assigned to teams and tasks!")
    print(f"🔐 Admin account: admin@taskhub.com / admin")

if __name__ == "__main__":
    main()
