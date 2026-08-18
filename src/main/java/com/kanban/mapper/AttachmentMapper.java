package com.kanban.mapper;

import com.kanban.model.dto.response.AttachmentResponse;
import com.kanban.model.entity.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttachmentMapper {

    @Mapping(target = "taskId", source = "task.id")
    AttachmentResponse toResponse(Attachment attachment);
}
