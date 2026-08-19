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
}
