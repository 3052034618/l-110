package com.carbon.management.dto;

import com.carbon.management.common.enums.EmissionScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetCreateDTO {

    @NotBlank(message = "预算名称不能为空")
    private String budgetName;

    @NotBlank(message = "组织ID不能为空")
    private String orgId;

    @NotBlank(message = "组织名称不能为空")
    private String orgName;

    @NotNull(message = "预算年度不能为空")
    private Integer budgetYear;

    private Integer budgetMonth;

    @NotNull(message = "排放范围不能为空")
    private EmissionScope emissionScope;

    @NotNull(message = "预算总额不能为空")
    private BigDecimal totalBudget;

    private BigDecimal warningThreshold;

    private BigDecimal dangerThreshold;

    private String description;

    private Long parentId;
}
