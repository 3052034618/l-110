package com.carbon.management.dto;

import com.carbon.management.common.enums.WarningLevel;
import lombok.Data;

@Data
public class WarningQueryDTO {

    private Long budgetId;

    private Long taskId;

    private String orgId;

    private String deptId;

    private WarningLevel warningLevel;

    private String warningType;

    private Boolean isHandled;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
