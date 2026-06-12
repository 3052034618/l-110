package com.carbon.management.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetDecompositionVO {

    private Long id;
    private Long budgetId;
    private String deptId;
    private String deptName;
    private BigDecimal targetReduction;
    private BigDecimal achievedReduction;
    private BigDecimal completionRate;
    private String responsibilityPerson;
    private String remark;
}
