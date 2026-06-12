package com.carbon.management.repository;

import com.carbon.management.common.enums.BudgetStatus;
import com.carbon.management.common.enums.EmissionScope;
import com.carbon.management.entity.CarbonBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarbonBudgetRepository extends JpaRepository<CarbonBudget, Long>, JpaSpecificationExecutor<CarbonBudget> {

    Optional<CarbonBudget> findByBudgetCode(String budgetCode);

    List<CarbonBudget> findByOrgIdAndBudgetYear(String orgId, Integer budgetYear);

    List<CarbonBudget> findByOrgIdAndBudgetYearAndBudgetMonth(String orgId, Integer budgetYear, Integer budgetMonth);

    List<CarbonBudget> findByOrgIdAndBudgetYearAndEmissionScope(String orgId, Integer budgetYear, EmissionScope emissionScope);

    List<CarbonBudget> findByOrgIdAndBudgetYearAndBudgetMonthAndEmissionScope(
            String orgId, Integer budgetYear, Integer budgetMonth, EmissionScope emissionScope);

    List<CarbonBudget> findByParentId(Long parentId);

    List<CarbonBudget> findByStatus(BudgetStatus status);

    List<CarbonBudget> findByOrgIdInAndBudgetYear(List<String> orgIds, Integer budgetYear);

    boolean existsByBudgetCode(String budgetCode);
}
