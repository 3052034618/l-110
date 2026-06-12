package com.carbon.management.dto;

import com.carbon.management.common.enums.TaskCategory;
import com.carbon.management.common.enums.TaskStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TaskUpdateDTO {

    private String taskName;

    private TaskCategory category;

    private TaskStatus status;

    private BigDecimal estimatedReduction;

    private LocalDate startDate;

    private LocalDate endDate;

    private String responsibilityPerson;

    private String description;

    private String measureDetail;
}
