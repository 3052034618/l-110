package com.carbon.management.entity;

import com.carbon.management.common.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "budget_adjustment")
@EqualsAndHashCode(callSuper = true)
public class BudgetAdjustment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget_id", nullable = false)
    private Long budgetId;

    @Column(name = "adjustment_no", nullable = false, unique = true, length = 64)
    private String adjustmentNo;

    @Column(name = "original_amount", nullable = false, precision = 18, scale = 4)
    private BigDecimal originalAmount;

    @Column(name = "adjusted_amount", nullable = false, precision = 18, scale = 4)
    private BigDecimal adjustedAmount;

    @Column(name = "adjustment_reason", nullable = false, length = 1000)
    private String adjustmentReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 32)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "approver", length = 64)
    private String approver;

    @Column(name = "approval_remark", length = 500)
    private String approvalRemark;

    @Column(name = "approval_time")
    private java.time.LocalDateTime approvalTime;
}
