package com.kanban.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.model.entity.AuditLog;
import com.kanban.model.enums.AuditAction;
import com.kanban.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void logAction(UUID actorId, AuditAction action, String entityType, UUID entityId, Map<String, Object> metadata) {
        AuditLog auditLog = AuditLog.builder()
            .actorId(actorId)
            .action(action)
            .entityType(entityType)
            .entityId(entityId)
            .metadata(toJson(metadata))
            .build();

        auditLogRepository.save(auditLog);
    }

    private String toJson(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return "{}";
        }

        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public void logUserRegistration(UUID userId) {
        logAction(userId, AuditAction.CREATE, "User", userId, Map.of("event", "registration"));
    }

    public void logUserLogin(UUID userId) {
        logAction(userId, AuditAction.UPDATE, "User", userId, Map.of("event", "login"));
    }

    public void logUserLogout(UUID userId) {
        logAction(userId, AuditAction.UPDATE, "User", userId, Map.of("event", "logout"));
    }

    public void logTaskCreated(UUID actorId, UUID taskId, String taskTitle) {
        logAction(actorId, AuditAction.CREATE, "Task", taskId, Map.of("title", taskTitle));
    }

    public void logTaskUpdated(UUID actorId, UUID taskId, Map<String, Object> changes) {
        logAction(actorId, AuditAction.UPDATE, "Task", taskId, changes);
    }

    public void logTaskDeleted(UUID actorId, UUID taskId, String taskTitle) {
        logAction(actorId, AuditAction.DELETE, "Task", taskId, Map.of("title", taskTitle));
    }

    public void logTaskAssigned(UUID actorId, UUID taskId, UUID assigneeId) {
        logAction(actorId, AuditAction.ASSIGN, "Task", taskId, Map.of("assigneeId", assigneeId.toString()));
    }

    public void logTaskStatusChanged(UUID actorId, UUID taskId, String oldStatus, String newStatus) {
        logAction(actorId, AuditAction.STATUS_CHANGE, "Task", taskId, 
            Map.of("oldStatus", oldStatus, "newStatus", newStatus));
    }

    public void logCommentAdded(UUID actorId, UUID commentId, UUID taskId) {
        logAction(actorId, AuditAction.COMMENT_ADD, "Comment", commentId, Map.of("taskId", taskId.toString()));
    }

    public void logCommentFlagged(UUID actorId, UUID commentId) {
        logAction(actorId, AuditAction.COMMENT_FLAG, "Comment", commentId, Map.of());
    }

    public void logAttachmentAdded(UUID actorId, UUID attachmentId, UUID taskId, String fileName) {
        logAction(actorId, AuditAction.ATTACHMENT_ADD, "Attachment", attachmentId, 
            Map.of("taskId", taskId.toString(), "fileName", fileName));
    }

    public void logAttachmentDeleted(UUID actorId, UUID attachmentId, String fileName) {
        logAction(actorId, AuditAction.ATTACHMENT_DELETE, "Attachment", attachmentId, Map.of("fileName", fileName));
    }
}
