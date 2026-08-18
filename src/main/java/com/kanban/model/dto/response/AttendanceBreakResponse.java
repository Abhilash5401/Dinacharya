package com.kanban.model.dto.response;

import com.kanban.model.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceBreakResponse {

    private UUID id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String duration;
}
