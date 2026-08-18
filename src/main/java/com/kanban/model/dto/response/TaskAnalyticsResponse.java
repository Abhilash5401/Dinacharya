package com.kanban.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAnalyticsResponse {

    private Map<String, Long> statusCounts;
    private Map<String, Long> priorityCounts;
    private Long totalTasks;
    private Long overdueTasks;
    private Long completedTasks;
}
