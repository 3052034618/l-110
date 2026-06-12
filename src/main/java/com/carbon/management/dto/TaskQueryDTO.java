package com.carbon.management.dto;

import com.carbon.management.common.enums.EmissionScope;
import com.carbon.management.common.enums.TaskCategory;
import com.carbon.management.common.enums.TaskStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TaskQueryDTO {

    private String taskCode;

    private String taskName;

    private Long budgetId;

    private String orgId;

    private String deptId;

    private TaskCategory category;

    private TaskStatus status;

    private String responsibilityPerson;

    private EmissionScope emissionScope;

    private LocalDate startDateFrom;

    private LocalDate startDateTo;

    private LocalDate endDateFrom;

    private LocalDate endDateTo;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
