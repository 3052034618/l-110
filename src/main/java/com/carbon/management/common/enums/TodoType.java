package com.carbon.management.common.enums;

import lombok.Getter;

@Getter
public enum TodoType {

    BUDGET_APPROVAL("BUDGET_APPROVAL", "预算审批"),
    TASK_APPROVAL("TASK_APPROVAL", "任务审批"),
    PROGRESS_REMIND("PROGRESS_REMIND", "进度上报提醒"),
    BUDGET_WARNING("BUDGET_WARNING", "预算预警"),
    TASK_DEADLINE("TASK_DEADLINE", "任务截止提醒");

    private final String code;
    private final String desc;

    TodoType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
