package com.carbon.management.entity;

import com.carbon.management.common.enums.EmissionScope;
import com.carbon.management.common.enums.TaskCategory;
import com.carbon.management.common.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "emission_task")
@EqualsAndHashCode(callSuper = true)
public class EmissionTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_code", nullable = false, unique = true, length = 64)
    private String taskCode;

    @Column(name = "task_name", nullable = false, length = 128)
    private String taskName;

    @Column(name = "budget_id")
    private Long budgetId;

    @Column(name = "decomposition_id")
    private Long decompositionId;

    @Column(name = "org_id", nullable = false, length = 64)
    private String orgId;

    @Column(name = "org_name", nullable = false, length = 128)
    private String orgName;

    @Column(name = "dept_id", nullable = false, length = 64)
    private String deptId;

    @Column(name = "dept_name", nullable = false, length = 128)
    private String deptName;

    @Enumerated(EnumType.STRING)
    @Column(name = "emission_scope", length = 32)
    private EmissionScope emissionScope;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 32)
    private TaskCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private TaskStatus status = TaskStatus.NOT_STARTED;

    @Column(name = "estimated_reduction", nullable = false, precision = 18, scale = 4)
    private BigDecimal estimatedReduction;

    @Column(name = "actual_reduction", precision = 18, scale = 4)
    private BigDecimal actualReduction = BigDecimal.ZERO;

    @Column(name = "completion_rate", precision = 5, scale = 2)
    private BigDecimal completionRate = BigDecimal.ZERO;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "responsibility_person", nullable = false, length = 64)
    private String responsibilityPerson;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "measure_detail", length = 2000)
    private String measureDetail;

    @Version
    private Integer version;
}
