package com.carbon.management.repository;

import com.carbon.management.entity.TaskProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskProgressRepository extends JpaRepository<TaskProgress, Long>, JpaSpecificationExecutor<TaskProgress> {

    List<TaskProgress> findByTaskIdOrderByCreatedTimeDesc(Long taskId);

    List<TaskProgress> findByTaskIdAndReportPeriod(Long taskId, String reportPeriod);

    List<TaskProgress> findByTaskIdInOrderByCreatedTimeDesc(List<Long> taskIds);

    List<TaskProgress> findByReporterOrderByCreatedTimeDesc(String reporter);
}
