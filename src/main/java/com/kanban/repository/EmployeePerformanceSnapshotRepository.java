package com.kanban.repository;

import com.kanban.model.entity.EmployeePerformanceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeePerformanceSnapshotRepository extends JpaRepository<EmployeePerformanceSnapshot, UUID> {

    Optional<EmployeePerformanceSnapshot> findByUserIdAndPeriodStartAndPeriodEnd(
        UUID userId,
        LocalDate periodStart,
        LocalDate periodEnd
    );

    @Query("""
        SELECT s FROM EmployeePerformanceSnapshot s
        JOIN FETCH s.user u
        WHERE s.periodStart = :periodStart AND s.periodEnd = :periodEnd
        AND (:department IS NULL OR u.department = :department)
        ORDER BY s.performanceIndex DESC
        """)
    List<EmployeePerformanceSnapshot> findByPeriod(
        @Param("periodStart") LocalDate periodStart,
        @Param("periodEnd") LocalDate periodEnd,
        @Param("department") String department
    );

    @Query("""
        SELECT s FROM EmployeePerformanceSnapshot s
        WHERE s.user.id = :userId
        AND s.periodStart >= :from
        ORDER BY s.periodStart ASC
        """)
    List<EmployeePerformanceSnapshot> findTrendByUser(
        @Param("userId") UUID userId,
        @Param("from") LocalDate from
    );

    @Modifying
    void deleteByUserIdAndPeriodStartAndPeriodEnd(UUID userId, LocalDate periodStart, LocalDate periodEnd);

    void deleteByUser_Id(UUID userId);

    @Query("""
        SELECT s FROM EmployeePerformanceSnapshot s
        WHERE s.user.id = :userId
        AND s.periodStart < :before
        ORDER BY s.periodStart DESC
        """)
    List<EmployeePerformanceSnapshot> findRecentSnapshotsBefore(
        @Param("userId") UUID userId,
        @Param("before") LocalDate before
    );
}
