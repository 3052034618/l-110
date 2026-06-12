package com.carbon.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Column(name = "created_by", nullable = false, length = 64)
    private String createdBy;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_by", length = 64)
    private String updatedBy;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @PrePersist
    public void prePersist() {
        if (createdTime == null) {
            createdTime = LocalDateTime.now();
        }
        if (createdBy == null) {
            createdBy = "system";
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedTime = LocalDateTime.now();
        if (updatedBy == null) {
            updatedBy = "system";
        }
    }
}
