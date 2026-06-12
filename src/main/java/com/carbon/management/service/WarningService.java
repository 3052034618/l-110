package com.carbon.management.service;

import com.carbon.management.common.PageResult;
import com.carbon.management.dto.WarningQueryDTO;
import com.carbon.management.entity.CarbonBudget;
import com.carbon.management.entity.EmissionTask;
import com.carbon.management.vo.WarningVO;

import java.util.List;

public interface WarningService {

    void checkAndCreateWarning(CarbonBudget budget);

    void checkAndCreateWarning(EmissionTask task);

    PageResult<WarningVO> queryWarnings(WarningQueryDTO dto);

    List<WarningVO> getWarningsByBudgetId(Long budgetId);

    List<WarningVO> getWarningsByTaskId(Long taskId);

    List<WarningVO> getWarningsByOrgId(String orgId);

    List<WarningVO> getUnhandledWarningsByOrgId(String orgId);

    List<WarningVO> getUnhandledWarningsByDeptId(String deptId);

    void handleWarning(Long id, String handleRemark);
}
