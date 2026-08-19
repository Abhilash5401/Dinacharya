# 📍 Task Import Feature - UI Location Guide

## 🎯 Where to Find It

The **Task Import** feature has been integrated into the **Task Management** page.

---

## 📂 Page Location

**Path:** `/tasks` or Task Management page

**File:** `frontend/src/pages/TaskManagement.tsx`

---

## 🖼️ UI Layout

```
┌─────────────────────────────────────────────────────────────────┐
│  TASK MANAGEMENT                                     Moderator    │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌─────┐ │
│  │   Pending    │  │ In Progress  │  │  Completed   │  │Over │ │
│  │      25      │  │      12      │  │      30      │  │  3  │ │
│  └──────────────┘  └──────────────┘  └──────────────┘  └─────┘ │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ 📥 IMPORT TASKS                                             ││
│  │                                                             ││
│  │  ┌────────────────┐  ┌────────────────┐                   ││
│  │  │ 📤 Import Tasks│  │ ⬇️  Template   │  Supported: .xlsx││
│  │  └────────────────┘  └────────────────┘                   ││
│  │                                                             ││
│  │  ┌───────────────────────────────────────────────────────┐││
│  │  │                                                        │││
│  │  │        📊            📄                                │││
│  │  │   (Excel Icon)  (Word Icon)                          │││
│  │  │                                                        │││
│  │  │     Drag & drop or click to upload                    │││
│  │  │     Excel (.xlsx) or Word (.docx) files               │││
│  │  │                                                        │││
│  │  └───────────────────────────────────────────────────────┘││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                   │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ ➕ ADD NEW TASK                                             ││
│  │  [Manual task creation form below...]                      ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                   │
│  [Task filters and list below...]                                │
└───────────────────────────────────────────────────────────────────┘
```

---

## 🎨 Visual Elements

### Import Section
Located **between** the stats cards and the "Add New Task" form.

**Components:**
1. **Import Button** 
   - Blue primary button with upload icon
   - Shows "Importing..." when processing

2. **Template Button**
   - Outlined button with download icon
   - Downloads CSV template file

3. **Drag & Drop Zone**
   - Large dashed border area
   - Shows Excel and Word icons
   - Changes color when dragging file over
   - Clickable to open file browser

---

## 🔄 User Flow

### Step 1: Navigate to Page
```
Sidebar → "Task Management" or "/tasks" URL
```

### Step 2: Find Import Section
```
Scroll to find "Import Tasks" section
(Located after stats, before "Add New Task")
```

### Step 3: Upload File
**Option A - Button:**
```
Click "Import Tasks" button → Select .xlsx or .docx file
```

**Option B - Drag & Drop:**
```
Drag file from desktop → Drop on the dashed zone
```

**Option C - Template:**
```
Click "Template" button → Download CSV → Edit in Excel → Upload
```

### Step 4: View Results
```
Modal appears showing:
- Total rows processed
- Success count (green)
- Failure count (red)
- Error messages (if any)
- List of imported tasks
```

### Step 5: Refresh
```
Close modal → Page refreshes automatically → See new tasks in list
```

---

## 📱 Responsive Behavior

### Desktop (>768px)
- Import section spans full width
- Buttons displayed side-by-side
- Large drag & drop area

### Tablet (768px - 1024px)
- Import section adjusts width
- Buttons stay side-by-side
- Slightly smaller drag area

### Mobile (<768px)
- Buttons stack vertically
- Drag & drop area height reduced
- Modal fills screen with padding

---

## 🎯 Feature Visibility

### When Visible
✅ User is logged in
✅ At least one team exists
✅ On Task Management page (`/tasks`)

### When Hidden
❌ No teams created yet (shows "Create team first" message)
❌ User not logged in
❌ On other pages

---

## 🖱️ Interactive Elements

### 1. Import Tasks Button
```
State: Normal → Hover → Click → Loading
Color: Blue → Darker Blue → Disabled Gray
Cursor: Pointer → Pointer → Not-allowed
```

### 2. Template Button
```
State: Normal → Hover → Click
Color: Outlined → Filled → Flash
Action: Download CSV template
```

### 3. Drag & Drop Zone
```
State: Normal → Drag Enter → Drop
Border: Gray Dashed → Blue Solid → Process
Background: Light Gray → Light Blue → White
```

### 4. Result Modal
```
Backdrop: Black 50% opacity
Position: Center screen
Animation: Fade in + Slide up
Close: Click X or "Close" button
```

---

## 🎨 Design Integration

### Theme
Follows **Synchrony** design system (DESIGN.md)

### Colors
- **Primary:** Blue (#3b82f6)
- **Success:** Green (#10b981)
- **Error:** Red (#ef4444)
- **Warning:** Yellow (#f59e0b)
- **Neutral:** Gray (#6b7280)

### Typography
- **Title:** font-display, text-2xl
- **Body:** Default font, text-base
- **Labels:** text-sm, charcoal-muted

### Spacing
- **Section padding:** p-6
- **Element gaps:** gap-3 to gap-4
- **Modal margins:** mx-4

---

## 📋 Example Screenshots

### Import Section (Empty State)
```
┌─────────────────────────────────────────────┐
│ Import Tasks                                │
│                                             │
│ [📤 Import Tasks] [⬇️ Template] (.xlsx...)  │
│                                             │
│ ┌─────────────────────────────────────────┐│
│ │         📊        📄                    ││
│ │   Drag & drop or click to upload       ││
│ │   Excel (.xlsx) or Word (.docx) files  ││
│ └─────────────────────────────────────────┘│
└─────────────────────────────────────────────┘
```

### Import Section (Uploading)
```
┌─────────────────────────────────────────────┐
│ Import Tasks                                │
│                                             │
│ [⏳ Importing...] [⬇️ Template]             │
│                                             │
│ ┌─────────────────────────────────────────┐│
│ │         📊        📄                    ││
│ │      Processing file...                ││
│ │   (Grayed out, disabled)               ││
│ └─────────────────────────────────────────┘│
└─────────────────────────────────────────────┘
```

### Result Modal (Success)
```
┌───────────────────────────────────────────────┐
│ ✅ Import Complete                        [X]│
│ Import completed: 8 succeeded, 2 failed      │
├───────────────────────────────────────────────┤
│      10            8             2            │
│  Total Rows    Succeeded      Failed          │
├───────────────────────────────────────────────┤
│ ⚠️ Errors (2)                                 │
│ • Row 3: Title is required                   │
│ • Row 7: Assignee not found                  │
│                                               │
│ ✅ Successfully Imported (8)                  │
│ ┌─────────────────────────────────────────┐ │
│ │ Implement login                    HIGH  │ │
│ │ JWT-based authentication           TODO  │ │
│ └─────────────────────────────────────────┘ │
│ ... and 7 more tasks                         │
├───────────────────────────────────────────────┤
│                            [Close]            │
└───────────────────────────────────────────────┘
```

---

## 🔗 Related Pages

### Where to Use Import

1. **Task Management** (`/tasks`) ✅ **INTEGRATED HERE**
   - Bulk task creation
   - Import project plans

2. **Team Board** (`/teams/:id/board`)
   - Could be added in future
   - Import team-specific tasks

3. **Dashboard** (`/dashboard`)
   - Could add quick import
   - Currently not integrated

---

## 🛠️ Developer Notes

### Component Import
```typescript
import TaskImport from '@/components/TaskImport';
```

### Usage
```tsx
<TaskImport 
  teamId={selectedTeamId} 
  onImportSuccess={(result) => {
    toast.success(`Imported ${result.successCount} tasks!`);
    // Refresh task list
    refetch();
  }}
/>
```

### Props
```typescript
interface TaskImportProps {
  teamId: string;              // Required
  onImportSuccess?: (result: TaskImportResponse) => void;  // Optional
}
```

---

## ✅ Quick Test

1. **Navigate:** Go to `/tasks` page
2. **Look for:** "Import Tasks" section (below stats, above "Add New Task")
3. **Click:** "Template" button → Downloads CSV
4. **Open:** In Excel → See sample data
5. **Modify:** Add your own tasks
6. **Save:** As `.xlsx` format
7. **Upload:** Drag file to import zone OR click "Import Tasks"
8. **View:** Modal shows results
9. **Check:** New tasks appear in task list

---

## 🎉 You're Ready!

The import feature is now **fully integrated** and ready to use on the Task Management page!

**Location:** `/tasks` → "Import Tasks" section (between stats and form)

**Actions Available:**
- 📤 Import Excel files (.xlsx)
- 📄 Import Word files (.docx)
- ⬇️ Download template
- 🖱️ Drag & drop upload
- 📊 View detailed results

---

**Need Help?** Check [TASK_IMPORT_GUIDE.md](./TASK_IMPORT_GUIDE.md) for detailed usage instructions!
