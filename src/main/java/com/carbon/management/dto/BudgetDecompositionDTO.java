package com.carbon.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetDecompositionDTO {

    @NotNull(message = "预算ID不能为空")
    private Long budgetId;

    @NotNull(message = "部门ID不能为空")
    private String deptId;

    @NotNull(message = "部门名称不能为空")
    private String deptName;

    @NotNull(message = "减排目标不能为空")
    private BigDecimal targetReduction;

    private String responsibilityPerson;

    private String remark;
}
