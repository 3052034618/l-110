package com.carbon.management.service.impl;

import com.carbon.management.common.enums.ApprovalStatus;
import com.carbon.management.common.exception.BusinessException;
import com.carbon.management.dto.ApprovalDTO;
import com.carbon.management.entity.ApprovalRecord;
import com.carbon.management.entity.BudgetAdjustment;
import com.carbon.management.entity.CarbonBudget;
import com.carbon.management.repository.ApprovalRecordRepository;
import com.carbon.management.repository.BudgetAdjustmentRepository;
import com.carbon.management.repository.CarbonBudgetRepository;
import com.carbon.management.service.ApprovalService;
import com.carbon.management.service.TodoService;
import com.carbon.management.vo.ApprovalRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRecordRepository approvalRecordRepository;
    private final CarbonBudgetRepository budgetRepository;
    private final BudgetAdjustmentRepository budgetAdjustmentRepository;
    private final TodoService todoService;

    @Override
    @Transactional
    public Long createApprovalRecord(ApprovalDTO dto) {
        ApprovalRecord record = new ApprovalRecord();
        BeanUtils.copyProperties(dto, record);
        record.setCreatedBy(dto.getApprover());
        ApprovalRecord saved = approvalRecordRepository.save(record);

        todoService.createTodo(
                dto.getApprover(),
                dto.getBusinessType() + "_APPROVAL",
                "待审批：" + dto.getBusinessNo(),
                "请及时处理审批事项",
                dto.getBusinessType(),
                dto.getBusinessId(),
                null
        );

        return saved.getId();
    }

    @Override
    @Transactional
    public void processApproval(Long id, ApprovalDTO dto) {
        ApprovalRecord record = approvalRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("审批记录不存在"));
        if (record.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("该审批已处理");
        }

        record.setApprovalStatus(dto.getApprovalStatus());
        record.setApprovalRemark(dto.getApprovalRemark());
        record.setApprovalTime(LocalDateTime.now());
        record.setApprover(dto.getApprover());
        record.setApproverName(dto.getApproverName());
        approvalRecordRepository.save(record);

        handleApprovalResult(record, dto);
    }

    private void handleApprovalResult(ApprovalRecord record, ApprovalDTO dto) {
        String businessType = record.getBusinessType();
        Long businessId = record.getBusinessId();

        if ("BUDGET".equals(businessType)) {
            CarbonBudget budget = budgetRepository.findById(businessId).orElse(null);
            if (budget != null) {
                if (dto.getApprovalStatus() == ApprovalStatus.APPROVED) {
                    budget.setStatus(com.carbon.management.common.enums.BudgetStatus.APPROVED);
                } else if (dto.getApprovalStatus() == ApprovalStatus.REJECTED) {
                    budget.setStatus(com.carbon.management.common.enums.BudgetStatus.REJECTED);
                }
                budgetRepository.save(budget);
            }
        } else if ("BUDGET_ADJUSTMENT".equals(businessType)) {
            BudgetAdjustment adjustment = budgetAdjustmentRepository.findById(businessId).orElse(null);
            if (adjustment != null) {
                adjustment.setApprovalStatus(dto.getApprovalStatus());
                adjustment.setApprover(dto.getApprover());
                adjustment.setApproverName(dto.getApproverName());
                adjustment.setApprovalRemark(dto.getApprovalRemark());
                adjustment.setApprovalTime(LocalDateTime.now());
                budgetAdjustmentRepository.save(adjustment);

                if (dto.getApprovalStatus() == ApprovalStatus.APPROVED) {
                    CarbonBudget budget = budgetRepository.findById(adjustment.getBudgetId()).orElse(null);
                    if (budget != null) {
                        java.math.BigDecimal diff = adjustment.getAdjustedAmount().subtract(budget.getTotalBudget());
                        budget.setTotalBudget(adjustment.getAdjustedAmount());
                        budget.setRemainingAmount(budget.getRemainingAmount().add(diff));
                        budget.setStatus(com.carbon.management.common.enums.BudgetStatus.ADJUSTED);
                        budgetRepository.save(budget);
                    }
                }
            }
        }
    }

    @Override
    public List<ApprovalRecordVO> getApprovalRecords(String businessType, Long businessId) {
        return approvalRecordRepository.findByBusinessTypeAndBusinessIdOrderByCreatedTimeAsc(businessType, businessId).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApprovalRecordVO> getApprovalRecordsByBusinessNo(String businessNo) {
        return approvalRecordRepository.findByBusinessNoOrderByCreatedTimeAsc(businessNo).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApprovalRecordVO> getPendingApprovals(String approver) {
        return approvalRecordRepository.findByApproverAndApprovalStatus(approver, ApprovalStatus.PENDING).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private ApprovalRecordVO convertToVO(ApprovalRecord record) {
        ApprovalRecordVO vo = new ApprovalRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setApprovalStatusDesc(record.getApprovalStatus().getDesc());
        return vo;
    }
}
