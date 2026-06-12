package com.carbon.management.common.enums;

import lombok.Getter;

@Getter
public enum TaskCategory {

    ENERGY_SAVING("ENERGY_SAVING", "节电节能"),
    FUEL_SUBSTITUTION("FUEL_SUBSTITUTION", "替代燃料"),
    GREEN_PROCUREMENT("GREEN_PROCUREMENT", "绿色采购"),
    PROCESS_OPTIMIZATION("PROCESS_OPTIMIZATION", "工艺优化"),
    WASTE_RECYCLING("WASTE_RECYCLING", "废弃物回收"),
    CARBON_SINK("CARBON_SINK", "碳汇/植树造林"),
    OTHER("OTHER", "其他");

    private final String code;
    private final String desc;

    TaskCategory(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
