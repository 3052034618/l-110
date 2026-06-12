package com.carbon.management.common.enums;

import lombok.Getter;

@Getter
public enum ApprovalStatus {

    PENDING("PENDING", "待审批"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已驳回"),
    WITHDRAWN("WITHDRAWN", "已撤回");

    private final String code;
    private final String desc;

    ApprovalStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
