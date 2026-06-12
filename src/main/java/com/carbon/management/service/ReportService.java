package com.carbon.management.service;

import com.carbon.management.vo.BudgetProgressVO;
import com.carbon.management.vo.YearlySummaryVO;

import java.util.List;

public interface ReportService {

    YearlySummaryVO getYearlySummary(String orgId, Integer year);

    List<BudgetProgressVO> getBudgetProgressReport(String orgId, Integer year);

    byte[] exportYearlyReport(String orgId, Integer year);
}
