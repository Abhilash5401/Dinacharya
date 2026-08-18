package com.kanban.mapper;

import com.kanban.model.dto.response.CommentResponse;
import com.kanban.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "taskId", source = "task.id")
    CommentResponse toResponse(Comment comment);
}
