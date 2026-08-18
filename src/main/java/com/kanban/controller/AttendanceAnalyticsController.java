package com.kanban.controller;

import com.kanban.model.dto.response.EmployeeAttendanceDashboardResponse;
import com.kanban.model.entity.User;
import com.kanban.security.CustomUserDetailsService;
import com.kanban.service.AttendanceAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/analytics/attendance")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Attendance Analytics", description = "Employee attendance dashboard APIs")
public class AttendanceAnalyticsController {

    private final AttendanceAnalyticsService attendanceAnalyticsService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/employees/{userId}")
    @Operation(summary = "Get attendance dashboard for an employee")
    public ResponseEntity<EmployeeAttendanceDashboardResponse> getEmployeeDashboard(
        @PathVariable UUID userId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        Authentication authentication
    ) {
        User currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        return ResponseEntity.ok(
            attendanceAnalyticsService.getDashboard(
                userId,
                currentUser.getId(),
                currentUser.getRole(),
                from,
                to
            )
        );
    }
}
