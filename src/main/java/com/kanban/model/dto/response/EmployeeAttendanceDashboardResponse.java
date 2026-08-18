package com.kanban.model.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class EmployeeAttendanceDashboardResponse {
    UUID userId;
    String userName;
    String department;
    LocalDate periodStart;
    LocalDate periodEnd;
    double overallPercent;
    int totalSessions;
    int presentCount;
    int absentCount;
    int lateCount;
    List<MonthSummary> months;

    @Value
    @Builder
    public static class MonthSummary {
        String monthKey;
        String monthLabel;
        double attendancePercent;
        int presentCount;
        int absentCount;
        int lateCount;
        int totalSessions;
        List<DayEntry> days;
    }

    @Value
    @Builder
    public static class DayEntry {
        LocalDate workDate;
        String dayLabel;
        String status;
        String note;
    }
}
