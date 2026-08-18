package com.kanban.model.dto.response;

import com.kanban.model.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecordResponse {

    private UUID id;
    private UUID userId;
    private String memberName;
    private String memberEmail;
    private String department;
    private String profilePicture;
    private LocalDate workDate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private AttendanceStatus status;
    private String hoursToday;
    private int hoursTodayMinutes;
    private String weeklyAvgHours;
    private int weeklyAvgMinutes;
    private List<AttendanceBreakResponse> breaks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
