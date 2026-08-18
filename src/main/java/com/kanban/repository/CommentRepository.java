package com.kanban.repository;

import com.kanban.model.entity.Comment;
import com.kanban.model.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    void deleteByAuthor_Id(UUID authorId);

    Set<Comment> findByTask(Task task);

    Page<Comment> findByTaskOrderByCreatedAtDesc(Task task, Pageable pageable);

    Page<Comment> findByFlaggedTrue(Pageable pageable);

    @Query("""
        SELECT c FROM Comment c 
        WHERE c.flagged = true AND c.task.team.id = :teamId
        ORDER BY c.createdAt DESC
        """)
    Page<Comment> findFlaggedCommentsByTeam(@Param("teamId") UUID teamId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.flagged = true")
    long countFlaggedComments();

    @Query("""
        SELECT COUNT(c) FROM Comment c 
        WHERE c.flagged = true AND c.task.team.id = :teamId
        """)
    long countFlaggedCommentsByTeam(@Param("teamId") UUID teamId);
}
