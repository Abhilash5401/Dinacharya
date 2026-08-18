package com.kanban.repository;

import com.kanban.model.entity.AuditLog;
import com.kanban.model.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findByActorId(UUID actorId, Pageable pageable);

    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);

    Page<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);

    @Query("""
        SELECT a FROM AuditLog a 
        WHERE (:actorId IS NULL OR a.actorId = :actorId)
        AND (:action IS NULL OR a.action = :action)
        AND (:entityType IS NULL OR a.entityType = :entityType)
        AND a.createdAt BETWEEN :from AND :to
        ORDER BY a.createdAt DESC
        """)
    Page<AuditLog> findAuditLogsWithFilters(
        @Param("actorId") UUID actorId,
        @Param("action") AuditAction action,
        @Param("entityType") String entityType,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable
    );

    @Query("DELETE FROM AuditLog a WHERE a.createdAt < :date")
    void deleteOlderThan(@Param("date") LocalDateTime date);
}
