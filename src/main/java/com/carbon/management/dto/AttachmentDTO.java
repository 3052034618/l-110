package com.carbon.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttachmentDTO {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    private Long progressId;

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    private Long fileSize;

    private String fileType;

    private String remark;
}
