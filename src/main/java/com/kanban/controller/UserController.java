package com.kanban.controller;

import com.kanban.model.dto.request.CreateMemberRequest;
import com.kanban.model.dto.request.UpdateEmployeeStatusRequest;
import com.kanban.model.dto.request.UpdateEmploymentTypeRequest;
import com.kanban.model.dto.request.UpdateMemberRequest;
import com.kanban.model.dto.request.UpdateUserRequest;
import com.kanban.model.dto.response.UserResponse;
import com.kanban.security.CustomUserDetailsService;
import com.kanban.service.DepartmentService;
import com.kanban.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UserResponse user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponse> updateCurrentUser(
        @Valid @RequestBody UpdateUserRequest request,
        Authentication authentication
    ) {
        var user = userDetailsService.loadUserEntityByEmail(authentication.getName());
        UserResponse updated = userService.updateUser(user.getId(), request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/departments")
    @Operation(summary = "List known departments from employee records")
    public ResponseEntity<List<String>> getDepartments() {
        return ResponseEntity.ok(departmentService.getDepartmentNames());
    }

    @PostMapping("/enroll")
    @Operation(summary = "Enroll a new employee and send a welcome email")
    public ResponseEntity<UserResponse> enrollMember(
        @Valid @RequestBody CreateMemberRequest request,
        Authentication authentication
    ) {
        userDetailsService.loadUserEntityByEmail(authentication.getName());
        UserResponse member = userService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update directory fields for an employee (name is not changed)")
    public ResponseEntity<UserResponse> updateDirectoryMember(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateMemberRequest request,
        Authentication authentication
    ) {
        userDetailsService.loadUserEntityByEmail(authentication.getName());
        return ResponseEntity.ok(userService.updateDirectoryMember(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee from the directory")
    public ResponseEntity<Void> deleteMember(
        @PathVariable UUID id,
        Authentication authentication
    ) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        userService.deleteMember(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Get users with optional filters")
    public ResponseEntity<Page<UserResponse>> getUsers(
        @RequestParam(required = false) String department,
        @RequestParam(required = false) String skill,
        Pageable pageable
    ) {
        Page<UserResponse> users;
        if (department != null || skill != null) {
            users = userService.getUsersByDepartmentAndSkills(department, skill, pageable);
        } else {
            users = userService.getAllUsers(pageable);
        }
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/employment-type")
    @Operation(summary = "Update employee intern / full-time / lead type")
    public ResponseEntity<UserResponse> updateEmploymentType(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateEmploymentTypeRequest request,
        Authentication authentication
    ) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        UserResponse updated = userService.updateEmploymentType(
            id, request.getEmploymentType(), currentUser.getId()
        );
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/employee-status")
    @Operation(summary = "Update employee directory status (active / onboarding / away)")
    public ResponseEntity<UserResponse> updateEmployeeStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateEmployeeStatusRequest request,
        Authentication authentication
    ) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        UserResponse updated = userService.updateEmployeeStatus(
            id, request.getEmployeeStatus(), currentUser.getId()
        );
        return ResponseEntity.ok(updated);
    }
}
