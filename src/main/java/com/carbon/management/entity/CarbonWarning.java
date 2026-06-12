package com.carbon.management.entity;

import com.carbon.management.common.enums.WarningLevel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "carbon_warning")
@EqualsAndHashCode(callSuper = true)
public class CarbonWarning extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warning_code", nullable = false, unique = true, length = 64)
    private String warningCode;

    @Column(name = "budget_id")
    private Long budgetId;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "org_id", length = 64)
    private String orgId;

    @Column(name = "org_name", length = 128)
    private String orgName;

    @Column(name = "dept_id", length = 64)
    private String deptId;

    @Column(name = "dept_name", length = 128)
    private String deptName;

    @Enumerated(EnumType.STRING)
    @Column(name = "warning_level", nullable = false, length = 32)
    private WarningLevel warningLevel;

    @Column(name = "warning_type", nullable = false, length = 32)
    private String warningType;

    @Column(name = "warning_title", nullable = false, length = 256)
    private String warningTitle;

    @Column(name = "warning_content", length = 1000)
    private String warningContent;

    @Column(name = "usage_rate", precision = 5, scale = 2)
    private BigDecimal usageRate;

    @Column(name = "budget_amount", precision = 18, scale = 4)
    private BigDecimal budgetAmount;

    @Column(name = "used_amount", precision = 18, scale = 4)
    private BigDecimal usedAmount;

    @Column(name = "exceed_amount", precision = 18, scale = 4)
    private BigDecimal exceedAmount;

    @Column(name = "is_handled", nullable = false)
    private Boolean isHandled = false;

    @Column(name = "handle_remark", length = 500)
    private String handleRemark;

    @Column(name = "handle_time")
    private java.time.LocalDateTime handleTime;
}
