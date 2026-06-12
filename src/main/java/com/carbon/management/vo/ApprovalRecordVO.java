package com.carbon.management.vo;

import com.carbon.management.common.enums.ApprovalStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalRecordVO {

    private Long id;
    private String businessType;
    private Long businessId;
    private String businessNo;
    private String approvalNode;
    private ApprovalStatus approvalStatus;
    private String approvalStatusDesc;
    private String approver;
    private String approverName;
    private String approvalRemark;
    private LocalDateTime approvalTime;
    private LocalDateTime createdTime;
}
