package com.kanban.model.dto.request;

import com.kanban.model.enums.AttendanceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class CreateAttendanceRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    private LocalDate workDate;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private AttendanceStatus status;

    @Valid
    private List<AttendanceBreakRequest> breaks;
}
