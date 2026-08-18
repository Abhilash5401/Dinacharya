package com.kanban.controller;

import com.kanban.model.dto.response.AttachmentResponse;
import com.kanban.security.CustomUserDetailsService;
import com.kanban.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/attachments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Attachments", description = "Attachment management APIs")
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping
    @Operation(summary = "Get attachments for a task")
    public ResponseEntity<Set<AttachmentResponse>> getAttachmentsByTask(@PathVariable UUID taskId) {
        Set<AttachmentResponse> attachments = attachmentService.getAttachmentsByTask(taskId);
        return ResponseEntity.ok(attachments);
    }

    @PostMapping
    @Operation(summary = "Upload an attachment to a task")
    public ResponseEntity<AttachmentResponse> uploadAttachment(
        @PathVariable UUID taskId,
        @RequestParam("file") MultipartFile file,
        Authentication authentication
    ) throws IOException {
        var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
        AttachmentResponse attachment = attachmentService.uploadAttachment(taskId, file, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
    }
}
