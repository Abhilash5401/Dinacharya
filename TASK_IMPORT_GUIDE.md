# 📁 Task Import Feature Guide

Import multiple tasks at once from Excel (XLSX) or Word (DOCX) files!

---

## 🚀 Quick Start

### 1. **API Endpoints**

#### Import from Excel
```http
POST /api/v1/import/tasks/excel/{teamId}
Content-Type: multipart/form-data
Authorization: Bearer {your-jwt-token}

Body:
- file: [Excel file]
```

#### Import from Word
```http
POST /api/v1/import/tasks/word/{teamId}
Content-Type: multipart/form-data
Authorization: Bearer {your-jwt-token}

Body:
- file: [Word file]
```

---

## 📊 Excel Format (.xlsx)

### Required Structure

| Title | Description | Status | Priority | Due Date | Assignee Email | Team Name |
|-------|-------------|--------|----------|----------|----------------|-----------|
| Implement login | Create JWT auth | TODO | HIGH | 2024-12-31 | dev@example.com | Backend |
| Fix UI bug | Menu not responsive | IN_PROGRESS | MEDIUM | 2024-12-25 | designer@example.com | Frontend |

### Column Details

1. **Title** ✅ **(Required)**
   - Task title/name
   - Example: "Implement user authentication"

2. **Description** (Optional)
   - Detailed task description
   - Example: "Create JWT-based authentication with refresh tokens"

3. **Status** (Optional, defaults to TODO)
   - Valid values: `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`
   - Case-insensitive
   - Aliases: "To Do", "Pending", "Working", "Complete"

4. **Priority** (Optional, defaults to MEDIUM)
   - Valid values: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`
   - Case-insensitive
   - Aliases: "Urgent" → HIGH

5. **Due Date** (Optional)
   - Formats accepted:
     - `yyyy-MM-dd` (2024-12-31)
     - `dd/MM/yyyy` (31/12/2024)
     - `MM/dd/yyyy` (12/31/2024)
     - Excel date format

6. **Assignee Email** (Optional)
   - User email address
   - Must match existing user in system
   - Example: "john.doe@company.com"

7. **Team Name** (Optional, currently ignored - uses teamId from URL)
   - For future use

---

## 📝 Word Format (.docx)

### Option 1: Table Format (Recommended)

Create a table in Word with the same columns as Excel:

```
┌──────────────┬─────────────┬──────────┬──────────┬──────────┬─────────────────┬───────────┐
│ Title        │ Description │ Status   │ Priority │ Due Date │ Assignee Email  │ Team Name │
├──────────────┼─────────────┼──────────┼──────────┼──────────┼─────────────────┼───────────┤
│ Task 1       │ Details...  │ TODO     │ HIGH     │ 2024-12  │ user@email.com  │ Team A    │
└──────────────┴─────────────┴──────────┴──────────┴──────────┴─────────────────┴───────────┘
```

### Option 2: Simple Format

Each line with format: `Title - Description - Status - Priority - Due Date`

```
Implement login - Create JWT auth - TODO - HIGH - 2024-12-31
Fix UI bug - Menu responsive - IN_PROGRESS - MEDIUM - 2024-12-25
Write docs - API documentation - TODO - LOW - 2025-01-10
```

---

## 📥 Response Format

```json
{
  "totalRows": 10,
  "successCount": 8,
  "failureCount": 2,
  "errors": [
    "Row 5: Title is required",
    "Row 8: Assignee with email john@unknown.com not found"
  ],
  "importedTasks": [
    {
      "id": 1,
      "title": "Implement login",
      "status": "TODO",
      "priority": "HIGH",
      ...
    }
  ],
  "message": "Import completed: 8 succeeded, 2 failed out of 10 total rows"
}
```

---

## 🔧 Usage Examples

### Using cURL

```bash
# Import from Excel
curl -X POST "http://localhost:8080/api/v1/import/tasks/excel/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@tasks.xlsx"

# Import from Word
curl -X POST "http://localhost:8080/api/v1/import/tasks/word/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@tasks.docx"
```

### Using JavaScript (Frontend)

```javascript
async function importTasks(file, teamId, token) {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(
    `http://localhost:8080/api/v1/import/tasks/excel/${teamId}`,
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData
    }
  );

  const result = await response.json();
  console.log('Import result:', result);
  return result;
}

// Usage with file input
document.getElementById('fileInput').addEventListener('change', async (e) => {
  const file = e.target.files[0];
  const teamId = 1;
  const token = localStorage.getItem('token');
  
  const result = await importTasks(file, teamId, token);
  alert(result.message);
});
```

---

## ✅ Validation Rules

1. **Title**: Required, cannot be empty
2. **Status**: Must be one of: TODO, IN_PROGRESS, IN_REVIEW, DONE (case-insensitive)
3. **Priority**: Must be one of: LOW, MEDIUM, HIGH, CRITICAL (case-insensitive)
4. **Due Date**: Must be valid date format
5. **Assignee Email**: If provided, user must exist in system
6. **Team**: Team ID in URL must exist

---

## ⚠️ Common Errors & Solutions

### Error: "Title is required"
**Solution:** Ensure every row has a title in the first column

### Error: "Assignee with email X not found"
**Solution:** 
- Check email spelling
- Ensure user exists in system
- Leave empty if no assignee needed

### Error: "Invalid file type"
**Solution:** 
- Use `.xlsx` for Excel (not `.xls`)
- Use `.docx` for Word (not `.doc`)

### Error: "Team not found"
**Solution:** Verify the team ID in the URL exists

---

## 📋 Sample Files

### Download Template
```http
GET /api/v1/import/template/excel
```

Returns a CSV template you can save as `.csv` and open in Excel.

### Pre-made Sample
See `sample-task-import.csv` in project root for a working example.

---

## 🎯 Best Practices

1. **Start Small**: Test with 5-10 tasks first
2. **Validate Data**: Check emails, dates, and enums before import
3. **Use Templates**: Download and modify the provided template
4. **Check Response**: Review errors array to fix failed imports
5. **Batch Size**: Keep imports under 100 tasks for best performance
6. **Date Format**: Stick to `yyyy-MM-dd` for consistency

---

## 🔒 Security & Permissions

- Requires authentication (Bearer token)
- Users must have `USER`, `MODERATOR`, or `ADMIN` role
- Can only import to teams they have access to
- File size limit: 10MB (configurable)

---

## 📊 Supported File Types

| File Type | Extension | Max Size | Notes |
|-----------|-----------|----------|-------|
| Excel | `.xlsx` | 10 MB | Office 2007+ format |
| Word | `.docx` | 10 MB | Office 2007+ format |
| CSV | `.csv` | 5 MB | Via template endpoint |

---

## 🚀 Frontend Integration Example

```tsx
import React, { useState } from 'react';
import { Upload, FileSpreadsheet, FileText } from 'lucide-react';

const TaskImportButton = ({ teamId, onSuccess }) => {
  const [importing, setImporting] = useState(false);

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const fileType = file.name.endsWith('.xlsx') ? 'excel' : 'word';
    setImporting(true);

    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await fetch(
        `${API_URL}/import/tasks/${fileType}/${teamId}`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${getToken()}`
          },
          body: formData
        }
      );

      const result = await response.json();
      
      if (result.successCount > 0) {
        onSuccess(result);
        alert(`✅ Imported ${result.successCount} tasks successfully!`);
      }
      
      if (result.failureCount > 0) {
        console.error('Import errors:', result.errors);
        alert(`⚠️ ${result.failureCount} tasks failed. Check console.`);
      }
    } catch (error) {
      console.error('Import failed:', error);
      alert('Import failed: ' + error.message);
    } finally {
      setImporting(false);
    }
  };

  return (
    <div className="flex gap-2">
      <label className="btn btn-primary">
        <Upload className="w-4 h-4" />
        {importing ? 'Importing...' : 'Import Tasks'}
        <input
          type="file"
          accept=".xlsx,.docx"
          onChange={handleFileUpload}
          disabled={importing}
          className="hidden"
        />
      </label>
    </div>
  );
};
```

---

## 📖 API Documentation

Access full API documentation at:
- **Swagger UI**: `http://localhost:8080/api/v1/swagger-ui.html`
- Look for **"File Import"** section

---

## 🆘 Support

For issues or questions:
1. Check error messages in response
2. Review validation rules above
3. Test with sample template first
4. Check logs for detailed errors

---

## ✨ Features

- ✅ Bulk task creation
- ✅ Excel (.xlsx) support
- ✅ Word (.docx) support (table & simple format)
- ✅ Auto-assign to users
- ✅ Date parsing (multiple formats)
- ✅ Status/Priority normalization
- ✅ Detailed error reporting
- ✅ Partial success handling
- ✅ Template download
- ✅ Row-level error tracking
