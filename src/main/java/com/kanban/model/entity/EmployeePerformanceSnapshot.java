package com.kanban.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "employee_performance_snapshots",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "period_start", "period_end"}),
    indexes = {
        @Index(name = "idx_perf_snapshot_user", columnList = "user_id"),
        @Index(name = "idx_perf_snapshot_period", columnList = "period_start, period_end")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeePerformanceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "productivity_score", nullable = false)
    private double productivityScore;

    @Column(name = "efficiency_score", nullable = false)
    private double efficiencyScore;

    @Column(name = "discipline_score", nullable = false)
    private double disciplineScore;

    @Column(name = "performance_index", nullable = false)
    private double performanceIndex;

    @Column(name = "rolling_index")
    private Double rollingIndex;

    @Column(name = "tasks_completed", nullable = false)
    private int tasksCompleted;

    @Column(name = "tasks_assigned", nullable = false)
    private int tasksAssigned;

    @Column(name = "on_time_tasks", nullable = false)
    private int onTimeTasks;

    @Column(name = "attendance_days", nullable = false)
    private int attendanceDays;

    @Column(name = "working_days", nullable = false)
    private int workingDays;

    @CreationTimestamp
    @Column(name = "computed_at", nullable = false, updatable = false)
    private LocalDateTime computedAt;
}
