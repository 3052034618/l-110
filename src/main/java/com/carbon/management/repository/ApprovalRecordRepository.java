package com.carbon.management.repository;

import com.carbon.management.common.enums.ApprovalStatus;
import com.carbon.management.entity.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {

    List<ApprovalRecord> findByBusinessTypeAndBusinessIdOrderByCreatedTimeAsc(String businessType, Long businessId);

    List<ApprovalRecord> findByBusinessNoOrderByCreatedTimeAsc(String businessNo);

    List<ApprovalRecord> findByApproverOrderByCreatedTimeDesc(String approver);

    List<ApprovalRecord> findByApproverAndApprovalStatus(String approver, ApprovalStatus approvalStatus);

    List<ApprovalRecord> findByBusinessTypeAndApprovalStatus(String businessType, ApprovalStatus approvalStatus);
}
