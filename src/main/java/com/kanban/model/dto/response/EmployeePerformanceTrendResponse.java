package com.kanban.model.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class EmployeePerformanceTrendResponse {
    UUID userId;
    String userName;
    List<TrendPoint> points;

    @Value
    @Builder
    public static class TrendPoint {
        LocalDate periodStart;
        LocalDate periodEnd;
        double productivityScore;
        double efficiencyScore;
        double disciplineScore;
        double performanceIndex;
        Double rollingIndex;
    }
}
