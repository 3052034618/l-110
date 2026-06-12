package com.carbon.management.vo;

import com.carbon.management.common.enums.EmissionScope;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScopeSummaryVO {

    private EmissionScope emissionScope;
    private String emissionScopeDesc;
    private BigDecimal budget;
    private BigDecimal used;
    private BigDecimal remaining;
    private BigDecimal usageRate;
}
