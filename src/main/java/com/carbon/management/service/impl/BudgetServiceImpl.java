package com.carbon.management.service.impl;

import com.carbon.management.common.PageResult;
import com.carbon.management.common.enums.ApprovalStatus;
import com.carbon.management.common.enums.BudgetStatus;
import com.carbon.management.common.enums.EmissionScope;
import com.carbon.management.common.enums.TaskStatus;
import com.carbon.management.common.enums.WarningLevel;
import com.carbon.management.common.exception.BusinessException;
import com.carbon.management.dto.ApprovalDTO;
import com.carbon.management.dto.BudgetAdjustmentDTO;
import com.carbon.management.dto.BudgetCreateDTO;
import com.carbon.management.dto.BudgetDecompositionDTO;
import com.carbon.management.dto.BudgetQueryDTO;
import com.carbon.management.dto.BudgetUpdateDTO;
import com.carbon.management.entity.BudgetAdjustment;
import com.carbon.management.entity.BudgetDecomposition;
import com.carbon.management.entity.CarbonBudget;
import com.carbon.management.entity.EmissionTask;
import com.carbon.management.repository.BudgetAdjustmentRepository;
import com.carbon.management.repository.BudgetDecompositionRepository;
import com.carbon.management.repository.CarbonBudgetRepository;
import com.carbon.management.repository.EmissionTaskRepository;
import com.carbon.management.service.ApprovalService;
import com.carbon.management.service.BudgetService;
import com.carbon.management.vo.BudgetAdjustmentVO;
import com.carbon.management.vo.BudgetDecompositionVO;
import com.carbon.management.vo.BudgetProgressVO;
import com.carbon.management.vo.BudgetVO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final CarbonBudgetRepository budgetRepository;
    private final BudgetDecompositionRepository decompositionRepository;
    private final BudgetAdjustmentRepository adjustmentRepository;
    private final EmissionTaskRepository taskRepository;
    private final ApprovalService approvalService;

    @Override
    @Transactional
    public Long createBudget(BudgetCreateDTO dto) {
        CarbonBudget budget = new CarbonBudget();
        BeanUtils.copyProperties(dto, budget);
        budget.setBudgetCode(generateBudgetCode(dto.getOrgId(), dto.getBudgetYear()));
        budget.setRemainingAmount(dto.getTotalBudget());
        budget.setUsedAmount(BigDecimal.ZERO);
        if (dto.getWarningThreshold() == null) {
            budget.setWarningThreshold(new BigDecimal("80.00"));
        }
        if (dto.getDangerThreshold() == null) {
            budget.setDangerThreshold(new BigDecimal("95.00"));
        }
        budget.setStatus(BudgetStatus.DRAFT);
        CarbonBudget saved = budgetRepository.save(budget);
        return saved.getId();
    }

    @Override
    @Transactional
    public void updateBudget(Long id, BudgetUpdateDTO dto) {
        CarbonBudget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预算不存在"));
        if (budget.getStatus() != BudgetStatus.DRAFT && dto.getStatus() == null) {
            throw new BusinessException("非草稿状态的预算不能修改");
        }
        if (dto.getBudgetName() != null) {
            budget.setBudgetName(dto.getBudgetName());
        }
        if (dto.getOrgName() != null) {
            budget.setOrgName(dto.getOrgName());
        }
        if (dto.getEmissionScope() != null) {
            budget.setEmissionScope(dto.getEmissionScope());
        }
        if (dto.getTotalBudget() != null) {
            BigDecimal diff = dto.getTotalBudget().subtract(budget.getTotalBudget());
            budget.setTotalBudget(dto.getTotalBudget());
            budget.setRemainingAmount(budget.getRemainingAmount().add(diff));
        }
        if (dto.getWarningThreshold() != null) {
            budget.setWarningThreshold(dto.getWarningThreshold());
        }
        if (dto.getDangerThreshold() != null) {
            budget.setDangerThreshold(dto.getDangerThreshold());
        }
        if (dto.getStatus() != null) {
            budget.setStatus(dto.getStatus());
        }
        if (dto.getDescription() != null) {
            budget.setDescription(dto.getDescription());
        }
        budgetRepository.save(budget);
    }

    @Override
    @Transactional
    public void deleteBudget(Long id) {
        CarbonBudget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预算不存在"));
        if (budget.getStatus() != BudgetStatus.DRAFT && budget.getStatus() != BudgetStatus.REJECTED) {
            throw new BusinessException("只能删除草稿或已驳回状态的预算");
        }
        List<BudgetDecomposition> decompositions = decompositionRepository.findByBudgetId(id);
        if (!decompositions.isEmpty()) {
            decompositionRepository.deleteAll(decompositions);
        }
        budgetRepository.delete(budget);
    }

    @Override
    public BudgetVO getBudgetById(Long id) {
        CarbonBudget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预算不存在"));
        return convertToBudgetVO(budget);
    }

    @Override
    public BudgetVO getBudgetByCode(String budgetCode) {
        CarbonBudget budget = budgetRepository.findByBudgetCode(budgetCode)
                .orElseThrow(() -> new BusinessException("预算不存在"));
        return convertToBudgetVO(budget);
    }

    @Override
    public PageResult<BudgetVO> queryBudgets(BudgetQueryDTO dto) {
        Specification<CarbonBudget> spec = buildBudgetSpecification(dto);
        PageRequest pageRequest = PageRequest.of(
                dto.getPageNum() - 1,
                dto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdTime")
        );
        Page<CarbonBudget> page = budgetRepository.findAll(spec, pageRequest);
        List<BudgetVO> records = page.getContent().stream()
                .map(this::convertToBudgetVO)
                .collect(Collectors.toList());
        return PageResult.of(records, page.getTotalElements(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    public List<BudgetVO> getBudgetsByOrgAndYear(String orgId, Integer year) {
        return budgetRepository.findByOrgIdAndBudgetYear(orgId, year).stream()
                .map(this::convertToBudgetVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetVO> getBudgetsByOrgAndYearAndMonth(String orgId, Integer year, Integer month) {
        return budgetRepository.findByOrgIdAndBudgetYearAndBudgetMonth(orgId, year, month).stream()
                .map(this::convertToBudgetVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createDecomposition(BudgetDecompositionDTO dto) {
        CarbonBudget budget = budgetRepository.findById(dto.getBudgetId())
                .orElseThrow(() -> new BusinessException("预算不存在"));
        List<BudgetDecomposition> existing = decompositionRepository.findByBudgetId(dto.getBudgetId());
        BigDecimal allocated = existing.stream()
                .map(BudgetDecomposition::getTargetReduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (allocated.add(dto.getTargetReduction()).compareTo(budget.getTotalBudget()) > 0) {
            throw new BusinessException("分解目标总和不能超过预算总额");
        }
        BudgetDecomposition decomposition = new BudgetDecomposition();
        BeanUtils.copyProperties(dto, decomposition);
        decomposition.setAchievedReduction(BigDecimal.ZERO);
        decomposition.setCompletionRate(BigDecimal.ZERO);
        BudgetDecomposition saved = decompositionRepository.save(decomposition);
        return saved.getId();
    }

    @Override
    @Transactional
    public void updateDecomposition(Long id, BudgetDecompositionDTO dto) {
        BudgetDecomposition decomposition = decompositionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分解记录不存在"));
        if (dto.getTargetReduction() != null) {
            decomposition.setTargetReduction(dto.getTargetReduction());
            updateCompletionRate(decomposition);
        }
        if (dto.getDeptName() != null) {
            decomposition.setDeptName(dto.getDeptName());
        }
        if (dto.getResponsibilityPerson() != null) {
            decomposition.setResponsibilityPerson(dto.getResponsibilityPerson());
        }
        if (dto.getRemark() != null) {
            decomposition.setRemark(dto.getRemark());
        }
        decompositionRepository.save(decomposition);
    }

    @Override
    @Transactional
    public void deleteDecomposition(Long id) {
        BudgetDecomposition decomposition = decompositionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分解记录不存在"));
        decompositionRepository.delete(decomposition);
    }

    @Override
    public List<BudgetDecompositionVO> getDecompositionsByBudgetId(Long budgetId) {
        return decompositionRepository.findByBudgetId(budgetId).stream()
                .map(this::convertToDecompositionVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetDecompositionVO> getDecompositionsByDeptId(String deptId) {
        return decompositionRepository.findByDeptId(deptId).stream()
                .map(this::convertToDecompositionVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createAdjustment(BudgetAdjustmentDTO dto) {
        CarbonBudget budget = budgetRepository.findById(dto.getBudgetId())
                .orElseThrow(() -> new BusinessException("预算不存在"));
        BudgetAdjustment adjustment = new BudgetAdjustment();
        adjustment.setBudgetId(dto.getBudgetId());
        adjustment.setAdjustmentNo(generateAdjustmentNo());
        adjustment.setOriginalAmount(budget.getTotalBudget());
        adjustment.setAdjustedAmount(dto.getAdjustedAmount());
        adjustment.setAdjustmentReason(dto.getAdjustmentReason());
        adjustment.setApprovalStatus(ApprovalStatus.PENDING);
        BudgetAdjustment saved = adjustmentRepository.save(adjustment);

        ApprovalDTO approvalDTO = new ApprovalDTO();
        approvalDTO.setBusinessId(saved.getId());
        approvalDTO.setBusinessType("BUDGET_ADJUSTMENT");
        approvalDTO.setBusinessNo(saved.getAdjustmentNo());
        approvalDTO.setApprovalStatus(ApprovalStatus.PENDING);
        approvalDTO.setApprover("admin");
        approvalService.createApprovalRecord(approvalDTO);

        return saved.getId();
    }

    @Override
    public List<BudgetAdjustmentVO> getAdjustmentsByBudgetId(Long budgetId) {
        return adjustmentRepository.findByBudgetIdOrderByCreatedTimeDesc(budgetId).stream()
                .map(this::convertToAdjustmentVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetProgressVO> getBudgetProgressList(String orgId, Integer year) {
        List<CarbonBudget> budgets = budgetRepository.findByOrgIdAndBudgetYear(orgId, year);
        return budgets.stream()
                .map(this::buildBudgetProgress)
                .collect(Collectors.toList());
    }

    @Override
    public BudgetProgressVO getBudgetProgress(Long budgetId) {
        CarbonBudget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BusinessException("预算不存在"));
        return buildBudgetProgress(budget);
    }

    @Override
    @Transactional
    public void submitForApproval(Long budgetId) {
        CarbonBudget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BusinessException("预算不存在"));
        if (budget.getStatus() != BudgetStatus.DRAFT) {
            throw new BusinessException("只有草稿状态的预算可以提交审批");
        }
        budget.setStatus(BudgetStatus.PENDING_APPROVAL);
        budgetRepository.save(budget);

        ApprovalDTO approvalDTO = new ApprovalDTO();
        approvalDTO.setBusinessId(budgetId);
        approvalDTO.setBusinessType("BUDGET");
        approvalDTO.setBusinessNo(budget.getBudgetCode());
        approvalDTO.setApprovalStatus(ApprovalStatus.PENDING);
        approvalDTO.setApprover("admin");
        approvalService.createApprovalRecord(approvalDTO);
    }

    private BudgetProgressVO buildBudgetProgress(CarbonBudget budget) {
        BudgetProgressVO vo = new BudgetProgressVO();
        vo.setBudgetId(budget.getId());
        vo.setBudgetCode(budget.getBudgetCode());
        vo.setBudgetName(budget.getBudgetName());
        vo.setOrgName(budget.getOrgName());
        vo.setTotalBudget(budget.getTotalBudget());
        vo.setUsedAmount(budget.getUsedAmount());
        vo.setRemainingAmount(budget.getRemainingAmount());

        BigDecimal usageRate = budget.getTotalBudget().compareTo(BigDecimal.ZERO) > 0
                ? budget.getUsedAmount().multiply(new BigDecimal("100"))
                .divide(budget.getTotalBudget(), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        vo.setUsageRate(usageRate);

        WarningLevel level = determineWarningLevel(usageRate, budget.getWarningThreshold(), budget.getDangerThreshold());
        vo.setWarningLevel(level.getCode());
        vo.setWarningLevelDesc(level.getDesc());
        vo.setOverBudgetRisk(usageRate.compareTo(budget.getWarningThreshold()) >= 0);

        List<EmissionTask> tasks = taskRepository.findByBudgetId(budget.getId());
        vo.setRelatedTaskCount((long) tasks.size());

        BigDecimal achievedReduction = tasks.stream()
                .map(EmissionTask::getActualReduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setAchievedReduction(achievedReduction);

        List<BudgetDecomposition> decompositions = decompositionRepository.findByBudgetId(budget.getId());
        BigDecimal targetReduction = decompositions.stream()
                .map(BudgetDecomposition::getTargetReduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTargetReduction(targetReduction);

        vo.setReductionRate(targetReduction.compareTo(BigDecimal.ZERO) > 0
                ? achievedReduction.multiply(new BigDecimal("100"))
                .divide(targetReduction, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        return vo;
    }

    private WarningLevel determineWarningLevel(BigDecimal usageRate, BigDecimal warningThreshold, BigDecimal dangerThreshold) {
        if (usageRate.compareTo(dangerThreshold) >= 0) {
            return WarningLevel.DANGER;
        } else if (usageRate.compareTo(warningThreshold) >= 0) {
            return WarningLevel.WARNING;
        } else if (usageRate.compareTo(new BigDecimal("60")) >= 0) {
            return WarningLevel.ATTENTION;
        }
        return WarningLevel.NORMAL;
    }

    private Specification<CarbonBudget> buildBudgetSpecification(BudgetQueryDTO dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dto.getBudgetCode() != null && !dto.getBudgetCode().isEmpty()) {
                predicates.add(cb.like(root.get("budgetCode"), "%" + dto.getBudgetCode() + "%"));
            }
            if (dto.getBudgetName() != null && !dto.getBudgetName().isEmpty()) {
                predicates.add(cb.like(root.get("budgetName"), "%" + dto.getBudgetName() + "%"));
            }
            if (dto.getOrgId() != null && !dto.getOrgId().isEmpty()) {
                predicates.add(cb.equal(root.get("orgId"), dto.getOrgId()));
            }
            if (dto.getBudgetYear() != null) {
                predicates.add(cb.equal(root.get("budgetYear"), dto.getBudgetYear()));
            }
            if (dto.getBudgetMonth() != null) {
                predicates.add(cb.equal(root.get("budgetMonth"), dto.getBudgetMonth()));
            }
            if (dto.getEmissionScope() != null) {
                predicates.add(cb.equal(root.get("emissionScope"), dto.getEmissionScope()));
            }
            if (dto.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), dto.getStatus()));
            }
            if (dto.getParentId() != null) {
                predicates.add(cb.equal(root.get("parentId"), dto.getParentId()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private BudgetVO convertToBudgetVO(CarbonBudget budget) {
        BudgetVO vo = new BudgetVO();
        BeanUtils.copyProperties(budget, vo);
        vo.setEmissionScopeDesc(budget.getEmissionScope().getDesc());
        vo.setStatusDesc(budget.getStatus().getDesc());
        if (budget.getTotalBudget().compareTo(BigDecimal.ZERO) > 0) {
            vo.setUsageRate(budget.getUsedAmount().multiply(new BigDecimal("100"))
                    .divide(budget.getTotalBudget(), 2, RoundingMode.HALF_UP));
        } else {
            vo.setUsageRate(BigDecimal.ZERO);
        }
        return vo;
    }

    private BudgetDecompositionVO convertToDecompositionVO(BudgetDecomposition decomposition) {
        BudgetDecompositionVO vo = new BudgetDecompositionVO();
        BeanUtils.copyProperties(decomposition, vo);
        return vo;
    }

    private BudgetAdjustmentVO convertToAdjustmentVO(BudgetAdjustment adjustment) {
        BudgetAdjustmentVO vo = new BudgetAdjustmentVO();
        BeanUtils.copyProperties(adjustment, vo);
        vo.setDifference(adjustment.getAdjustedAmount().subtract(adjustment.getOriginalAmount()));
        vo.setApprovalStatusDesc(adjustment.getApprovalStatus().getDesc());
        return vo;
    }

    private void updateCompletionRate(BudgetDecomposition decomposition) {
        if (decomposition.getTargetReduction().compareTo(BigDecimal.ZERO) > 0) {
            decomposition.setCompletionRate(
                    decomposition.getAchievedReduction().multiply(new BigDecimal("100"))
                            .divide(decomposition.getTargetReduction(), 2, RoundingMode.HALF_UP)
            );
        }
    }

    private String generateBudgetCode(String orgId, Integer year) {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return String.format("BGT-%s-%d-%s", orgId, year, suffix);
    }

    private String generateAdjustmentNo() {
        return "ADJ-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
    }
}
