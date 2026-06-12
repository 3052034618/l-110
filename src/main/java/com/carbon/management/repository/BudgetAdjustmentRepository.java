package com.carbon.management.repository;

import com.carbon.management.common.enums.ApprovalStatus;
import com.carbon.management.entity.BudgetAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetAdjustmentRepository extends JpaRepository<BudgetAdjustment, Long>, JpaSpecificationExecutor<BudgetAdjustment> {

    Optional<BudgetAdjustment> findByAdjustmentNo(String adjustmentNo);

    List<BudgetAdjustment> findByBudgetIdOrderByCreatedTimeDesc(Long budgetId);

    List<BudgetAdjustment> findByApprovalStatus(ApprovalStatus approvalStatus);

    List<BudgetAdjustment> findByBudgetIdAndApprovalStatus(Long budgetId, ApprovalStatus approvalStatus);
}
