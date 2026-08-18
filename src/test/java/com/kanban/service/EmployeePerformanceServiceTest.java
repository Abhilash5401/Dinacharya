package com.kanban.service;

import com.kanban.model.enums.TaskPriority;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeePerformanceServiceTest {

    @Test
    void priorityWeight_mapsAllPriorities() {
        assertEquals(1.0, EmployeePerformanceService.priorityWeight(TaskPriority.LOW));
        assertEquals(2.0, EmployeePerformanceService.priorityWeight(TaskPriority.MEDIUM));
        assertEquals(3.0, EmployeePerformanceService.priorityWeight(TaskPriority.HIGH));
        assertEquals(4.0, EmployeePerformanceService.priorityWeight(TaskPriority.URGENT));
    }

    @Test
    void normalizeProductivity_capsAtOneHundred() {
        assertEquals(100.0, EmployeePerformanceService.normalizeProductivity(20, 10));
        assertEquals(50.0, EmployeePerformanceService.normalizeProductivity(5, 10));
        assertEquals(0.0, EmployeePerformanceService.normalizeProductivity(0, 10));
        assertEquals(100.0, EmployeePerformanceService.normalizeProductivity(5, 0));
    }

    @Test
    void completionRate_usesAssignedTasks() {
        assertEquals(0.0, EmployeePerformanceService.completionRate(0, 0));
        assertEquals(50.0, EmployeePerformanceService.completionRate(2, 4));
        assertEquals(100.0, EmployeePerformanceService.completionRate(3, 3));
    }

    @Test
    void efficiencyScore_requiresCompletedTasks() {
        assertEquals(0.0, EmployeePerformanceService.efficiencyScore(0, 0, 0));
        assertEquals(100.0, EmployeePerformanceService.efficiencyScore(2, 0, 0));
        assertEquals(75.0, EmployeePerformanceService.efficiencyScore(4, 4, 3));
    }

    @Test
    void workingDaysBetween_excludesWeekends() {
        List<LocalDate> days = EmployeePerformanceService.workingDaysBetween(
            LocalDate.of(2026, 8, 14),
            LocalDate.of(2026, 8, 18)
        );
        assertEquals(3, days.size());
        assertEquals(LocalDate.of(2026, 8, 14), days.get(0));
        assertEquals(LocalDate.of(2026, 8, 17), days.get(1));
        assertEquals(LocalDate.of(2026, 8, 18), days.get(2));
    }
}
