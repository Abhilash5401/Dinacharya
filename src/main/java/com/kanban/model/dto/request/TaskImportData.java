package com.kanban.model.dto.request;

import com.kanban.model.enums.TaskPriority;
import com.kanban.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskImportData {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private String assigneeEmail;
    private String teamName;
    private Integer rowNumber; // For error reporting

    // --- Attendance / daily tasksheet fields ---
    private String employeeName;   // resolved from a Name column or the sheet tab name
    private String attendance;     // e.g. Present / Absent / Leave
    private String loginTime;      // raw text, e.g. "10:30 AM"
    private String logoutTime;     // raw text, e.g. "06:00 PM"
    private String hoursWorked;    // raw text, e.g. "7:30" or "7.5"
    private String sheetName;      // source sheet, for error reporting
}
