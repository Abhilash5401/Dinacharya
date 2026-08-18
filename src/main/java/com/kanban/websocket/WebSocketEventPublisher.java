package com.kanban.websocket;

import com.kanban.model.dto.response.AttachmentResponse;
import com.kanban.model.dto.response.CommentResponse;
import com.kanban.model.dto.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishTaskAssigned(TaskResponse task) {
        if (task == null || task.getAssignedTo() == null) {
            return;
        }
        if (task.getCreatedBy() != null && task.getAssignedTo().getId().equals(task.getCreatedBy().getId())) {
            return;
        }
        Map<String, Object> message = new HashMap<>();
        message.put("type", "TASK_ASSIGNED");
        message.put("task", task);
        messagingTemplate.convertAndSend("/topic/users/" + task.getAssignedTo().getId(), message);
    }

    public void publishTaskCreated(UUID teamId, TaskResponse task) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "TASK_CREATED");
        message.put("task", task);
        messagingTemplate.convertAndSend("/topic/teams/" + teamId, message);
    }

    public void publishTaskUpdated(UUID teamId, TaskResponse task) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "TASK_UPDATED");
        message.put("task", task);
        messagingTemplate.convertAndSend("/topic/teams/" + teamId, message);
    }

    public void publishTaskDeleted(UUID teamId, UUID taskId) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "TASK_DELETED");
        message.put("taskId", taskId);
        messagingTemplate.convertAndSend("/topic/teams/" + teamId, message);
    }

    public void publishCommentAdded(UUID teamId, UUID taskId, CommentResponse comment) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "COMMENT_ADDED");
        message.put("taskId", taskId);
        message.put("comment", comment);
        messagingTemplate.convertAndSend("/topic/teams/" + teamId, message);
    }

    public void publishCommentDeleted(UUID teamId, UUID taskId, UUID commentId) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "COMMENT_DELETED");
        message.put("taskId", taskId);
        message.put("commentId", commentId);
        messagingTemplate.convertAndSend("/topic/teams/" + teamId, message);
    }

    public void publishAttachmentAdded(UUID teamId, UUID taskId, AttachmentResponse attachment) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "ATTACHMENT_ADDED");
        message.put("taskId", taskId);
        message.put("attachment", attachment);
        messagingTemplate.convertAndSend("/topic/teams/" + teamId, message);
    }

    public void publishAttachmentDeleted(UUID teamId, UUID taskId, UUID attachmentId) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "ATTACHMENT_DELETED");
        message.put("taskId", taskId);
        message.put("attachmentId", attachmentId);
        messagingTemplate.convertAndSend("/topic/teams/" + teamId, message);
    }
}
