package com.carbon.management.service;

import com.carbon.management.common.PageResult;
import com.carbon.management.dto.BudgetAdjustmentDTO;
import com.carbon.management.dto.BudgetCreateDTO;
import com.carbon.management.dto.BudgetDecompositionDTO;
import com.carbon.management.dto.BudgetQueryDTO;
import com.carbon.management.dto.BudgetUpdateDTO;
import com.carbon.management.vo.BudgetAdjustmentVO;
import com.carbon.management.vo.BudgetDecompositionVO;
import com.carbon.management.vo.BudgetProgressVO;
import com.carbon.management.vo.BudgetVO;

import java.util.List;

public interface BudgetService {

    Long createBudget(BudgetCreateDTO dto);

    void updateBudget(Long id, BudgetUpdateDTO dto);

    void deleteBudget(Long id);

    BudgetVO getBudgetById(Long id);

    BudgetVO getBudgetByCode(String budgetCode);

    PageResult<BudgetVO> queryBudgets(BudgetQueryDTO dto);

    List<BudgetVO> getBudgetsByOrgAndYear(String orgId, Integer year);

    List<BudgetVO> getBudgetsByOrgAndYearAndMonth(String orgId, Integer year, Integer month);

    Long createDecomposition(BudgetDecompositionDTO dto);

    void updateDecomposition(Long id, BudgetDecompositionDTO dto);

    void deleteDecomposition(Long id);

    List<BudgetDecompositionVO> getDecompositionsByBudgetId(Long budgetId);

    List<BudgetDecompositionVO> getDecompositionsByDeptId(String deptId);

    Long createAdjustment(BudgetAdjustmentDTO dto);

    List<BudgetAdjustmentVO> getAdjustmentsByBudgetId(Long budgetId);

    List<BudgetProgressVO> getBudgetProgressList(String orgId, Integer year);

    BudgetProgressVO getBudgetProgress(Long budgetId);

    void submitForApproval(Long budgetId);
}
