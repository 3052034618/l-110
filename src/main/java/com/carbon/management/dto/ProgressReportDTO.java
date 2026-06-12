package com.carbon.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProgressReportDTO {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotBlank(message = "上报周期不能为空")
    private String reportPeriod;

    @NotNull(message = "本期减排量不能为空")
    private BigDecimal periodReduction;

    private String progressDesc;

    private String problemDesc;

    private String nextPlan;

    @NotBlank(message = "上报人不能为空")
    private String reporter;
}
