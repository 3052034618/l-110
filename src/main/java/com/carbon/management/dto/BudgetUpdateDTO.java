package com.carbon.management.dto;

import com.carbon.management.common.enums.BudgetStatus;
import com.carbon.management.common.enums.EmissionScope;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetUpdateDTO {

    private String budgetName;

    private String orgName;

    private EmissionScope emissionScope;

    private BigDecimal totalBudget;

    private BigDecimal warningThreshold;

    private BigDecimal dangerThreshold;

    private BudgetStatus status;

    private String description;
}
