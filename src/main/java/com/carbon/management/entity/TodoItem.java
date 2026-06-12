package com.carbon.management.entity;

import com.carbon.management.common.enums.TodoType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "todo_item")
@EqualsAndHashCode(callSuper = true)
public class TodoItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "todo_code", nullable = false, unique = true, length = 64)
    private String todoCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "todo_type", nullable = false, length = 32)
    private TodoType todoType;

    @Column(name = "title", nullable = false, length = 256)
    private String title;

    @Column(name = "content", length = 1000)
    private String content;

    @Column(name = "related_type", length = 32)
    private String relatedType;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "assignee", nullable = false, length = 64)
    private String assignee;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Column(name = "read_time")
    private java.time.LocalDateTime readTime;

    @Column(name = "complete_time")
    private java.time.LocalDateTime completeTime;

    @Column(name = "deadline")
    private java.time.LocalDateTime deadline;
}
