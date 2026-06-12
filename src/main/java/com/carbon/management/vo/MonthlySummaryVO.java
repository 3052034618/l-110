package com.carbon.management.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlySummaryVO {

    private Integer month;
    private BigDecimal budget;
    private BigDecimal used;
    private BigDecimal remaining;
    private BigDecimal reduction;
    private Long newTaskCount;
    private Long completedTaskCount;
}
