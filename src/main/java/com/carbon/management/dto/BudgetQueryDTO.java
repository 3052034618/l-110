package com.carbon.management.dto;

import com.carbon.management.common.enums.BudgetStatus;
import com.carbon.management.common.enums.EmissionScope;
import lombok.Data;

@Data
public class BudgetQueryDTO {

    private String budgetCode;

    private String budgetName;

    private String orgId;

    private Integer budgetYear;

    private Integer budgetMonth;

    private EmissionScope emissionScope;

    private BudgetStatus status;

    private Long parentId;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
