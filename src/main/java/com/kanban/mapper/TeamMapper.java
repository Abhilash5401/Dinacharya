package com.kanban.mapper;

import com.kanban.model.dto.request.CreateTeamRequest;
import com.kanban.model.dto.request.UpdateTeamRequest;
import com.kanban.model.dto.response.TeamResponse;
import com.kanban.model.entity.Team;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {

    @Mapping(target = "taskCount", expression = "java(team.getTasks() != null ? team.getTasks().size() : 0)")
    TeamResponse toResponse(Team team);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTeamFromRequest(UpdateTeamRequest request, @MappingTarget Team team);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lead", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Team fromCreateRequest(CreateTeamRequest request);
}
