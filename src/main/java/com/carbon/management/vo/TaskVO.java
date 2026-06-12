package com.carbon.management.vo;

import com.carbon.management.common.enums.EmissionScope;
import com.carbon.management.common.enums.TaskCategory;
import com.carbon.management.common.enums.TaskStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskVO {

    private Long id;
    private String taskCode;
    private String taskName;
    private Long budgetId;
    private Long decompositionId;
    private String orgId;
    private String orgName;
    private String deptId;
    private String deptName;
    private EmissionScope emissionScope;
    private String emissionScopeDesc;
    private TaskCategory category;
    private String categoryDesc;
    private TaskStatus status;
    private String statusDesc;
    private BigDecimal estimatedReduction;
    private BigDecimal actualReduction;
    private BigDecimal completionRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String responsibilityPerson;
    private String description;
    private String measureDetail;
    private List<TaskProgressVO> progressList;
    private List<AttachmentVO> attachmentList;
    private String createdBy;
    private LocalDateTime createdTime;
}
