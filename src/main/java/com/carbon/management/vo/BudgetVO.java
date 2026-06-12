package com.carbon.management.vo;

import com.carbon.management.common.enums.BudgetStatus;
import com.carbon.management.common.enums.EmissionScope;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BudgetVO {

    private Long id;
    private String budgetCode;
    private String budgetName;
    private String orgId;
    private String orgName;
    private Integer budgetYear;
    private Integer budgetMonth;
    private EmissionScope emissionScope;
    private String emissionScopeDesc;
    private BigDecimal totalBudget;
    private BigDecimal usedAmount;
    private BigDecimal remainingAmount;
    private BigDecimal usageRate;
    private BigDecimal warningThreshold;
    private BigDecimal dangerThreshold;
    private BudgetStatus status;
    private String statusDesc;
    private String description;
    private Long parentId;
    private String createdBy;
    private LocalDateTime createdTime;
}
