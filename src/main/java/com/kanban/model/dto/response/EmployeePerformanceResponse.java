package com.kanban.model.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class EmployeePerformanceResponse {
    UUID userId;
    String userName;
    String department;
    LocalDate periodStart;
    LocalDate periodEnd;
    double productivityScore;
    double efficiencyScore;
    double disciplineScore;
    double performanceIndex;
    Double rollingIndex;
    int tasksCompleted;
    int tasksAssigned;
    int onTimeTasks;
    int attendanceDays;
    int workingDays;
}
