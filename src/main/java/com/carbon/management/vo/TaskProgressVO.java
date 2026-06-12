package com.carbon.management.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TaskProgressVO {

    private Long id;
    private Long taskId;
    private String reportPeriod;
    private BigDecimal periodReduction;
    private BigDecimal cumulativeReduction;
    private BigDecimal completionRate;
    private String progressDesc;
    private String problemDesc;
    private String nextPlan;
    private String reporter;
    private LocalDateTime createdTime;
}
