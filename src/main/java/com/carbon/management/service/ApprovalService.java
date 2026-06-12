package com.carbon.management.service;

import com.carbon.management.dto.ApprovalDTO;
import com.carbon.management.vo.ApprovalRecordVO;

import java.util.List;

public interface ApprovalService {

    Long createApprovalRecord(ApprovalDTO dto);

    void processApproval(Long id, ApprovalDTO dto);

    List<ApprovalRecordVO> getApprovalRecords(String businessType, Long businessId);

    List<ApprovalRecordVO> getApprovalRecordsByBusinessNo(String businessNo);

    List<ApprovalRecordVO> getPendingApprovals(String approver);
}
