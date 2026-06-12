package com.carbon.management.entity;

import com.carbon.management.common.enums.BudgetStatus;
import com.carbon.management.common.enums.EmissionScope;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "carbon_budget")
@EqualsAndHashCode(callSuper = true)
public class CarbonBudget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget_code", nullable = false, unique = true, length = 64)
    private String budgetCode;

    @Column(name = "budget_name", nullable = false, length = 128)
    private String budgetName;

    @Column(name = "org_id", nullable = false, length = 64)
    private String orgId;

    @Column(name = "org_name", nullable = false, length = 128)
    private String orgName;

    @Column(name = "budget_year", nullable = false)
    private Integer budgetYear;

    @Column(name = "budget_month")
    private Integer budgetMonth;

    @Enumerated(EnumType.STRING)
    @Column(name = "emission_scope", nullable = false, length = 32)
    private EmissionScope emissionScope;

    @Column(name = "total_budget", nullable = false, precision = 18, scale = 4)
    private BigDecimal totalBudget;

    @Column(name = "used_amount", precision = 18, scale = 4)
    private BigDecimal usedAmount = BigDecimal.ZERO;

    @Column(name = "remaining_amount", precision = 18, scale = 4)
    private BigDecimal remainingAmount = BigDecimal.ZERO;

    @Column(name = "warning_threshold", precision = 5, scale = 2)
    private BigDecimal warningThreshold = new BigDecimal("80.00");

    @Column(name = "danger_threshold", precision = 5, scale = 2)
    private BigDecimal dangerThreshold = new BigDecimal("95.00");

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private BudgetStatus status = BudgetStatus.DRAFT;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "parent_id")
    private Long parentId;

    @Version
    private Integer version;
}
