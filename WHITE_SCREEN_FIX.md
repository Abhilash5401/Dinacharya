# 🔧 White Screen Fix Guide

## 🐛 Problem
Getting a complete white screen when loading the application.

---

## ✅ Temporary Fix Applied

**I've disabled the TaskImport component** to get your app working again.

The import feature is commented out in `TaskManagement.tsx`:
- Line 9: `// import TaskImport...` (commented)
- Lines 327-343: Import section commented out

**Your app should work normally now** (without the import feature).

---

## 🔍 How to Debug

###1. Open Browser Console

Press `F12` in your browser and check the Console tab for errors.

**Common errors:**
```
Uncaught SyntaxError: ...
Uncaught ReferenceError: ...
Cannot read property '...' of undefined
```

### 2. Check Network Tab

Look for failed requests (red status codes):
- 404: File not found
- 500: Server error
- CORS errors

### 3. Check React DevTools

Install React DevTools extension and check if components are rendering.

---

## 🎯 Steps to Fix White Screen

### Step 1: Start Dev Server
```bash
cd d:\Projects\Dashboard\frontend
npm run dev
```

### Step 2: Open Browser
```
http://localhost:5173
```

### Step 3: Check Console
Press `F12` → Console tab

**Share the error message with me** and I can help fix it!

---

## 🔄 To Re-enable Import Feature

Once the white screen is fixed, uncomment these lines in `TaskManagement.tsx`:

**Line 9:**
```typescript
// Remove the comment:
import TaskImport from '@/components/TaskImport';
```

**Lines 327-343:**
```typescript
// Remove the comment block:
{teams.length > 0 && selectedTeamId && (
  <section className="tms-panel">
    <h2 className="tms-panel-title">Import Tasks</h2>
    <TaskImport 
      teamId={selectedTeamId} 
      onImportSuccess={(result) => {
        toast.success(`Successfully imported ${result.successCount} task(s)!`);
        // ...
      }}
    />
  </section>
)}
```

---

## 🚨 Common White Screen Causes

### 1. JavaScript Syntax Error
**Solution:** Check browser console for syntax errors

### 2. Import Path Error  
**Solution:** Verify all import paths are correct

### 3. Missing Dependency
**Solution:**
```bash
npm install
```

### 4. Build Cache Issue
**Solution:**
```bash
rm -rf node_modules/.vite
npm run build
```

### 5. Port Already in Use
**Solution:**
```bash
# Kill process on port 5173
netstat -ano | findstr :5173
taskkill /PID <PID_NUMBER> /F
```

### 6. Environment Variables
**Solution:** Check if `.env` file exists with:
```
VITE_API_URL=http://localhost:8080/api/v1
```

---

## 📋 Debugging Checklist

- [ ] Browser console shows no errors
- [ ] Network tab shows all files loaded (200 OK)
- [ ] Dev server is running (`npm run dev`)
- [ ] Port 5173 is accessible
- [ ] Backend is running on port 8080
- [ ] No CORS errors in console
- [ ] React DevTools shows components

---

## 🔧 Quick Fixes to Try

### Fix 1: Clear Cache
```bash
# Clear browser cache
Ctrl + Shift + Delete

# Clear Vite cache
cd frontend
rm -rf node_modules/.vite
rm -rf dist
npm run build
```

### Fix 2: Reinstall Dependencies
```bash
cd frontend
rm -rf node_modules
rm package-lock.json
npm install
npm run dev
```

### Fix 3: Check Backend
```bash
# Make sure backend is running
cd d:\Projects\Dashboard
mvn spring-boot:run
```

### Fix 4: Check Environment
```bash
# frontend/.env should have:
VITE_API_URL=http://localhost:8080/api/v1
```

---

## 📞 Need Help?

**Share with me:**
1. Screenshot of browser console (F12)
2. Any error messages
3. Which page shows white screen (login, dashboard, tasks, etc.)
4. Does `/login` page work?

Then I can provide a specific fix!

---

## ✅ Current Status

**App Status:** ✅ Should work (import feature disabled)  
**Import Feature:** ❌ Temporarily disabled  
**Build:** ✅ Succeeds  

**To test:**
```bash
cd d:\Projects\Dashboard\frontend
npm run dev
```

Then open: `http://localhost:5173`

If you see the login page, the white screen is fixed!

---

## 🎯 Next Steps

1. **Test basic app** - Can you see the login page?
2. **Share console errors** - Open F12, share any red errors
3. **I'll fix the import** - Once we know the root cause
4. **Re-enable import** - Uncomment the feature

**The app should work now without the import feature!** 🎉
