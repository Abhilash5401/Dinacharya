package com.kanban.model.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class EmployeePerformanceOverviewResponse {
    LocalDate periodStart;
    LocalDate periodEnd;
    List<EmployeePerformanceResponse> employees;
}
