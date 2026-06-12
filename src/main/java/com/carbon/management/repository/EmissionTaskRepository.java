package com.carbon.management.repository;

import com.carbon.management.common.enums.TaskCategory;
import com.carbon.management.common.enums.TaskStatus;
import com.carbon.management.entity.EmissionTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmissionTaskRepository extends JpaRepository<EmissionTask, Long>, JpaSpecificationExecutor<EmissionTask> {

    Optional<EmissionTask> findByTaskCode(String taskCode);

    List<EmissionTask> findByBudgetId(Long budgetId);

    List<EmissionTask> findByDecompositionId(Long decompositionId);

    List<EmissionTask> findByDeptId(String deptId);

    List<EmissionTask> findByOrgId(String orgId);

    List<EmissionTask> findByStatus(TaskStatus status);

    List<EmissionTask> findByCategory(TaskCategory category);

    List<EmissionTask> findByResponsibilityPerson(String responsibilityPerson);

    List<EmissionTask> findByEndDateBeforeAndStatusNot(LocalDate endDate, TaskStatus status);

    List<EmissionTask> findByBudgetIdIn(List<Long> budgetIds);

    boolean existsByTaskCode(String taskCode);
}
