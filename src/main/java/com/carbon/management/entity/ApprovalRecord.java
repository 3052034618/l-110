package com.carbon.management.entity;

import com.carbon.management.common.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "approval_record")
@EqualsAndHashCode(callSuper = true)
public class ApprovalRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_type", nullable = false, length = 32)
    private String businessType;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "business_no", length = 64)
    private String businessNo;

    @Column(name = "approval_node", length = 64)
    private String approvalNode;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 32)
    private ApprovalStatus approvalStatus;

    @Column(name = "approver", nullable = false, length = 64)
    private String approver;

    @Column(name = "approver_name", length = 64)
    private String approverName;

    @Column(name = "approval_remark", length = 500)
    private String approvalRemark;

    @Column(name = "approval_time")
    private java.time.LocalDateTime approvalTime;
}
