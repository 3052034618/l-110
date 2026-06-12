package com.carbon.management.common.enums;

import lombok.Getter;

@Getter
public enum BudgetStatus {

    DRAFT("DRAFT", "草稿"),
    PENDING_APPROVAL("PENDING_APPROVAL", "待审批"),
    APPROVED("APPROVED", "已审批"),
    REJECTED("REJECTED", "已驳回"),
    ADJUSTED("ADJUSTED", "已调整"),
    ARCHIVED("ARCHIVED", "已归档");

    private final String code;
    private final String desc;

    BudgetStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
