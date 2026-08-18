package com.kanban.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponse {

    private UUID id;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private UUID taskId;
    private UserResponse uploadedBy;
    private LocalDateTime uploadedAt;
}
