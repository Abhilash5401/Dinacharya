package com.kanban.controller;

import com.kanban.model.dto.response.MailStatusResponse;
import com.kanban.security.CustomUserDetailsService;
import com.kanban.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Mail", description = "Email notification status and test send")
public class MailController {

    private final EmailService emailService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/status")
    @Operation(summary = "Check whether SMTP email notifications are configured")
    public ResponseEntity<MailStatusResponse> status() {
        return ResponseEntity.ok(emailService.getStatus());
    }

    @PostMapping("/test")
    @Operation(summary = "Send a test assignment email to the logged-in user")
    public ResponseEntity<MailStatusResponse> sendTest(Authentication authentication) {
        var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
        emailService.sendTestAssignmentEmail(user.getEmail(), user.getName());
        return ResponseEntity.ok(emailService.getStatus());
    }
}
