package com.kanban.service;

import com.kanban.model.dto.response.MailStatusResponse;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final String TEMPLATE_PATH = "templates/email/task-assignment.html";

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:noreply@dinacharya.local}")
    private String fromAddress;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    private boolean canSend() {
        return mailEnabled
            && StringUtils.hasText(mailUsername)
            && StringUtils.hasText(mailPassword)
            && mailSenderProvider.getIfAvailable() != null;
    }

    private void sendHtml(String toEmail, String subject, String html) {
        if (!canSend() || !StringUtils.hasText(toEmail)) {
            log.warn("Skipping email to {}: mail is not ready", toEmail);
            return;
        }
        try {
            JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Sent email '{}' to {}", subject, toEmail);
        } catch (Exception ex) {
            log.error("Failed to send email '{}' to {}: {}", subject, toEmail, ex.getMessage(), ex);
        }
    }

    @Async("emailExecutor")
    public void sendPasswordResetEmail(String toEmail, String employeeName, String rawToken) {
        sendPasswordResetEmailNow(toEmail, employeeName, rawToken);
    }

    public void sendPasswordResetEmailNow(String toEmail, String employeeName, String rawToken) {
        String resetUrl = frontendUrl.replaceAll("/$", "") + "/reset-password?token=" + rawToken;
        if (!canSend()) {
            log.warn("Mail is not ready. Password reset link for {}: {}", toEmail, resetUrl);
            return;
        }
        try {
            String html = loadResetTemplate()
                .replace("{{EMPLOYEE_NAME}}", escape(employeeName))
                .replace("{{RESET_URL}}", resetUrl)
                .replace("{{EXPIRE_MINUTES}}", "15");
            sendHtml(toEmail, "Reset your Dinacharya password", html);
        } catch (Exception ex) {
            log.error("Failed to send password reset email to {}: {}", toEmail, ex.getMessage(), ex);
        }
    }

    @Async("emailExecutor")
    public void sendEnrollmentEmail(String toEmail, String employeeName, String temporaryPassword) {
        String loginUrl = frontendUrl.replaceAll("/$", "") + "/login";
        String html = """
            <html><body style="font-family:Arial,sans-serif;color:#222521;background:#FBF9F5;padding:24px;">
              <div style="max-width:560px;margin:0 auto;background:#fff;border:1px solid #E8E4DC;border-radius:12px;padding:28px;">
                <p style="color:#7c3aed;letter-spacing:0.08em;text-transform:uppercase;font-size:12px;">Dinacharya</p>
                <h2>Welcome, %s</h2>
                <p>Your employee account is ready. Sign in with this work email and the temporary password below.</p>
                <p><strong>Email:</strong> %s<br/><strong>Temporary password:</strong> %s</p>
                <p><a href="%s" style="display:inline-block;background:#6d28d9;color:#fff;text-decoration:none;padding:12px 20px;border-radius:8px;">Open Dinacharya</a></p>
              </div>
            </body></html>
            """.formatted(escape(employeeName), escape(toEmail), escape(temporaryPassword), loginUrl);
        sendHtml(toEmail, "Welcome to Dinacharya", html);
    }

    public MailStatusResponse getStatus() {
        boolean credentialsConfigured = StringUtils.hasText(mailUsername) && StringUtils.hasText(mailPassword);
        boolean ready = mailEnabled && credentialsConfigured;
        String hint;
        if (ready) {
            hint = "Email notifications are on. Assign a task to an employee to send mail, or send a test from Profile.";
        } else if (!mailEnabled) {
            hint = "Mail is off. Set MAIL_ENABLED=true (or app.mail.enabled: true in application-dev.yml) and restart the backend.";
        } else {
            hint = "Mail is on but SMTP login is empty. Set MAIL_USERNAME and MAIL_PASSWORD (Gmail App Password) in application-dev.yml, then restart.";
        }
        return MailStatusResponse.builder()
            .enabled(mailEnabled)
            .credentialsConfigured(credentialsConfigured)
            .ready(ready)
            .host(mailHost)
            .from(fromAddress)
            .hint(hint)
            .build();
    }

    @Async("emailExecutor")
    public void sendTestAssignmentEmail(String toEmail, String employeeName) {
        sendTaskAssignmentEmail(
            toEmail,
            employeeName,
            "Test task assignment",
            "MEDIUM",
            java.time.LocalDateTime.now().plusDays(1),
            "This is a test email from Dinacharya. If you received it, SMTP is working.",
            java.util.UUID.fromString("00000000-0000-0000-0000-000000000001")
        );
    }

    @Async("emailExecutor")
    public void sendTaskAssignmentEmail(
        String toEmail,
        String employeeName,
        String taskTitle,
        String priority,
        LocalDateTime dueDate,
        String description,
        UUID taskId
    ) {
        if (!canSend()) {
            log.warn("Mail is not ready. Skipping assignment email for task {}", taskId);
            return;
        }
        if (!StringUtils.hasText(toEmail)) {
            log.warn("Skipping assignment email for task {}: assignee has no email", taskId);
            return;
        }

        try {
            String html = buildAssignmentHtml(employeeName, taskTitle, priority, dueDate, description, taskId);
            sendHtml(toEmail, "New task assigned: " + taskTitle, html);
            log.info("Sent task assignment email to {} for task {}", toEmail, taskId);
        } catch (Exception ex) {
            log.error("Failed to send task assignment email to {} for task {}: {}", toEmail, taskId, ex.getMessage(), ex);
        }
    }

    private String buildAssignmentHtml(
        String employeeName,
        String taskTitle,
        String priority,
        LocalDateTime dueDate,
        String description,
        UUID taskId
    ) {
        String template = loadTemplate();
        String viewUrl = frontendUrl.replaceAll("/$", "") + "/tasks";
        return template
            .replace("{{EMPLOYEE_NAME}}", escape(employeeName))
            .replace("{{TASK_TITLE}}", escape(taskTitle))
            .replace("{{PRIORITY_LABEL}}", escape(priorityLabel(priority)))
            .replace("{{PRIORITY_STYLE}}", priorityStyle(priority))
            .replace("{{DUE_DATE}}", dueDate != null ? dueDate.format(DATE_FORMAT) : "No due date")
            .replace("{{TASK_DESCRIPTION}}", escape(StringUtils.hasText(description) ? description : "No description provided."))
            .replace("{{VIEW_TASK_URL}}", viewUrl);
    }

    private String loadResetTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email/password-reset.html");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.warn("Could not load password reset template; using fallback HTML");
            return """
                <html><body style="font-family:Arial,sans-serif;color:#222521;">
                <h2>Hi {{EMPLOYEE_NAME}},</h2>
                <p>Use this link to choose a new password. It expires in {{EXPIRE_MINUTES}} minutes.</p>
                <p><a href="{{RESET_URL}}">Set a new password</a></p>
                <p>If you did not ask for this, you can ignore the email.</p>
                </body></html>
                """;
        }
    }

    private String loadTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.warn("Could not load email template {}; using fallback HTML", TEMPLATE_PATH);
            return """
                <html><body style="font-family:Arial,sans-serif;color:#222521;">
                <h2>Hi {{EMPLOYEE_NAME}},</h2>
                <p>A new task has been assigned to you.</p>
                <p><strong>{{TASK_TITLE}}</strong></p>
                <p>Priority: {{PRIORITY_LABEL}} &middot; Due: {{DUE_DATE}}</p>
                <p>{{TASK_DESCRIPTION}}</p>
                <p><a href="{{VIEW_TASK_URL}}">View Task</a></p>
                </body></html>
                """;
        }
    }

    private String priorityLabel(String priority) {
        if (!StringUtils.hasText(priority)) {
            return "Medium";
        }
        String normalized = priority.toLowerCase();
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private String priorityStyle(String priority) {
        String value = priority == null ? "MEDIUM" : priority.toUpperCase();
        return switch (value) {
            case "URGENT", "HIGH" -> "background-color:#fee2e2;color:#b91c1c;";
            case "MEDIUM" -> "background-color:#fef3c7;color:#b45309;";
            default -> "background-color:#d1fae5;color:#047857;";
        };
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
