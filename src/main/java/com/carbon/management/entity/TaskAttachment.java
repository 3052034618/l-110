package com.carbon.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "task_attachment")
@EqualsAndHashCode(callSuper = true)
public class TaskAttachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "progress_id")
    private Long progressId;

    @Column(name = "file_name", nullable = false, length = 256)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type", length = 64)
    private String fileType;

    @Column(name = "remark", length = 256)
    private String remark;
}
