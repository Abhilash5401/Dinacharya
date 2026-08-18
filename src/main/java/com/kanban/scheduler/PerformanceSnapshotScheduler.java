package com.kanban.scheduler;

import com.kanban.service.EmployeePerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceSnapshotScheduler {

    private final EmployeePerformanceService employeePerformanceService;

    @Scheduled(cron = "0 0 2 * * *")
    public void computeNightlySnapshots() {
        log.info("Computing employee performance snapshots for current month");
        employeePerformanceService.computeCurrentMonthSnapshots();
        log.info("Employee performance snapshots updated");
    }
}
