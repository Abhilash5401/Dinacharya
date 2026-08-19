package com.kanban.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskImportResponse {
    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<String> errors;
    private List<TaskResponse> importedTasks;
    private String message;
}
