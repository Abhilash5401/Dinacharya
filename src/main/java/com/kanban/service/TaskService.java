package com.kanban.service;

import com.kanban.exception.OptimisticLockException;
import com.kanban.exception.ResourceNotFoundException;
import com.kanban.exception.UnauthorizedException;
import com.kanban.mapper.TaskMapper;
import com.kanban.model.dto.request.CreateTaskRequest;
import com.kanban.model.dto.request.UpdateTaskRequest;
import com.kanban.model.dto.response.TaskResponse;
import com.kanban.model.entity.Task;
import com.kanban.model.entity.Team;
import com.kanban.model.entity.User;
import com.kanban.model.enums.TaskPriority;
import com.kanban.model.enums.TaskStatus;
import com.kanban.model.enums.UserRole;
import com.kanban.repository.TaskRepository;
import com.kanban.websocket.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final TeamService teamService;
    private final AuditService auditService;
    private final WebSocketEventPublisher webSocketEventPublisher;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByFilters(
        UUID teamId,
        TaskStatus status,
        TaskPriority priority,
        UUID assignedToId,
        UUID currentUserId,
        Pageable pageable
    ) {
        User currentUser = userService.getUserEntityById(currentUserId);

        if (teamId != null) {
            return taskRepository.findTasksByFilters(teamId, status, priority, assignedToId, pageable)
                .map(taskMapper::toResponse);
        }

        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.MODERATOR) {
            return taskRepository.findAllTasksByFilters(status, priority, assignedToId, pageable)
                .map(taskMapper::toResponse);
        }

        Set<UUID> teamIds = teamService.getAccessibleTeamIds(currentUserId);
        if (teamIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return taskRepository.findTasksByTeamIds(teamIds, status, priority, assignedToId, pageable)
            .map(taskMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getOverdueTasks(Pageable pageable) {
        return taskRepository.findOverdueTasks(LocalDateTime.now(), pageable)
            .map(taskMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getMyTasks(UUID userId, Pageable pageable) {
        return taskRepository.findEmployeeWorkspaceTasks(userId, pageable)
            .map(taskMapper::toResponse);
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, UUID createdById) {
        User createdBy = userService.getUserEntityById(createdById);
        boolean privileged = createdBy.getRole() == UserRole.ADMIN || createdBy.getRole() == UserRole.MODERATOR;
        Team team;

        if (request.getTeamId() != null) {
            team = teamService.getTeamEntityById(request.getTeamId());
            if (!teamService.isMember(team, createdBy) && !privileged) {
                teamService.ensureMember(team, createdBy);
            }
        } else {
            team = teamService.getOrCreatePersonalTeam(createdBy);
        }

        Task task = taskMapper.fromCreateRequest(request);
        task.setCreatedBy(createdBy);
        task.setTeam(team);
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);

        if (request.getAssignedToId() != null) {
            User assignee = userService.getUserEntityById(request.getAssignedToId());
            if (!teamService.isMember(team, assignee) && !privileged && !assignee.getId().equals(createdById)) {
                throw new UnauthorizedException("You can only assign daily tasks to yourself");
            }
            teamService.ensureMember(team, assignee);
            task.setAssignedTo(assignee);
        } else if (!privileged) {
            teamService.ensureMember(team, createdBy);
            task.setAssignedTo(createdBy);
        }

        task = taskRepository.save(task);
        auditService.logTaskCreated(createdById, task.getId(), task.getTitle());

        TaskResponse response = taskMapper.toResponse(task);
        webSocketEventPublisher.publishTaskCreated(team.getId(), response);
        webSocketEventPublisher.publishTaskAssigned(response);
        notifyAssigneeAfterCommit(task);

        return response;
    }

    @Transactional
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request, UUID currentUserId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        validateTaskEditPermission(task, currentUserId);

        UUID previousAssigneeId = task.getAssignedTo() != null ? task.getAssignedTo().getId() : null;

        Map<String, Object> changes = new HashMap<>();

        if (request.getTitle() != null) {
            changes.put("title", Map.of("old", task.getTitle(), "new", request.getTitle()));
        }
        if (request.getPriority() != null) {
            changes.put("priority", Map.of("old", task.getPriority(), "new", request.getPriority()));
        }
        if (request.getDeadline() != null) {
            changes.put("deadline", Map.of("old", task.getDeadline(), "new", request.getDeadline()));
        }

        taskMapper.updateTaskFromRequest(request, task);

        if (request.getAssignedToId() != null) {
            User assignee = userService.getUserEntityById(request.getAssignedToId());
            teamService.ensureMember(task.getTeam(), assignee);
            task.setAssignedTo(assignee);
            changes.put("assignedTo", assignee.getId().toString());
        }

        try {
            task = taskRepository.save(task);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockException("Task was modified by another user. Please refresh and try again.");
        }

        auditService.logTaskUpdated(currentUserId, task.getId(), changes);

        TaskResponse response = taskMapper.toResponse(task);
        webSocketEventPublisher.publishTaskUpdated(task.getTeam().getId(), response);
        if (request.getAssignedToId() != null && !request.getAssignedToId().equals(previousAssigneeId)) {
            webSocketEventPublisher.publishTaskAssigned(response);
            notifyAssigneeAfterCommit(task);
        }

        return response;
    }

    @Transactional
    public TaskResponse updateTaskStatus(UUID id, TaskStatus newStatus, UUID currentUserId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        User currentUser = userService.getUserEntityById(currentUserId);
        boolean isCreator = task.getCreatedBy().getId().equals(currentUserId);
        boolean isAssignee = task.getAssignedTo() != null && task.getAssignedTo().getId().equals(currentUserId);
        boolean isTeamLead = task.getTeam().getLead().getId().equals(currentUserId);
        boolean isModerator = currentUser.getRole() == UserRole.MODERATOR || currentUser.getRole() == UserRole.ADMIN;
        if (!isCreator && !isAssignee && !isTeamLead && !isModerator) {
            throw new UnauthorizedException("You don't have permission to update this task status");
        }

        TaskStatus oldStatus = task.getStatus();
        task.setStatus(newStatus);

        try {
            task = taskRepository.save(task);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockException("Task was modified by another user. Please refresh and try again.");
        }

        auditService.logTaskStatusChanged(currentUserId, task.getId(), oldStatus.name(), newStatus.name());

        TaskResponse response = taskMapper.toResponse(task);
        webSocketEventPublisher.publishTaskUpdated(task.getTeam().getId(), response);

        return response;
    }

    @Transactional
    public TaskResponse assignTask(UUID id, UUID assigneeId, UUID currentUserId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        validateTaskEditPermission(task, currentUserId);

        User assignee = userService.getUserEntityById(assigneeId);
        teamService.ensureMember(task.getTeam(), assignee);
        task.setAssignedTo(assignee);

        try {
            task = taskRepository.save(task);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockException("Task was modified by another user. Please refresh and try again.");
        }

        auditService.logTaskAssigned(currentUserId, task.getId(), assigneeId);

        TaskResponse response = taskMapper.toResponse(task);
        webSocketEventPublisher.publishTaskUpdated(task.getTeam().getId(), response);
        webSocketEventPublisher.publishTaskAssigned(response);
        notifyAssigneeAfterCommit(task);

        return response;
    }

    @Transactional
    public void deleteTask(UUID id, UUID currentUserId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        validateTaskEditPermission(task, currentUserId);

        String taskTitle = task.getTitle();
        UUID teamId = task.getTeam().getId();

        if (task.getComments() != null) {
            task.getComments().clear();
        }
        if (task.getAttachments() != null) {
            task.getAttachments().clear();
        }
        if (task.getLabels() != null) {
            task.getLabels().clear();
        }

        taskRepository.delete(task);
        auditService.logTaskDeleted(currentUserId, id, taskTitle);

        webSocketEventPublisher.publishTaskDeleted(teamId, id);
    }

    private void validateTaskEditPermission(Task task, UUID currentUserId) {
        User currentUser = userService.getUserEntityById(currentUserId);

        boolean isCreator = task.getCreatedBy().getId().equals(currentUserId);
        boolean isTeamLead = task.getTeam().getLead().getId().equals(currentUserId);
        boolean isModerator = currentUser.getRole() == UserRole.MODERATOR || currentUser.getRole() == UserRole.ADMIN;

        if (!isCreator && !isTeamLead && !isModerator) {
            throw new UnauthorizedException("You don't have permission to edit this task");
        }
    }

    private void notifyAssigneeAfterCommit(Task task) {
        if (task.getAssignedTo() == null || task.getAssignedTo().getEmail() == null) {
            return;
        }
        if (task.getCreatedBy() != null && task.getAssignedTo().getId().equals(task.getCreatedBy().getId())) {
            return;
        }

        String toEmail = task.getAssignedTo().getEmail();
        String employeeName = task.getAssignedTo().getName();
        String title = task.getTitle();
        String priority = task.getPriority() != null ? task.getPriority().name() : TaskPriority.MEDIUM.name();
        LocalDateTime dueDate = task.getDeadline();
        String description = task.getDescription();
        UUID taskId = task.getId();

        Runnable sendMail = () -> emailService.sendTaskAssignmentEmail(
            toEmail, employeeName, title, priority, dueDate, description, taskId
        );

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendMail.run();
                }
            });
            return;
        }

        sendMail.run();
    }

    @Transactional(readOnly = true)
    public Task getTaskEntityById(UUID id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }
}
