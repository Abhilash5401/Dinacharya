package com.kanban.service.impl;

import com.kanban.exception.ResourceNotFoundException;
import com.kanban.model.dto.request.CreateTaskRequest;
import com.kanban.model.dto.request.TaskImportData;
import com.kanban.model.dto.response.TaskImportResponse;
import com.kanban.model.dto.response.TaskResponse;
import com.kanban.model.entity.Team;
import com.kanban.model.entity.User;
import com.kanban.model.enums.TaskPriority;
import com.kanban.model.enums.TaskStatus;
import com.kanban.model.enums.UserRole;
import com.kanban.repository.TeamRepository;
import com.kanban.repository.UserRepository;
import com.kanban.service.FileImportService;
import com.kanban.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileImportServiceImpl implements FileImportService {

    private final TaskService taskService;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TaskImportResponse importTasksFromExcel(MultipartFile file, UUID teamId, UUID userId) throws IOException {
        log.info("Starting Excel import for team: {}", teamId);

        // Verify team exists
        teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        // Read the file bytes once so we can attempt two parsing strategies.
        byte[] bytes = file.getBytes();

        // Auto-detect the multi-sheet attendance/timesheet layout first.
        List<TaskImportData> attendanceTasks = parseAttendanceExcel(bytes);
        if (!attendanceTasks.isEmpty()) {
            log.info("Detected attendance tasksheet layout ({} task rows) — importing with employee auto-create",
                    attendanceTasks.size());
            return processImportedTasks(attendanceTasks, teamId, userId, true);
        }

        // Fall back to the flat template (Title, Description, Status, Priority, Due Date, Assignee Email, Team).
        List<TaskImportData> parsedTasks = parseExcelBytes(bytes);
        return processImportedTasks(parsedTasks, teamId, userId);
    }

    @Override
    @Transactional
    public TaskImportResponse importTasksFromWord(MultipartFile file, UUID teamId, UUID userId) throws IOException {
        log.info("Starting Word import for team: {}", teamId);
        
        // Verify team exists
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        List<TaskImportData> parsedTasks = parseWordFile(file);
        return processImportedTasks(parsedTasks, teamId, userId);
    }

    @Override
    @Transactional
    public TaskImportResponse importAttendanceSheet(MultipartFile file, UUID teamId, UUID userId) throws IOException {
        log.info("Starting attendance tasksheet import for team: {}", teamId);

        teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        List<TaskImportData> parsedTasks = parseAttendanceExcel(file);
        return processImportedTasks(parsedTasks, teamId, userId, true);
    }

    @Override
    public List<TaskImportData> parseExcelFile(MultipartFile file) throws IOException {
        return parseExcelBytes(file.getBytes());
    }

    private List<TaskImportData> parseExcelBytes(byte[] bytes) throws IOException {
        List<TaskImportData> tasks = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheetAt(0);

            // Skip header row (row 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                TaskImportData task = parseExcelRow(row, i + 1);
                if (task != null) {
                    tasks.add(task);
                }
            }
        }

        log.info("Parsed {} tasks from Excel file", tasks.size());
        return tasks;
    }

    @Override
    public List<TaskImportData> parseWordFile(MultipartFile file) throws IOException {
        List<TaskImportData> tasks = new ArrayList<>();
        
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            // Parse tables in the document
            List<XWPFTable> tables = document.getTables();
            
            for (XWPFTable table : tables) {
                List<XWPFTableRow> rows = table.getRows();
                
                // Skip header row (row 0)
                for (int i = 1; i < rows.size(); i++) {
                    XWPFTableRow row = rows.get(i);
                    TaskImportData task = parseWordRow(row, i + 1);
                    if (task != null) {
                        tasks.add(task);
                    }
                }
            }

            // If no tables found, try parsing from paragraphs (simple format)
            if (tasks.isEmpty()) {
                tasks = parseWordParagraphs(document);
            }
        }

        log.info("Parsed {} tasks from Word file", tasks.size());
        return tasks;
    }

    private TaskImportData parseExcelRow(Row row, int rowNumber) {
        try {
            String title = getCellValueAsString(row.getCell(0));
            
            // Skip if title is empty
            if (title == null || title.trim().isEmpty()) {
                return null;
            }

            return TaskImportData.builder()
                    .title(title)
                    .description(getCellValueAsString(row.getCell(1)))
                    .status(parseStatus(getCellValueAsString(row.getCell(2))))
                    .priority(parsePriority(getCellValueAsString(row.getCell(3))))
                    .dueDate(getCellValueAsDate(row.getCell(4)))
                    .assigneeEmail(getCellValueAsString(row.getCell(5)))
                    .teamName(getCellValueAsString(row.getCell(6)))
                    .rowNumber(rowNumber)
                    .build();
        } catch (Exception e) {
            log.error("Error parsing Excel row {}: {}", rowNumber, e.getMessage());
            return null;
        }
    }

    private TaskImportData parseWordRow(XWPFTableRow row, int rowNumber) {
        try {
            if (row.getTableCells().size() < 5) {
                return null;
            }

            String title = row.getCell(0).getText().trim();
            
            // Skip if title is empty
            if (title.isEmpty()) {
                return null;
            }

            return TaskImportData.builder()
                    .title(title)
                    .description(row.getCell(1).getText().trim())
                    .status(parseStatus(row.getCell(2).getText().trim()))
                    .priority(parsePriority(row.getCell(3).getText().trim()))
                    .dueDate(parseDate(row.getCell(4).getText().trim()))
                    .assigneeEmail(row.getTableCells().size() > 5 ? row.getCell(5).getText().trim() : null)
                    .teamName(row.getTableCells().size() > 6 ? row.getCell(6).getText().trim() : null)
                    .rowNumber(rowNumber)
                    .build();
        } catch (Exception e) {
            log.error("Error parsing Word row {}: {}", rowNumber, e.getMessage());
            return null;
        }
    }

    private List<TaskImportData> parseWordParagraphs(XWPFDocument document) {
        List<TaskImportData> tasks = new ArrayList<>();
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        
        int rowNumber = 1;
        for (XWPFParagraph paragraph : paragraphs) {
            String text = paragraph.getText().trim();
            
            // Skip empty lines or headers
            if (text.isEmpty() || text.toLowerCase().startsWith("title")) {
                continue;
            }

            // Simple format: "Task Title - Description - Status - Priority - Due Date"
            String[] parts = text.split("-");
            if (parts.length >= 3) {
                TaskImportData task = TaskImportData.builder()
                        .title(parts[0].trim())
                        .description(parts.length > 1 ? parts[1].trim() : "")
                        .status(parts.length > 2 ? parseStatus(parts[2].trim()) : TaskStatus.TODO)
                        .priority(parts.length > 3 ? parsePriority(parts[3].trim()) : TaskPriority.MEDIUM)
                        .dueDate(parts.length > 4 ? parseDate(parts[4].trim()) : null)
                        .rowNumber(rowNumber++)
                        .build();
                
                tasks.add(task);
            }
        }
        
        return tasks;
    }

    private TaskImportResponse processImportedTasks(List<TaskImportData> parsedTasks, UUID teamId, UUID userId) {
        return processImportedTasks(parsedTasks, teamId, userId, false);
    }

    private TaskImportResponse processImportedTasks(List<TaskImportData> parsedTasks, UUID teamId, UUID userId,
                                                    boolean autoCreateEmployees) {
        List<TaskResponse> importedTasks = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (TaskImportData taskData : parsedTasks) {
            try {
                // Validate required fields
                if (taskData.getTitle() == null || taskData.getTitle().trim().isEmpty()) {
                    errors.add(String.format("Row %d: Title is required", taskData.getRowNumber()));
                    failureCount++;
                    continue;
                }

                // Find assignee: prefer email, then fall back to employee name (attendance sheets)
                UUID assigneeId = null;
                if (taskData.getAssigneeEmail() != null && !taskData.getAssigneeEmail().isEmpty()) {
                    User assignee = userRepository.findByEmailIgnoreCase(taskData.getAssigneeEmail())
                            .orElse(null);
                    if (assignee != null) {
                        assigneeId = assignee.getId();
                    } else {
                        log.warn("Row {}: Assignee with email {} not found",
                                taskData.getRowNumber(), taskData.getAssigneeEmail());
                    }
                }
                if (assigneeId == null && taskData.getEmployeeName() != null
                        && !taskData.getEmployeeName().isBlank()) {
                    User assignee = resolveUserByName(taskData.getEmployeeName());
                    if (assignee == null && autoCreateEmployees) {
                        assignee = createEmployee(taskData.getEmployeeName(), taskData.getDepartment());
                    }
                    if (assignee != null) {
                        assigneeId = assignee.getId();
                    } else {
                        log.warn("Row {}: Employee '{}' not matched to any user",
                                taskData.getRowNumber(), taskData.getEmployeeName());
                    }
                }

                // Create task request
                CreateTaskRequest request = CreateTaskRequest.builder()
                        .title(taskData.getTitle())
                        .description(taskData.getDescription())
                        .remark(taskData.getRemark())
                        .status(taskData.getStatus() != null ? taskData.getStatus() : TaskStatus.TODO)
                        .priority(taskData.getPriority() != null ? taskData.getPriority() : TaskPriority.MEDIUM)
                        .deadline(taskData.getDueDate() != null ? taskData.getDueDate().atStartOfDay() : null)
                        .assignedToId(assigneeId)
                        .teamId(teamId)
                        .build();

                // Create the task
                TaskResponse createdTask = taskService.createTask(request, userId);
                importedTasks.add(createdTask);
                successCount++;

            } catch (Exception e) {
                String errorMsg = String.format("Row %d: %s", taskData.getRowNumber(), e.getMessage());
                errors.add(errorMsg);
                failureCount++;
                log.error("Error importing task from row {}: {}", taskData.getRowNumber(), e.getMessage());
            }
        }

        String message = String.format("Import completed: %d succeeded, %d failed out of %d total rows",
                successCount, failureCount, parsedTasks.size());

        return TaskImportResponse.builder()
                .totalRows(parsedTasks.size())
                .successCount(successCount)
                .failureCount(failureCount)
                .errors(errors)
                .importedTasks(importedTasks)
                .message(message)
                .build();
    }

    // ==========================================================================
    // Attendance / daily tasksheet parsing (multi-sheet, header-driven)
    // ==========================================================================

    @Override
    public List<TaskImportData> parseAttendanceExcel(MultipartFile file) throws IOException {
        return parseAttendanceExcel(file.getBytes());
    }

    private List<TaskImportData> parseAttendanceExcel(byte[] bytes) throws IOException {
        List<TaskImportData> tasks = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            DataFormatter formatter = new DataFormatter();
            int sheetCount = workbook.getNumberOfSheets();

            for (int s = 0; s < sheetCount; s++) {
                Sheet sheet = workbook.getSheetAt(s);
                if (sheet == null) {
                    continue;
                }
                try {
                    parseAttendanceSheet(sheet, formatter, tasks);
                } catch (Exception e) {
                    log.warn("Skipping sheet '{}' due to parse error: {}", sheet.getSheetName(), e.getMessage());
                }
            }
        }

        log.info("Parsed {} task rows from attendance workbook", tasks.size());
        return tasks;
    }

    private void parseAttendanceSheet(Sheet sheet, DataFormatter formatter, List<TaskImportData> tasks) {
        int headerRowIdx = findHeaderRow(sheet, formatter);
        if (headerRowIdx < 0) {
            log.info("No recognizable header row in sheet '{}' — skipping", sheet.getSheetName());
            return;
        }

        Row headerRow = sheet.getRow(headerRowIdx);
        java.util.Map<String, Integer> cols = new java.util.HashMap<>();
        for (int c = headerRow.getFirstCellNum(); c < headerRow.getLastCellNum(); c++) {
            String header = formatter.formatCellValue(headerRow.getCell(c));
            String key = classifyHeader(header);
            if (key != null && !cols.containsKey(key)) {
                cols.put(key, c);
            }
        }

        String sheetEmployee = detectSheetEmployeeName(sheet, formatter, headerRowIdx, cols);
        // The sheet tab is like "ASE Pattima kalyani" -> department "ASE", name "Pattima kalyani"
        String[] deptAndName = splitDepartmentAndName(sheetEmployee);
        String sheetDepartment = deptAndName[0];
        String sheetName = deptAndName[1];

        for (int r = headerRowIdx + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || isRowEmpty(row)) {
                continue;
            }

            // Title comes from the TASK column; fall back to DESCRIPTION if TASK is empty.
            String taskText = cellText(row, cols.get("TASK"), formatter);
            String descriptionText = cellText(row, cols.get("DESCRIPTION"), formatter);
            String title = (taskText != null && !taskText.isBlank()) ? taskText : descriptionText;
            if (title == null || title.isBlank()) {
                continue; // skip rows with no task/description (attendance-only rows)
            }

            String statusText = cellText(row, cols.get("STATUS"), formatter);
            String priorityText = cellText(row, cols.get("PRIORITY"), formatter);
            String attendance = cellText(row, cols.get("ATTENDANCE"), formatter);
            String explicitRemark = cellText(row, cols.get("REMARK"), formatter);
            String login = cellText(row, cols.get("LOGIN"), formatter);
            String logout = cellText(row, cols.get("LOGOUT"), formatter);
            String hours = cellText(row, cols.get("HOURS"), formatter);

            // Build a rich description from the timesheet details (login/logout/hours),
            // unless the sheet already has a dedicated description column.
            String description = (descriptionText != null && !descriptionText.isBlank() && taskText != null)
                    ? descriptionText
                    : composeWorkDetails(login, logout, hours);

            // Remark = explicit remark, else the attendance value (e.g. "Present")
            String remark = (explicitRemark != null && !explicitRemark.isBlank()) ? explicitRemark : attendance;

            String rowDepartment = cellText(row, cols.get("DEPARTMENT"), formatter);
            String department = (rowDepartment != null && !rowDepartment.isBlank()) ? rowDepartment : sheetDepartment;

            String rowEmployee = cellText(row, cols.get("NAME"), formatter);
            String employee = (rowEmployee != null && !rowEmployee.isBlank()) ? rowEmployee : sheetName;

            LocalDate date = null;
            if (cols.containsKey("DATE")) {
                date = getCellValueAsDate(row.getCell(cols.get("DATE")));
            }

            TaskImportData data = TaskImportData.builder()
                    .title(title.trim())
                    .description(description)
                    .remark(remark)
                    .status(parseStatus(statusText))
                    .priority(parsePriority(priorityText))
                    .dueDate(date)
                    .employeeName(employee)
                    .department(department)
                    .sheetName(sheet.getSheetName())
                    .rowNumber(r + 1)
                    .build();

            tasks.add(data);
        }
    }

    /** Splits a tab/label like "ASE Pattima kalyani" into ["ASE", "Pattima kalyani"]. */
    private String[] splitDepartmentAndName(String raw) {
        if (raw == null || raw.isBlank()) {
            return new String[]{null, null};
        }
        String s = raw.trim();
        int sp = s.indexOf(' ');
        if (sp > 0) {
            String first = s.substring(0, sp);
            String rest = s.substring(sp + 1).trim();
            // Treat a short, all-uppercase leading token (e.g. "ASE") as the department code.
            if (!rest.isEmpty() && first.length() <= 5 && first.equals(first.toUpperCase())) {
                return new String[]{first, rest};
            }
        }
        return new String[]{null, s};
    }

    private String composeWorkDetails(String login, String logout, String hours) {
        List<String> parts = new ArrayList<>();
        if (login != null && !login.isBlank()) parts.add("Login: " + login.trim());
        if (logout != null && !logout.isBlank()) parts.add("Logout: " + logout.trim());
        if (hours != null && !hours.isBlank()) parts.add("Hours: " + hours.trim());
        return parts.isEmpty() ? null : String.join(" | ", parts);
    }

    /** Creates a minimal employee profile from a tasksheet row (name + department). */
    private User createEmployee(String rawName, String department) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            return null;
        }
        String email = buildEmployeeEmail(name);

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .name(name)
                .role(UserRole.MEMBER)
                .department((department != null && !department.isBlank()) ? department.trim() : null)
                .isActive(true)
                .lastActive(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        log.info("Auto-created employee '{}' ({}) from tasksheet import", name, email);
        return saved;
    }

    /** Builds a unique, deterministic email from an employee name, e.g. "Akkipalli Sri Usha" -> akkipalli.sri.usha@imported.local */
    private String buildEmployeeEmail(String name) {
        String slug = name.toLowerCase()
                .replaceAll("[^a-z0-9]+", ".")
                .replaceAll("^\\.|\\.$", "");
        if (slug.isEmpty()) {
            slug = "employee";
        }
        String base = slug + "@imported.local";
        String candidate = base;
        int suffix = 1;
        while (userRepository.findByEmailIgnoreCase(candidate).isPresent()) {
            candidate = slug + suffix + "@imported.local";
            suffix++;
        }
        return candidate;
    }

    /** Scans the first rows of a sheet to find the row that looks like a header. */
    private int findHeaderRow(Sheet sheet, DataFormatter formatter) {
        int scanLimit = Math.min(sheet.getLastRowNum(), 15);
        int bestRow = -1;
        int bestScore = 0;

        for (int r = sheet.getFirstRowNum(); r <= scanLimit; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            java.util.Set<String> keys = new java.util.HashSet<>();
            for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                String key = classifyHeader(formatter.formatCellValue(row.getCell(c)));
                if (key != null) {
                    keys.add(key);
                }
            }
            // A header must have a TASK column plus at least one other known column.
            if (keys.contains("TASK") && keys.size() >= 2 && keys.size() > bestScore) {
                bestScore = keys.size();
                bestRow = r;
            }
        }
        return bestRow;
    }

    /** Maps a header cell's text to a canonical column key, or null if unrecognized. */
    private String classifyHeader(String raw) {
        if (raw == null) {
            return null;
        }
        String h = raw.trim().toLowerCase();
        if (h.isEmpty()) {
            return null;
        }
        if (h.contains("action")) return null; // ignore ACTIONS column
        if (h.contains("scorecard") || h.contains("completion") || h.contains("completed")) return null; // ignore Performance Scorecard block
        if (h.contains("employee") || h.contains("associate") || h.equals("name") || h.contains("emp name") || h.contains("staff")) return "NAME";
        if (h.contains("depart") || h.equals("dept")) return "DEPARTMENT";
        // "Task Description" must be the task title, so check "task" before "description"
        if (h.contains("task") || h.contains("activity") || h.contains("assignment") || h.contains("work done")) return "TASK";
        if (h.contains("description") || h.contains("details")) return "DESCRIPTION";
        if (h.contains("date")) return "DATE";
        if (h.contains("priorit")) return "PRIORITY";
        if (h.contains("status")) return "STATUS";
        if (h.contains("attend") || h.equals("present") || h.contains("presence")) return "ATTENDANCE";
        if (h.contains("remark") || h.contains("note") || h.contains("comment")) return "REMARK";
        if (h.contains("login") || h.contains("in time") || h.contains("check in") || h.contains("check-in")) return "LOGIN";
        if (h.contains("logout") || h.contains("out time") || h.contains("check out") || h.contains("check-out")) return "LOGOUT";
        if (h.contains("hour") || h.contains("duration") || h.contains("worked")) return "HOURS";
        return null;
    }

    /** Tries to find the employee name from a NAME column value above the header, a labelled cell, or the sheet tab. */
    private String detectSheetEmployeeName(Sheet sheet, DataFormatter formatter, int headerRowIdx,
                                           java.util.Map<String, Integer> cols) {
        // Look for a "Name: X" style label in the rows above the header
        for (int r = sheet.getFirstRowNum(); r < headerRowIdx; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                String text = formatter.formatCellValue(row.getCell(c));
                if (text == null) {
                    continue;
                }
                String t = text.trim();
                if (t.toLowerCase().startsWith("name") && t.contains(":")) {
                    String candidate = t.substring(t.indexOf(':') + 1).trim();
                    if (!candidate.isEmpty()) {
                        return candidate;
                    }
                }
                if (t.toUpperCase().startsWith("ASE ") && t.length() > 4) {
                    return t;
                }
            }
        }
        // Fall back to the sheet tab name if it is not generic
        String sheetName = sheet.getSheetName();
        if (sheetName != null && !sheetName.matches("(?i)sheet\\d+")) {
            return sheetName.trim();
        }
        return null;
    }

    private String cellText(Row row, Integer colIdx, DataFormatter formatter) {
        if (colIdx == null || row == null) {
            return null;
        }
        Cell cell = row.getCell(colIdx);
        if (cell == null) {
            return null;
        }
        String v = formatter.formatCellValue(cell);
        return v == null ? null : v.trim();
    }

    private User resolveUserByName(String rawName) {
        if (rawName == null) {
            return null;
        }
        String name = rawName.trim().replaceFirst("(?i)^name\\s*[:\\-]\\s*", "").trim();
        if (name.isEmpty()) {
            return null;
        }
        String cleaned = name.replaceFirst("(?i)^ASE\\s+", "").trim();

        User user = userRepository.findByNameIgnoreCase(name).orElse(null);
        if (user == null && !cleaned.equalsIgnoreCase(name)) {
            user = userRepository.findByNameIgnoreCase(cleaned).orElse(null);
        }
        if (user == null) {
            String query = cleaned.isEmpty() ? name : cleaned;
            List<User> matches = userRepository.searchByNameContaining(query);
            if (matches.size() == 1) {
                user = matches.get(0);
            }
        }
        return user;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    private LocalDate getCellValueAsDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                return parseDate(cell.getStringCellValue());
            }
        } catch (Exception e) {
            log.error("Error parsing date from cell: {}", e.getMessage());
        }

        return null;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            // Try common date formats
            String[] formats = {
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "MM/dd/yyyy",
                "dd-MM-yyyy",
                "MM-dd-yyyy"
            };

            for (String format : formats) {
                try {
                    return LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern(format));
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            log.error("Error parsing date '{}': {}", dateStr, e.getMessage());
        }

        return null;
    }

    private TaskStatus parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return TaskStatus.TODO;
        }

        try {
            return TaskStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try to match partial strings
            String normalized = status.trim().toLowerCase();
            if (normalized.contains("todo") || normalized.contains("to do") || normalized.contains("pending")) {
                return TaskStatus.TODO;
            } else if (normalized.contains("progress") || normalized.contains("working")) {
                return TaskStatus.IN_PROGRESS;
            } else if (normalized.contains("review")) {
                return TaskStatus.IN_REVIEW;
            } else if (normalized.contains("done") || normalized.contains("complete")) {
                return TaskStatus.DONE;
            }
            return TaskStatus.TODO;
        }
    }

    private TaskPriority parsePriority(String priority) {
        if (priority == null || priority.trim().isEmpty()) {
            return TaskPriority.MEDIUM;
        }

        try {
            return TaskPriority.valueOf(priority.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try to match partial strings
            String normalized = priority.trim().toLowerCase();
            if (normalized.contains("low")) {
                return TaskPriority.LOW;
            } else if (normalized.contains("high") || normalized.contains("critical")) {
                return TaskPriority.HIGH;
            } else if (normalized.contains("urgent")) {
                return TaskPriority.URGENT;
            }
            return TaskPriority.MEDIUM;
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
