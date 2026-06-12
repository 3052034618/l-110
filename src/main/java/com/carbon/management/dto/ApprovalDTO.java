package com.carbon.management.dto;

import com.carbon.management.common.enums.ApprovalStatus;
import lombok.Data;

@Data
public class ApprovalDTO {

    @jakarta.validation.constraints.NotNull(message = "业务ID不能为空")
    private Long businessId;

    @jakarta.validation.constraints.NotBlank(message = "业务类型不能为空")
    private String businessType;

    private String businessNo;

    private String approvalNode;

    @jakarta.validation.constraints.NotNull(message = "审批状态不能为空")
    private ApprovalStatus approvalStatus;

    @jakarta.validation.constraints.NotBlank(message = "审批人不能为空")
    private String approver;

    private String approverName;

    private String approvalRemark;
}
