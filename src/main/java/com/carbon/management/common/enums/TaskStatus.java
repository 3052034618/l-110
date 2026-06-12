package com.carbon.management.common.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {

    NOT_STARTED("NOT_STARTED", "未开始"),
    IN_PROGRESS("IN_PROGRESS", "进行中"),
    COMPLETED("COMPLETED", "已完成"),
    DELAYED("DELAYED", "已延期"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String desc;

    TaskStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
