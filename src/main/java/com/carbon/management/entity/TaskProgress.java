package com.carbon.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "task_progress")
@EqualsAndHashCode(callSuper = true)
public class TaskProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "report_period", nullable = false, length = 20)
    private String reportPeriod;

    @Column(name = "period_reduction", nullable = false, precision = 18, scale = 4)
    private BigDecimal periodReduction;

    @Column(name = "cumulative_reduction", nullable = false, precision = 18, scale = 4)
    private BigDecimal cumulativeReduction;

    @Column(name = "completion_rate", precision = 5, scale = 2)
    private BigDecimal completionRate;

    @Column(name = "progress_desc", length = 1000)
    private String progressDesc;

    @Column(name = "problem_desc", length = 500)
    private String problemDesc;

    @Column(name = "next_plan", length = 500)
    private String nextPlan;

    @Column(name = "reporter", nullable = false, length = 64)
    private String reporter;
}
