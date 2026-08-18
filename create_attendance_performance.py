#!/usr/bin/env python3
"""
Create attendance records and trigger performance snapshots
Populates the Performance Dashboard and Attendance Dashboard with data
"""

import requests
import json
import random
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8080/api/v1"
ADMIN_EMAIL = "admin@taskhub.com"
ADMIN_PASSWORD = "admin"

# Correct AttendanceStatus values from backend
ATTENDANCE_STATUSES = ["ONLINE", "ON_BREAK", "OFFLINE"]
# Weighted for realistic distribution (mostly online/working)
STATUS_WEIGHTS = [0.75, 0.15, 0.10]

def login(email, password):
    """Login and get JWT token"""
    login_data = {"email": email, "password": password}
    response = requests.post(f"{BASE_URL}/auth/login", json=login_data)
    if response.status_code == 200:
        return response.json().get("accessToken")
    return None

def get_users(token):
    """Get all users"""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(f"{BASE_URL}/users?page=0&size=100", headers=headers)
    if response.status_code == 200:
        return response.json().get('content', [])
    return []

def create_attendance(token, user_id, work_date, entry_time, exit_time, status, breaks=None):
    """Create an attendance record with the correct payload"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    payload = {
        "userId": user_id,
        "workDate": work_date,
        "entryTime": entry_time,
        "exitTime": exit_time,
        "status": status
    }
    
    if breaks:
        payload["breaks"] = breaks
    
    response = requests.post(
        f"{BASE_URL}/moderator/attendance",
        json=payload,
        headers=headers
    )
    
    if response.status_code not in [200, 201]:
        # Debug first error
        if not hasattr(create_attendance, '_shown_error'):
            print(f"   ⚠️  Sample error: {response.status_code} - {response.text[:200]}")
            create_attendance._shown_error = True
    
    return response.status_code in [200, 201]

def compute_performance(token):
    """Trigger performance computation"""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.post(f"{BASE_URL}/analytics/performance/compute", headers=headers)
    return response.status_code in [200, 201, 204]

def generate_work_times(base_date, status):
    """Generate realistic entry/exit times"""
    if status == "OFFLINE":
        # Person was offline all day (absent)
        entry_hour = 9
        entry_min = 0
        exit_hour = 9
        exit_min = 0
    else:
        # Normal work day for ONLINE and ON_BREAK
        entry_hour = random.randint(8, 10)
        entry_min = random.randint(0, 59)
        exit_hour = random.randint(17, 20)
        exit_min = random.randint(0, 59)
    
    entry = base_date.replace(hour=entry_hour, minute=entry_min, second=0, microsecond=0)
    exit_time = base_date.replace(hour=exit_hour, minute=exit_min, second=0, microsecond=0)
    
    return (
        entry.strftime("%Y-%m-%dT%H:%M:%S"),
        exit_time.strftime("%Y-%m-%dT%H:%M:%S")
    )

def generate_breaks(base_date, num_breaks=None):
    """Generate realistic break times"""
    if num_breaks is None:
        num_breaks = random.randint(1, 3)
    
    breaks = []
    # Lunch break around 12-1
    lunch_start = base_date.replace(hour=12, minute=random.randint(0, 30), second=0, microsecond=0)
    lunch_end = lunch_start + timedelta(minutes=random.randint(30, 60))
    breaks.append({
        "startTime": lunch_start.strftime("%Y-%m-%dT%H:%M:%S"),
        "endTime": lunch_end.strftime("%Y-%m-%dT%H:%M:%S"),
        "reason": "Lunch"
    })
    
    # Optional coffee breaks
    if num_breaks > 1:
        coffee_start = base_date.replace(hour=random.choice([10, 15]), minute=random.randint(0, 45), second=0, microsecond=0)
        coffee_end = coffee_start + timedelta(minutes=random.randint(10, 20))
        breaks.append({
            "startTime": coffee_start.strftime("%Y-%m-%dT%H:%M:%S"),
            "endTime": coffee_end.strftime("%Y-%m-%dT%H:%M:%S"),
            "reason": "Coffee break"
        })
    
    return breaks

def main():
    print("🚀 Creating attendance & performance data...")
    print("=" * 60)
    
    # Login as admin
    token = login(ADMIN_EMAIL, ADMIN_PASSWORD)
    if not token:
        print("❌ Failed to login as admin")
        return
    print("✅ Logged in as admin")
    
    # Get all users
    users = get_users(token)
    regular_users = [u for u in users if u.get('email') != ADMIN_EMAIL]
    print(f"👥 Found {len(regular_users)} users")
    
    if not regular_users:
        print("❌ No users found")
        return
    
    # Create attendance records for the last 60 days
    print(f"\n📅 Creating attendance records for last 60 days (weekdays only)...")
    today = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
    
    total_records = 0
    status_counts = {status: 0 for status in ATTENDANCE_STATUSES}
    
    for days_back in range(60, 0, -1):
        current_date = today - timedelta(days=days_back)
        
        # Skip weekends (Saturday=5, Sunday=6)
        if current_date.weekday() >= 5:
            continue
        
        work_date = current_date.strftime("%Y-%m-%d")
        
        for user in regular_users:
            # Random status based on weights
            status = random.choices(ATTENDANCE_STATUSES, weights=STATUS_WEIGHTS)[0]
            
            # Generate times
            entry_time, exit_time = generate_work_times(current_date, status)
            
            # Generate breaks (only if working)
            breaks = None
            if status != "OFFLINE":
                breaks = generate_breaks(current_date)
            
            if create_attendance(token, user['id'], work_date, entry_time, exit_time, status, breaks):
                total_records += 1
                status_counts[status] += 1
        
        if days_back % 10 == 0:
            print(f"   Processed {60 - days_back + 1} days, {total_records} records created...")
    
    # Trigger performance computation
    print(f"\n⚙️  Computing performance snapshots...")
    if compute_performance(token):
        print("   ✅ Performance snapshots computed successfully")
    else:
        print("   ⚠️  Performance computation may not be available")
    
    # Summary
    print("\n" + "=" * 60)
    print(f"🎉 Successfully created {total_records} attendance records!")
    print("\n📊 Attendance Status Distribution:")
    total = sum(status_counts.values())
    for status, count in sorted(status_counts.items(), key=lambda x: x[1], reverse=True):
        if total > 0:
            percentage = (count / total * 100)
            bar = "█" * int(percentage / 2)
            print(f"   {status:12s}: {count:4d} ({percentage:5.1f}%) {bar}")
    
    print("\n📈 Dashboards Now Populated:")
    print("   ✅ Employee Attendance Dashboard")
    print("      - 60 days of attendance history")
    print("      - Entry/Exit time tracking")
    print("      - Break time analysis")
    print("      - Online/Break/Offline distribution")
    print("      - Working hours calculation")
    print("")
    print("   ✅ Employee Performance Dashboard")
    print("      - Performance scores per employee")
    print("      - Trend graphs over months")
    print("      - Productivity metrics")
    print("      - Task completion vs productivity")
    
    print("\n🌐 View dashboards at:")
    print("   Login: admin@taskhub.com / admin")
    print("   Refresh browser: Ctrl+Shift+R")

if __name__ == "__main__":
    main()
