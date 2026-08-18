package com.kanban.service;

import com.kanban.model.dto.response.TaskAnalyticsResponse;
import com.kanban.model.dto.response.WorkloadResponse;
import com.kanban.model.entity.Team;
import com.kanban.model.entity.User;
import com.kanban.model.enums.TaskPriority;
import com.kanban.model.enums.TaskStatus;
import com.kanban.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TaskRepository taskRepository;
    private final TeamService teamService;

    public TaskAnalyticsResponse getTeamAnalytics(UUID teamId) {
        Team team = teamService.getTeamEntityById(teamId);

        Map<String, Long> statusCounts = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            long count = taskRepository.countByTeamAndStatus(teamId, status);
            statusCounts.put(status.name(), count);
        }

        Map<String, Long> priorityCounts = Arrays.stream(TaskPriority.values())
            .collect(Collectors.toMap(
                Enum::name,
                priority -> 0L
            ));

        long totalTasks = team.getTasks().size();
        long overdueTasks = taskRepository.findOverdueTasksByTeam(teamId, LocalDateTime.now()).size();
        long completedTasks = statusCounts.getOrDefault(TaskStatus.DONE.name(), 0L);

        // Calculate priority counts from team tasks
        team.getTasks().forEach(task -> {
            String priority = task.getPriority().name();
            priorityCounts.put(priority, priorityCounts.getOrDefault(priority, 0L) + 1);
        });

        return TaskAnalyticsResponse.builder()
            .statusCounts(statusCounts)
            .priorityCounts(priorityCounts)
            .totalTasks(totalTasks)
            .overdueTasks(overdueTasks)
            .completedTasks(completedTasks)
            .build();
    }

    public List<WorkloadResponse> getTeamWorkload(UUID teamId) {
        Team team = teamService.getTeamEntityById(teamId);

        return team.getMembers().stream()
            .map(member -> {
                long assignedTasks = team.getTasks().stream()
                    .filter(task -> task.getAssignedTo() != null && task.getAssignedTo().getId().equals(member.getId()))
                    .count();

                long completedTasks = team.getTasks().stream()
                    .filter(task -> task.getAssignedTo() != null && 
                                    task.getAssignedTo().getId().equals(member.getId()) &&
                                    task.getStatus() == TaskStatus.DONE)
                    .count();

                long inProgressTasks = team.getTasks().stream()
                    .filter(task -> task.getAssignedTo() != null && 
                                    task.getAssignedTo().getId().equals(member.getId()) &&
                                    task.getStatus() == TaskStatus.IN_PROGRESS)
                    .count();

                long overdueTasks = team.getTasks().stream()
                    .filter(task -> task.getAssignedTo() != null && 
                                    task.getAssignedTo().getId().equals(member.getId()) &&
                                    task.getDeadline() != null &&
                                    task.getDeadline().isBefore(LocalDateTime.now()) &&
                                    task.getStatus() != TaskStatus.DONE)
                    .count();

                return WorkloadResponse.builder()
                    .userId(member.getId())
                    .userName(member.getName())
                    .assignedTasks(assignedTasks)
                    .completedTasks(completedTasks)
                    .inProgressTasks(inProgressTasks)
                    .overdueTasks(overdueTasks)
                    .build();
            })
            .collect(Collectors.toList());
    }
}
