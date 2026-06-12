package com.carbon.management.vo;

import com.carbon.management.common.enums.ApprovalStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BudgetAdjustmentVO {

    private Long id;
    private Long budgetId;
    private String adjustmentNo;
    private BigDecimal originalAmount;
    private BigDecimal adjustedAmount;
    private BigDecimal difference;
    private String adjustmentReason;
    private ApprovalStatus approvalStatus;
    private String approvalStatusDesc;
    private String approver;
    private String approvalRemark;
    private LocalDateTime approvalTime;
    private String createdBy;
    private LocalDateTime createdTime;
}
