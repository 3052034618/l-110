package com.carbon.management.repository;

import com.carbon.management.common.enums.WarningLevel;
import com.carbon.management.entity.CarbonWarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarbonWarningRepository extends JpaRepository<CarbonWarning, Long>, JpaSpecificationExecutor<CarbonWarning> {

    Optional<CarbonWarning> findByWarningCode(String warningCode);

    List<CarbonWarning> findByBudgetId(Long budgetId);

    List<CarbonWarning> findByTaskId(Long taskId);

    List<CarbonWarning> findByOrgId(String orgId);

    List<CarbonWarning> findByDeptId(String deptId);

    List<CarbonWarning> findByWarningLevel(WarningLevel warningLevel);

    List<CarbonWarning> findByIsHandled(Boolean isHandled);

    List<CarbonWarning> findByOrgIdAndIsHandled(String orgId, Boolean isHandled);

    List<CarbonWarning> findByDeptIdAndIsHandled(String deptId, Boolean isHandled);
}
