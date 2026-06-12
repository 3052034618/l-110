package com.carbon.management.dto;

import com.carbon.management.common.enums.TaskCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TaskCreateDTO {

    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    private Long budgetId;

    private Long decompositionId;

    @NotBlank(message = "组织ID不能为空")
    private String orgId;

    @NotBlank(message = "组织名称不能为空")
    private String orgName;

    @NotBlank(message = "部门ID不能为空")
    private String deptId;

    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    @NotNull(message = "任务类别不能为空")
    private TaskCategory category;

    @NotNull(message = "预计减排量不能为空")
    private BigDecimal estimatedReduction;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotBlank(message = "负责人不能为空")
    private String responsibilityPerson;

    private String description;

    private String measureDetail;
}
