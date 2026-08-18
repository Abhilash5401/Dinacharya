package com.kanban.model.dto.request;

import com.kanban.model.enums.AttendanceStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAttendanceRequest {

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private AttendanceStatus status;

    @Valid
    private List<AttendanceBreakRequest> breaks;
}
