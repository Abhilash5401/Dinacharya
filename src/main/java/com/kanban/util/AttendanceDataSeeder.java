package com.kanban.util;

import com.kanban.model.entity.AttendanceRecord;
import com.kanban.model.entity.User;
import com.kanban.model.enums.AttendanceStatus;
import com.kanban.model.enums.UserRole;
import com.kanban.repository.AttendanceRecordRepository;
import com.kanban.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;

/**
 * Seeds permanent attendance data for all ASE employees.
 * Creates historical attendance records for past 6 months including weekends excluded.
 * Runs after AseTeamSeeder to ensure employees exist.
 */
@Component
@Order(21) // After AseTeamSeeder (20)
@RequiredArgsConstructor
@Slf4j
public class AttendanceDataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        try {
            log.info("Starting Attendance Data Seeder for historical records...");

            // Get all active ASE employees (USER role)
            List<User> aseEmployees = userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.USER && Boolean.TRUE.equals(u.getIsActive()) && "ASE".equals(u.getDepartment()))
                .toList();

            if (aseEmployees.isEmpty()) {
                log.warn("No ASE employees found. Skipping attendance seeding.");
                return;
            }

            log.info("Found {} ASE employees. Generating historical attendance...", aseEmployees.size());

            // Generate data for last 6 months
            LocalDate today = LocalDate.now();
            LocalDate sixMonthsAgo = today.minusMonths(6);

            for (User employee : aseEmployees) {
                int recordsCreated = seedAttendanceForEmployee(employee, sixMonthsAgo, today);
                log.info("Created {} attendance records for {}", recordsCreated, employee.getName());
            }

            log.info("========== ATTENDANCE DATA SEEDING COMPLETED ==========");

        } catch (Exception e) {
            log.error("Error seeding attendance data", e);
        }
    }

    @Transactional
    private int seedAttendanceForEmployee(User employee, LocalDate startDate, LocalDate endDate) {
        int created = 0;

        // Only create records for dates that don't already exist
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Skip weekends
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }

            // Check if record already exists
            boolean exists = attendanceRecordRepository.findByUserIdAndWorkDate(employee.getId(), date)
                .isPresent();

            if (exists) {
                continue;
            }

            // Create attendance record
            // 80% present, 10% absent, 10% half-day
            AttendanceStatus status = getRandomStatus();
            
            AttendanceRecord record = AttendanceRecord.builder()
                .user(employee)
                .workDate(date)
                .entryTime(date.atTime(9, 0).plusMinutes(random.nextInt(30))) // 9:00 - 9:30
                .exitTime(date.atTime(18, 0).minusMinutes(random.nextInt(30))) // 17:30 - 18:00
                .status(status)
                .build();

            // For absent days, don't set entry/exit time
            if (status == AttendanceStatus.ABSENT) {
                record.setEntryTime(null);
                record.setExitTime(null);
            }

            attendanceRecordRepository.save(record);
            created++;
        }

        return created;
    }

    private AttendanceStatus getRandomStatus() {
        int chance = random.nextInt(100);
        if (chance < 80) {
            return AttendanceStatus.PRESENT;
        } else if (chance < 90) {
            return AttendanceStatus.ABSENT;
        } else {
            return AttendanceStatus.HALF_DAY;
        }
    }
}
