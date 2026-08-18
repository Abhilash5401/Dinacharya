package com.kanban.repository;

import com.kanban.model.entity.Attachment;
import com.kanban.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    void deleteByUploadedBy_Id(UUID userId);

    Set<Attachment> findByTask(Task task);

    long countByTask(Task task);
}
