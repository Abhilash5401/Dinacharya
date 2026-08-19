# 📋 Task Import Examples

Complete examples showing how to format your Excel and Word files for import.

---

## 📊 Excel Example (.xlsx)

### Example 1: Basic Task Import

Create an Excel file with these columns in the **first row** (header):

| Title | Description | Status | Priority | Due Date | Assignee Email | Team Name |
|-------|-------------|--------|----------|----------|----------------|-----------|
| Implement OAuth2 | Add Google and GitHub social login | TODO | HIGH | 2024-12-31 | john.doe@company.com | Backend Team |
| Responsive Navigation | Fix mobile menu collapse issue | IN_PROGRESS | MEDIUM | 2024-12-25 | jane.smith@company.com | Frontend Team |
| Database Migration | Upgrade PostgreSQL to version 15 | TODO | CRITICAL | 2024-12-20 | db.admin@company.com | DevOps Team |
| API Rate Limiting | Implement rate limiting middleware | TODO | HIGH | 2024-12-28 | john.doe@company.com | Backend Team |
| Dark Mode Toggle | Add user preference for dark theme | IN_REVIEW | MEDIUM | 2024-12-22 | jane.smith@company.com | Frontend Team |

### Example 2: Minimal (Only Required Fields)

| Title | Description | Status | Priority | Due Date | Assignee Email | Team Name |
|-------|-------------|--------|----------|----------|----------------|-----------|
| Fix login bug | | | | | | |
| Update dependencies | | | | | | |
| Code review | | | | | | |

**Note:** Empty fields will use defaults:
- Status → TODO
- Priority → MEDIUM
- Others → null (unassigned)

---

## 📝 Word Example (.docx)

### Option 1: Table Format (Recommended)

Create a **table** in Word with the same structure as Excel:

```
┌────────────────────┬─────────────────────────┬──────────────┬──────────┬────────────┬─────────────────────┬─────────────┐
│ Title              │ Description             │ Status       │ Priority │ Due Date   │ Assignee Email      │ Team Name   │
├────────────────────┼─────────────────────────┼──────────────┼──────────┼────────────┼─────────────────────┼─────────────┤
│ Implement OAuth2   │ Add social login        │ TODO         │ HIGH     │ 2024-12-31 │ john@company.com    │ Backend     │
├────────────────────┼─────────────────────────┼──────────────┼──────────┼────────────┼─────────────────────┼─────────────┤
│ Responsive Nav     │ Fix mobile menu         │ IN_PROGRESS  │ MEDIUM   │ 2024-12-25 │ jane@company.com    │ Frontend    │
├────────────────────┼─────────────────────────┼──────────────┼──────────┼────────────┼─────────────────────┼─────────────┤
│ Database Migration │ Upgrade PostgreSQL      │ TODO         │ CRITICAL │ 2024-12-20 │ admin@company.com   │ DevOps      │
└────────────────────┴─────────────────────────┴──────────────┴──────────┴────────────┴─────────────────────┴─────────────┘
```

**To create in Word:**
1. Insert → Table → 7 columns
2. Fill header row (bold it)
3. Add task rows below
4. Save as `.docx`

### Option 2: Simple Text Format

Just write each task on a new line with format:  
**Title - Description - Status - Priority - Due Date**

```
Implement OAuth2 - Add Google and GitHub social login - TODO - HIGH - 2024-12-31
Responsive Navigation - Fix mobile menu collapse issue - IN_PROGRESS - MEDIUM - 2024-12-25
Database Migration - Upgrade PostgreSQL to version 15 - TODO - CRITICAL - 2024-12-20
API Rate Limiting - Implement rate limiting middleware - TODO - HIGH - 2024-12-28
Dark Mode Toggle - Add user preference for dark theme - IN_REVIEW - MEDIUM - 2024-12-22
```

**Note:** Simple format doesn't support assignee/team assignment.

---

## 🎯 Real-World Examples

### Example 1: Sprint Planning

Import a full sprint backlog:

| Title | Description | Status | Priority | Due Date | Assignee Email | Team Name |
|-------|-------------|--------|----------|----------|----------------|-----------|
| User Authentication | JWT-based auth with refresh tokens | TODO | CRITICAL | 2024-12-20 | auth.dev@company.com | Security |
| Password Reset Flow | Email-based password reset | TODO | HIGH | 2024-12-22 | auth.dev@company.com | Security |
| Profile Management | CRUD operations for user profiles | TODO | MEDIUM | 2024-12-25 | backend.dev@company.com | Backend |
| Dashboard UI | Main dashboard with charts | TODO | HIGH | 2024-12-23 | ui.dev@company.com | Frontend |
| Notification System | Real-time notifications via WebSocket | TODO | MEDIUM | 2024-12-28 | backend.dev@company.com | Backend |
| Email Templates | Transactional email templates | TODO | LOW | 2024-12-30 | designer@company.com | Design |
| Unit Tests | 80% code coverage | TODO | HIGH | 2024-12-27 | qa@company.com | QA |
| API Documentation | OpenAPI spec for all endpoints | TODO | MEDIUM | 2024-12-29 | tech.writer@company.com | Docs |
| Performance Testing | Load test with 1000 concurrent users | TODO | MEDIUM | 2025-01-02 | qa@company.com | QA |
| Security Audit | OWASP top 10 vulnerability scan | TODO | CRITICAL | 2024-12-26 | security@company.com | Security |

### Example 2: Bug Fixes

Import bugs from issue tracker:

| Title | Description | Status | Priority | Due Date | Assignee Email | Team Name |
|-------|-------------|--------|----------|----------|----------------|-----------|
| BUG-101: Login fails Safari | Users can't login on Safari browser | TODO | HIGH | 2024-12-18 | frontend@company.com | Frontend |
| BUG-102: Memory leak | Server memory grows unbounded | IN_PROGRESS | CRITICAL | 2024-12-17 | backend@company.com | Backend |
| BUG-103: Chart rendering | Bar charts not displaying on mobile | TODO | MEDIUM | 2024-12-20 | frontend@company.com | Frontend |
| BUG-104: API timeout | Payment API timeouts under load | TODO | HIGH | 2024-12-19 | backend@company.com | Backend |
| BUG-105: Email formatting | HTML emails broken in Outlook | TODO | LOW | 2024-12-22 | designer@company.com | Design |

### Example 3: Feature Requests

Import user-requested features:

| Title | Description | Status | Priority | Due Date | Assignee Email | Team Name |
|-------|-------------|--------|----------|----------|----------------|-----------|
| Dark mode support | System-wide dark theme | TODO | HIGH | 2025-01-15 | ui.dev@company.com | Frontend |
| Export to PDF | Export reports as PDF | TODO | MEDIUM | 2025-01-20 | backend.dev@company.com | Backend |
| Keyboard shortcuts | Add hotkeys for common actions | TODO | LOW | 2025-01-30 | ui.dev@company.com | Frontend |
| Bulk operations | Select and update multiple items | TODO | MEDIUM | 2025-01-25 | fullstack@company.com | Full Stack |
| Advanced filters | Complex filtering with AND/OR logic | TODO | HIGH | 2025-01-18 | backend.dev@company.com | Backend |

---

## ✅ Validation Examples

### ✔️ Valid Formats

**Status:**
- `TODO` ✅
- `todo` ✅ (case-insensitive)
- `To Do` ✅ (alias)
- `IN_PROGRESS` ✅
- `In Progress` ✅
- `Working` ✅ (alias)
- `IN_REVIEW` ✅
- `DONE` ✅
- `Complete` ✅ (alias)

**Priority:**
- `LOW` ✅
- `low` ✅
- `MEDIUM` ✅
- `HIGH` ✅
- `high` ✅
- `CRITICAL` ✅
- `Urgent` ✅ (alias for HIGH)

**Date Formats:**
- `2024-12-31` ✅ (ISO format)
- `31/12/2024` ✅ (DD/MM/YYYY)
- `12/31/2024` ✅ (MM/DD/YYYY)
- `31-12-2024` ✅
- Excel date cells ✅

### ❌ Invalid Formats

**Status:**
- `PENDING` ❌ (not recognized → defaults to TODO)
- `COMPLETED` ❌ (use DONE)
- `ARCHIVED` ❌ (not supported)

**Priority:**
- `NORMAL` ❌ (use MEDIUM)
- `P1` ❌ (use actual name)

**Date:**
- `December 31, 2024` ❌ (use numeric format)
- `31st Dec 2024` ❌
- `Invalid date format` ❌ (will be null)

---

## 🛠️ Step-by-Step: Creating Import File

### Using Excel:

1. **Open Excel** (or Google Sheets, then export as .xlsx)

2. **Create headers in row 1:**
   - A1: `Title`
   - B1: `Description`
   - C1: `Status`
   - D1: `Priority`
   - E1: `Due Date`
   - F1: `Assignee Email`
   - G1: `Team Name`

3. **Add data starting from row 2:**
   ```
   A2: Implement login
   B2: JWT authentication with refresh tokens
   C2: TODO
   D2: HIGH
   E2: 2024-12-31
   F2: dev@company.com
   G2: Backend Team
   ```

4. **Format date column (E):**
   - Select column E
   - Right-click → Format Cells
   - Choose Date → yyyy-mm-dd

5. **Save as:**
   - File → Save As
   - Format: **Excel Workbook (.xlsx)**
   - Name: `tasks-import.xlsx`

6. **Upload via API or UI**

### Using Word:

1. **Open Word**

2. **Insert Table:**
   - Insert → Table → 7 columns x 5 rows

3. **Fill header row:**
   - Make first row bold
   - Enter: Title | Description | Status | Priority | Due Date | Assignee Email | Team Name

4. **Add tasks in remaining rows**

5. **Save as:**
   - File → Save As
   - Format: **Word Document (.docx)**
   - Name: `tasks-import.docx`

6. **Upload via API or UI**

---

## 🚀 Frontend Integration

```jsx
import TaskImport from './components/TaskImport';

function TasksPage() {
  const teamId = 1; // Current team ID

  const handleImportSuccess = (result) => {
    console.log('Imported:', result);
    // Refresh task list
    fetchTasks();
  };

  return (
    <div>
      <h1>Tasks</h1>
      
      {/* Import Component */}
      <TaskImport 
        teamId={teamId} 
        onImportSuccess={handleImportSuccess}
      />
      
      {/* Task List */}
      <TaskList />
    </div>
  );
}
```

---

## 📥 Testing Your Import

### Test File (Copy to Excel):

```
Title,Description,Status,Priority,Due Date,Assignee Email,Team Name
Test Task 1,This is a test task,TODO,MEDIUM,2024-12-31,test@example.com,Test Team
Test Task 2,Another test,IN_PROGRESS,HIGH,2024-12-25,test@example.com,Test Team
Test Task 3,Final test,DONE,LOW,2024-12-20,test@example.com,Test Team
```

### Expected Result:
```json
{
  "totalRows": 3,
  "successCount": 3,
  "failureCount": 0,
  "errors": [],
  "message": "Import completed: 3 succeeded, 0 failed out of 3 total rows"
}
```

---

## 📞 Troubleshooting

### "Title is required"
- Ensure column A has value in every data row
- Check for hidden/blank rows

### "Invalid file type"
- Save as `.xlsx` not `.xls` (Excel)
- Save as `.docx` not `.doc` (Word)
- Don't use `.csv` (use Excel endpoint for CSV)

### "Assignee not found"
- Verify email exists in system
- Check for typos
- Leave blank if no assignee

### No tasks imported
- Check file has data in rows 2+
- Verify header row matches expected columns
- Try sample template first

---

## 💡 Pro Tips

1. **Start Small:** Test with 3-5 tasks first
2. **Use Template:** Download CSV template as starting point
3. **Validate Emails:** Ensure all assignee emails exist
4. **Date Format:** Use ISO format (yyyy-MM-dd) for best compatibility
5. **Case-Insensitive:** Status/Priority are case-insensitive
6. **Batch Import:** Keep batches under 100 tasks
7. **Error Handling:** Check response errors array for issues
8. **Refresh Data:** Refresh UI after successful import

---

## ✨ Advanced: Programmatic Import

```javascript
async function importTasksFromCSV(csvText, teamId) {
  // Convert CSV to Excel-like array
  const lines = csvText.split('\n');
  const headers = lines[0].split(',');
  
  // Create Excel file programmatically
  const workbook = XLSX.utils.book_new();
  const worksheet = XLSX.utils.aoa_to_sheet(
    lines.map(line => line.split(','))
  );
  XLSX.utils.book_append_sheet(workbook, worksheet, 'Tasks');
  
  // Convert to blob
  const excelBlob = XLSX.write(workbook, { 
    type: 'blob', 
    bookType: 'xlsx' 
  });
  
  // Upload
  const formData = new FormData();
  formData.append('file', excelBlob, 'tasks.xlsx');
  
  const response = await api.post(
    `/import/tasks/excel/${teamId}`,
    formData
  );
  
  return response.data;
}
```

---

Ready to import! 🚀
