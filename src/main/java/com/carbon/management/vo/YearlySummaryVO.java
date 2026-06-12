package com.carbon.management.vo;

import com.carbon.management.common.enums.EmissionScope;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class YearlySummaryVO {

    private Integer year;
    private String orgId;
    private String orgName;
    private BigDecimal totalBudget;
    private BigDecimal totalUsed;
    private BigDecimal totalRemaining;
    private BigDecimal totalTargetReduction;
    private BigDecimal totalAchievedReduction;
    private BigDecimal reductionCompletionRate;
    private Long totalTaskCount;
    private Long completedTaskCount;
    private BigDecimal taskCompletionRate;
    private Map<EmissionScope, ScopeSummaryVO> scopeSummary;
    private List<DeptSummaryVO> deptSummary;
    private List<MonthlySummaryVO> monthlyTrend;
    private Long warningCount;
    private Long handledWarningCount;
}
