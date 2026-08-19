package com.kanban.controller;

import com.kanban.model.dto.response.TaskImportResponse;
import com.kanban.service.FileImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
@Tag(name = "File Import", description = "Endpoints for importing tasks from Excel and Word files")
@SecurityRequirement(name = "bearerAuth")
public class FileImportController {

    private final FileImportService fileImportService;
    private final com.kanban.security.CustomUserDetailsService userDetailsService;

    @PostMapping(value = "/tasks/excel/{teamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    @Operation(
            summary = "Import tasks from Excel file",
            description = "Upload an Excel (.xlsx) file to import multiple tasks at once. " +
                    "Expected columns: Title, Description, Status, Priority, Due Date, Assignee Email, Team Name",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks imported successfully",
                            content = @Content(schema = @Schema(implementation = TaskImportResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid file format"),
                    @ApiResponse(responseCode = "404", description = "Team not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<TaskImportResponse> importFromExcel(
            @Parameter(description = "Excel file containing tasks", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Team ID to import tasks to", required = true)
            @PathVariable UUID teamId,
            org.springframework.security.core.Authentication authentication) {

        log.info("Received Excel import request for team: {}, file: {}", teamId, file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    TaskImportResponse.builder()
                            .message("File is empty")
                            .totalRows(0)
                            .successCount(0)
                            .failureCount(0)
                            .build()
            );
        }

        // Validate file type
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().body(
                    TaskImportResponse.builder()
                            .message("Invalid file type. Only .xlsx files are supported")
                            .totalRows(0)
                            .successCount(0)
                            .failureCount(0)
                            .build()
            );
        }

        try {
            // Get authenticated user
            var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
            TaskImportResponse response = fileImportService.importTasksFromExcel(file, teamId, user.getId());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error processing Excel file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    TaskImportResponse.builder()
                            .message("Error processing file: " + e.getMessage())
                            .totalRows(0)
                            .successCount(0)
                            .failureCount(0)
                            .build()
            );
        }
    }

    @PostMapping(value = "/tasks/word/{teamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    @Operation(
            summary = "Import tasks from Word file",
            description = "Upload a Word (.docx) file to import multiple tasks at once. " +
                    "Expected format: Table with columns: Title, Description, Status, Priority, Due Date, Assignee Email, Team Name " +
                    "OR simple format: 'Title - Description - Status - Priority - Due Date' on each line",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks imported successfully",
                            content = @Content(schema = @Schema(implementation = TaskImportResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid file format"),
                    @ApiResponse(responseCode = "404", description = "Team not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<TaskImportResponse> importFromWord(
            @Parameter(description = "Word file containing tasks", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Team ID to import tasks to", required = true)
            @PathVariable UUID teamId,
            org.springframework.security.core.Authentication authentication) {

        log.info("Received Word import request for team: {}, file: {}", teamId, file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    TaskImportResponse.builder()
                            .message("File is empty")
                            .totalRows(0)
                            .successCount(0)
                            .failureCount(0)
                            .build()
            );
        }

        // Validate file type
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".docx")) {
            return ResponseEntity.badRequest().body(
                    TaskImportResponse.builder()
                            .message("Invalid file type. Only .docx files are supported")
                            .totalRows(0)
                            .successCount(0)
                            .failureCount(0)
                            .build()
            );
        }

        try {
            // Get authenticated user
            var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
            TaskImportResponse response = fileImportService.importTasksFromWord(file, teamId, user.getId());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error processing Word file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    TaskImportResponse.builder()
                            .message("Error processing file: " + e.getMessage())
                            .totalRows(0)
                            .successCount(0)
                            .failureCount(0)
                            .build()
            );
        }
    }

    @GetMapping("/template/excel")
    @Operation(
            summary = "Download Excel template",
            description = "Download a sample Excel template for task import"
    )
    public ResponseEntity<String> getExcelTemplate() {
        String template = "Title,Description,Status,Priority,Due Date,Assignee Email,Team Name\n" +
                "Implement login feature,Create JWT-based authentication,TODO,HIGH,2024-12-31,developer@example.com,Backend Team\n" +
                "Fix UI bug,Navigation menu not responsive,IN_PROGRESS,MEDIUM,2024-12-25,designer@example.com,Frontend Team\n" +
                "Write API docs,Document all REST endpoints,TODO,LOW,2025-01-10,tech.writer@example.com,Documentation Team";

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=task-import-template.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(template);
    }

    @GetMapping("/template/info")
    @Operation(
            summary = "Get file format information",
            description = "Get detailed information about expected file formats for import"
    )
    public ResponseEntity<FileFormatInfo> getFileFormatInfo() {
        FileFormatInfo info = new FileFormatInfo();
        info.excelFormat = "Excel (.xlsx) file with columns: Title, Description, Status, Priority, Due Date, Assignee Email, Team Name";
        info.wordFormat = "Word (.docx) file with either:\n" +
                "1. Table format with columns: Title, Description, Status, Priority, Due Date, Assignee Email, Team Name\n" +
                "2. Simple format: 'Title - Description - Status - Priority - Due Date' on each line";
        info.statusValues = "TODO, IN_PROGRESS, IN_REVIEW, DONE";
        info.priorityValues = "LOW, MEDIUM, HIGH, URGENT";
        info.dateFormat = "yyyy-MM-dd, dd/MM/yyyy, or MM/dd/yyyy";
        info.requiredFields = "Title (required), Description (optional), Status (optional, defaults to TODO), " +
                "Priority (optional, defaults to MEDIUM), Due Date (optional), Assignee Email (optional), Team Name (optional)";
        
        return ResponseEntity.ok(info);
    }

    @lombok.Data
    private static class FileFormatInfo {
        private String excelFormat;
        private String wordFormat;
        private String statusValues;
        private String priorityValues;
        private String dateFormat;
        private String requiredFields;
    }
}
