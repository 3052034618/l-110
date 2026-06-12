package com.carbon.management.common.enums;

import lombok.Getter;

@Getter
public enum EmissionScope {

    SCOPE_1("SCOPE_1", "范围1：直接排放"),
    SCOPE_2("SCOPE_2", "范围2：间接排放（能源）"),
    SCOPE_3("SCOPE_3", "范围3：其他间接排放");

    private final String code;
    private final String desc;

    EmissionScope(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
