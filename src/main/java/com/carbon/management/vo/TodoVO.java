package com.carbon.management.vo;

import com.carbon.management.common.enums.TodoType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoVO {

    private Long id;
    private String todoCode;
    private TodoType todoType;
    private String todoTypeDesc;
    private String title;
    private String content;
    private String relatedType;
    private Long relatedId;
    private String assignee;
    private Boolean isRead;
    private Boolean isCompleted;
    private LocalDateTime readTime;
    private LocalDateTime completeTime;
    private LocalDateTime deadline;
    private LocalDateTime createdTime;
}
