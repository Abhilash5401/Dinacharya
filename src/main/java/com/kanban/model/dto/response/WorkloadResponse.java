package com.kanban.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadResponse {

    private UUID userId;
    private String userName;
    private Long assignedTasks;
    private Long completedTasks;
    private Long inProgressTasks;
    private Long overdueTasks;
}
