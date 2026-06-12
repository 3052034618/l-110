package com.carbon.management.common.enums;

import lombok.Getter;

@Getter
public enum WarningLevel {

    NORMAL("NORMAL", "正常"),
    ATTENTION("ATTENTION", "关注"),
    WARNING("WARNING", "预警"),
    DANGER("DANGER", "严重");

    private final String code;
    private final String desc;

    WarningLevel(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
