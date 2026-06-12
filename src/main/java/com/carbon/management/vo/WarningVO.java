package com.carbon.management.vo;

import com.carbon.management.common.enums.WarningLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WarningVO {

    private Long id;
    private String warningCode;
    private Long budgetId;
    private Long taskId;
    private String orgId;
    private String orgName;
    private String deptId;
    private String deptName;
    private WarningLevel warningLevel;
    private String warningLevelDesc;
    private String warningType;
    private String warningTitle;
    private String warningContent;
    private BigDecimal usageRate;
    private BigDecimal budgetAmount;
    private BigDecimal usedAmount;
    private BigDecimal exceedAmount;
    private Boolean isHandled;
    private String handleRemark;
    private LocalDateTime handleTime;
    private LocalDateTime createdTime;
}
