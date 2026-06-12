package com.carbon.management.repository;

import com.carbon.management.entity.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    List<TaskAttachment> findByTaskId(Long taskId);

    List<TaskAttachment> findByProgressId(Long progressId);

    List<TaskAttachment> findByTaskIdOrProgressId(Long taskId, Long progressId);

    void deleteByTaskId(Long taskId);
}
