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
import com.kanban.repository.TeamRepository;
import com.kanban.repository.UserRepository;
import com.kanban.service.FileImportService;
import com.kanban.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    @Transactional
    public TaskImportResponse importTasksFromExcel(MultipartFile file, UUID teamId, UUID userId) throws IOException {
        log.info("Starting Excel import for team: {}", teamId);
        
        // Verify team exists
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        List<TaskImportData> parsedTasks = parseExcelFile(file);
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
    public List<TaskImportData> parseExcelFile(MultipartFile file) throws IOException {
        List<TaskImportData> tasks = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
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

                // Find assignee if email provided
                UUID assigneeId = null;
                if (taskData.getAssigneeEmail() != null && !taskData.getAssigneeEmail().isEmpty()) {
                    User assignee = userRepository.findByEmail(taskData.getAssigneeEmail())
                            .orElse(null);
                    if (assignee != null) {
                        assigneeId = assignee.getId();
                    } else {
                        log.warn("Row {}: Assignee with email {} not found", 
                                taskData.getRowNumber(), taskData.getAssigneeEmail());
                    }
                }

                // Create task request
                CreateTaskRequest request = CreateTaskRequest.builder()
                        .title(taskData.getTitle())
                        .description(taskData.getDescription())
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
