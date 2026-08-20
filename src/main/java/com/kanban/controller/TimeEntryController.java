package com.kanban.controller;

import com.kanban.model.dto.request.CreateTimeEntryRequest;
import com.kanban.model.dto.request.UpdateTimeEntryRequest;
import com.kanban.model.dto.response.TimeEntryResponse;
import com.kanban.service.TimeEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/time-entries")
@RequiredArgsConstructor
@Tag(name = "Time Tracking", description = "Attendance and time tracking APIs")
@SecurityRequirement(name = "bearerAuth")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @PostMapping
    @Operation(summary = "Create a time entry")
    public ResponseEntity<TimeEntryResponse> createTimeEntry(
            @Valid @RequestBody CreateTimeEntryRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        TimeEntryResponse response = timeEntryService.createTimeEntry(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{entryId}")
    @Operation(summary = "Update a time entry")
    public ResponseEntity<TimeEntryResponse> updateTimeEntry(
            @PathVariable UUID entryId,
            @Valid @RequestBody UpdateTimeEntryRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        TimeEntryResponse response = timeEntryService.updateTimeEntry(entryId, request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{entryId}")
    @Operation(summary = "Get time entry by ID")
    public ResponseEntity<TimeEntryResponse> getTimeEntry(@PathVariable UUID entryId) {
        TimeEntryResponse response = timeEntryService.getTimeEntry(entryId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{entryId}")
    @Operation(summary = "Delete a time entry")
    public ResponseEntity<Void> deleteTimeEntry(@PathVariable UUID entryId) {
        timeEntryService.deleteTimeEntry(entryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get time entries by date")
    public ResponseEntity<Page<TimeEntryResponse>> getTimeEntriesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TimeEntryResponse> response = timeEntryService.getTimeEntriesByDate(date, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's time entries")
    public ResponseEntity<List<TimeEntryResponse>> getTodayTimeEntries() {
        List<TimeEntryResponse> response = timeEntryService.getTodayTimeEntries();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's time entries within date range")
    public ResponseEntity<Page<TimeEntryResponse>> getUserTimeEntries(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TimeEntryResponse> response = timeEntryService.getUserTimeEntries(userId, startDate, endDate, page, size);
        return ResponseEntity.ok(response);
    }
}
