package com.carbon.management.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttachmentVO {

    private Long id;
    private Long taskId;
    private Long progressId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String remark;
    private LocalDateTime createdTime;
}
