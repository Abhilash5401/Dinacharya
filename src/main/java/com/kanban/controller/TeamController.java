package com.kanban.controller;

import com.kanban.model.dto.request.AddTeamMemberRequest;
import com.kanban.model.dto.request.CreateTeamRequest;
import com.kanban.model.dto.request.UpdateTeamRequest;
import com.kanban.model.dto.response.TaskAnalyticsResponse;
import com.kanban.model.dto.response.TeamResponse;
import com.kanban.model.dto.response.WorkloadResponse;
import com.kanban.security.CustomUserDetailsService;
import com.kanban.service.AnalyticsService;
import com.kanban.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Teams", description = "Team management APIs")
public class TeamController {

    private final TeamService teamService;
    private final AnalyticsService analyticsService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping
    @Operation(summary = "List all teams")
    public ResponseEntity<Page<TeamResponse>> getAllTeams(
        Pageable pageable,
        Authentication authentication
    ) {
        return ResponseEntity.ok(teamService.getAllTeams(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable UUID id) {
        TeamResponse team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    @PostMapping
    @Operation(summary = "Create a new team")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamResponse> createTeam(
        @Valid @RequestBody CreateTeamRequest request,
        Authentication authentication
    ) {
        var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
        TeamResponse team = teamService.createTeam(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(team);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update team")
    public ResponseEntity<TeamResponse> updateTeam(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateTeamRequest request,
        Authentication authentication
    ) {
        var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
        TeamResponse team = teamService.updateTeam(id, request, user.getId());
        return ResponseEntity.ok(team);
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Add member to team")
    public ResponseEntity<TeamResponse> addMember(
        @PathVariable UUID id,
        @Valid @RequestBody AddTeamMemberRequest request,
        Authentication authentication
    ) {
        var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
        TeamResponse team = teamService.addMember(id, request.getUserId(), user.getId());
        return ResponseEntity.ok(team);
    }

    @DeleteMapping("/{id}/members/{userId}")
    @Operation(summary = "Remove member from team")
    public ResponseEntity<TeamResponse> removeMember(
        @PathVariable UUID id,
        @PathVariable UUID userId,
        Authentication authentication
    ) {
        var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
        TeamResponse team = teamService.removeMember(id, userId, user.getId());
        return ResponseEntity.ok(team);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete team")
    public ResponseEntity<Void> deleteTeam(
        @PathVariable UUID id,
        Authentication authentication
    ) {
        var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
        teamService.deleteTeam(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/analytics/status-counts")
    @Operation(summary = "Get task status counts for team")
    public ResponseEntity<TaskAnalyticsResponse> getTeamAnalytics(@PathVariable UUID id) {
        TaskAnalyticsResponse analytics = analyticsService.getTeamAnalytics(id);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/{id}/analytics/workload")
    @Operation(summary = "Get workload distribution for team")
    public ResponseEntity<List<WorkloadResponse>> getTeamWorkload(@PathVariable UUID id) {
        List<WorkloadResponse> workload = analyticsService.getTeamWorkload(id);
        return ResponseEntity.ok(workload);
    }
}
