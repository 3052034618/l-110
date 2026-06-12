package com.carbon.management.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetProgressVO {

    private Long budgetId;
    private String budgetCode;
    private String budgetName;
    private String orgName;
    private BigDecimal totalBudget;
    private BigDecimal usedAmount;
    private BigDecimal remainingAmount;
    private BigDecimal usageRate;
    private String warningLevel;
    private String warningLevelDesc;
    private Boolean overBudgetRisk;
    private Long relatedTaskCount;
    private BigDecimal achievedReduction;
    private BigDecimal targetReduction;
    private BigDecimal reductionRate;
}
