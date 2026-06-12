package com.carbon.management.dto;

import com.carbon.management.common.enums.TodoType;
import lombok.Data;

@Data
public class TodoQueryDTO {

    private String assignee;

    private TodoType todoType;

    private Boolean isRead;

    private Boolean isCompleted;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
