package com.carbon.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "budget_decomposition")
@EqualsAndHashCode(callSuper = true)
public class BudgetDecomposition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget_id", nullable = false)
    private Long budgetId;

    @Column(name = "dept_id", nullable = false, length = 64)
    private String deptId;

    @Column(name = "dept_name", nullable = false, length = 128)
    private String deptName;

    @Column(name = "target_reduction", nullable = false, precision = 18, scale = 4)
    private BigDecimal targetReduction;

    @Column(name = "achieved_reduction", precision = 18, scale = 4)
    private BigDecimal achievedReduction = BigDecimal.ZERO;

    @Column(name = "completion_rate", precision = 5, scale = 2)
    private BigDecimal completionRate = BigDecimal.ZERO;

    @Column(name = "responsibility_person", length = 64)
    private String responsibilityPerson;

    @Column(name = "remark", length = 500)
    private String remark;
}
