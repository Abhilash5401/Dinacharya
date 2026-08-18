package com.kanban.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AttendanceAnalyticsServiceTest {

    @Test
    void workingDaysBetween_excludesWeekends() {
        List<LocalDate> days = AttendanceAnalyticsService.workingDaysBetween(
            LocalDate.of(2026, 8, 14),
            LocalDate.of(2026, 8, 18)
        );
        assertEquals(3, days.size());
        assertEquals(LocalDate.of(2026, 8, 14), days.get(0));
        assertEquals(LocalDate.of(2026, 8, 17), days.get(1));
        assertEquals(LocalDate.of(2026, 8, 18), days.get(2));
    }
}
