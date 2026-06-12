package com.carbon.management.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeptSummaryVO {

    private String deptId;
    private String deptName;
    private BigDecimal targetReduction;
    private BigDecimal achievedReduction;
    private BigDecimal completionRate;
    private Long taskCount;
    private Long completedTaskCount;
}
