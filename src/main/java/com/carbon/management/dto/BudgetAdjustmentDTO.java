package com.carbon.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetAdjustmentDTO {

    @NotNull(message = "预算ID不能为空")
    private Long budgetId;

    @NotNull(message = "调整后金额不能为空")
    private BigDecimal adjustedAmount;

    @NotBlank(message = "调整原因不能为空")
    private String adjustmentReason;
}
