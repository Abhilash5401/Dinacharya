package com.kanban.controller;

import com.kanban.security.CustomUserDetailsService;
import com.kanban.service.AttachmentService;
import com.kanban.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Delete Operations", description = "APIs for deleting comments and attachments")
public class DeleteController {

    private final CommentService commentService;
    private final AttachmentService attachmentService;
    private final CustomUserDetailsService userDetailsService;

    @DeleteMapping("/comments/{id}")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<Void> deleteComment(
        @PathVariable UUID id,
        Authentication authentication
    ) {
        var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
        commentService.deleteComment(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/attachments/{id}")
    @Operation(summary = "Delete an attachment")
    public ResponseEntity<Void> deleteAttachment(
        @PathVariable UUID id,
        Authentication authentication
    ) {
        var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
        attachmentService.deleteAttachment(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
