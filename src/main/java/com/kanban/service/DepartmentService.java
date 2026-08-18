package com.kanban.service;

import com.kanban.exception.ResourceNotFoundException;
import com.kanban.model.dto.request.CreateDepartmentRequest;
import com.kanban.model.entity.Department;
import com.kanban.repository.DepartmentRepository;
import com.kanban.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<String> getDepartmentNames() {
        Set<String> names = new LinkedHashSet<>();
        departmentRepository.findAllByOrderByNameAsc()
            .forEach(department -> names.add(department.getName()));
        userRepository.findDistinctDepartments()
            .forEach(names::add);
        return names.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }

    @Transactional
    public String createDepartment(CreateDepartmentRequest request) {
        String name = request.getName().trim();
        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Department already exists");
        }

        Department department = Department.builder().name(name).build();
        departmentRepository.save(department);
        return department.getName();
    }

    @Transactional
    public void deleteDepartment(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException("Department name is required");
        }

        Department department = departmentRepository.findByNameIgnoreCase(trimmed).orElse(null);
        if (department != null) {
            departmentRepository.delete(department);
        }

        int cleared = userRepository.clearDepartment(trimmed);
        if (department == null && cleared == 0) {
            throw new ResourceNotFoundException("Department not found: " + trimmed);
        }
    }
}
