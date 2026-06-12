package com.carbon.management.repository;

import com.carbon.management.entity.BudgetDecomposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetDecompositionRepository extends JpaRepository<BudgetDecomposition, Long>, JpaSpecificationExecutor<BudgetDecomposition> {

    List<BudgetDecomposition> findByBudgetId(Long budgetId);

    List<BudgetDecomposition> findByDeptId(String deptId);

    List<BudgetDecomposition> findByBudgetIdIn(List<Long> budgetIds);

    List<BudgetDecomposition> findByDeptIdAndBudgetIdIn(String deptId, List<Long> budgetIds);
}
