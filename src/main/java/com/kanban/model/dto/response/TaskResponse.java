package com.kanban.model.dto.response;

import com.kanban.model.enums.TaskPriority;
import com.kanban.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private String remark;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime deadline;
    private UserResponse assignedTo;
    private UserResponse createdBy;
    private UUID teamId;
    private String teamName;
    private Set<String> labels;
    private Integer commentCount;
    private Integer attachmentCount;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
