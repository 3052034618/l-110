package com.carbon.management.service.impl;

import com.carbon.management.common.PageResult;
import com.carbon.management.common.enums.WarningLevel;
import com.carbon.management.common.exception.BusinessException;
import com.carbon.management.dto.WarningQueryDTO;
import com.carbon.management.entity.CarbonBudget;
import com.carbon.management.entity.CarbonWarning;
import com.carbon.management.entity.EmissionTask;
import com.carbon.management.repository.CarbonWarningRepository;
import com.carbon.management.service.TodoService;
import com.carbon.management.service.WarningService;
import com.carbon.management.vo.WarningVO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarningServiceImpl implements WarningService {

    private final CarbonWarningRepository warningRepository;
    private final TodoService todoService;

    @Override
    @Transactional
    public void checkAndCreateWarning(CarbonBudget budget) {
        if (budget.getTotalBudget().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal usageRate = budget.getUsedAmount().multiply(new BigDecimal("100"))
                .divide(budget.getTotalBudget(), 2, RoundingMode.HALF_UP);

        WarningLevel level = WarningLevel.NORMAL;
        if (usageRate.compareTo(budget.getDangerThreshold()) >= 0) {
            level = WarningLevel.DANGER;
        } else if (usageRate.compareTo(budget.getWarningThreshold()) >= 0) {
            level = WarningLevel.WARNING;
        }

        if (level != WarningLevel.NORMAL) {
            List<CarbonWarning> existing = warningRepository.findByBudgetId(budget.getId());
            boolean hasUnHandled = existing.stream().anyMatch(w -> !w.getIsHandled()
                    && w.getWarningLevel() == level);
            if (!hasUnHandled) {
                createBudgetWarning(budget, level, usageRate);
            }
        }
    }

    @Override
    @Transactional
    public void checkAndCreateWarning(EmissionTask task) {
        LocalDate today = LocalDate.now();
        if (task.getEndDate() != null && today.isAfter(task.getEndDate())
                && task.getStatus() != com.carbon.management.common.enums.TaskStatus.COMPLETED) {
            List<CarbonWarning> existing = warningRepository.findByTaskId(task.getId());
            boolean hasUnHandled = existing.stream().anyMatch(w -> !w.getIsHandled()
                    && "TASK_DELAYED".equals(w.getWarningType()));
            if (!hasUnHandled) {
                createTaskDelayedWarning(task);
            }
        }

        if (task.getCompletionRate().compareTo(new BigDecimal("100")) < 0) {
            long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, task.getEndDate());
            if (daysRemaining <= 7 && daysRemaining >= 0) {
                List<CarbonWarning> existing = warningRepository.findByTaskId(task.getId());
                boolean hasUnHandled = existing.stream().anyMatch(w -> !w.getIsHandled()
                        && "TASK_DEADLINE".equals(w.getWarningType()));
                if (!hasUnHandled) {
                    createTaskDeadlineWarning(task, daysRemaining);
                }
            }
        }
    }

    private void createBudgetWarning(CarbonBudget budget, WarningLevel level, BigDecimal usageRate) {
        CarbonWarning warning = new CarbonWarning();
        warning.setWarningCode(generateWarningCode());
        warning.setBudgetId(budget.getId());
        warning.setOrgId(budget.getOrgId());
        warning.setOrgName(budget.getOrgName());
        warning.setWarningLevel(level);
        warning.setWarningType("BUDGET_USAGE");
        warning.setWarningTitle(String.format("%s预算使用率预警", budget.getBudgetName()));
        warning.setWarningContent(String.format("当前预算使用率已达 %s%%，已超过%s阈值，请关注",
                usageRate, level == WarningLevel.DANGER ? "危险" : "预警"));
        warning.setUsageRate(usageRate);
        warning.setBudgetAmount(budget.getTotalBudget());
        warning.setUsedAmount(budget.getUsedAmount());
        warning.setExceedAmount(usageRate.compareTo(new BigDecimal("100")) > 0
                ? budget.getUsedAmount().subtract(budget.getTotalBudget())
                : BigDecimal.ZERO);
        warning.setIsHandled(false);
        warningRepository.save(warning);

        todoService.createTodo(
                "admin",
                "BUDGET_WARNING",
                warning.getWarningTitle(),
                warning.getWarningContent(),
                "BUDGET",
                budget.getId(),
                LocalDateTime.now().plusDays(1)
        );
    }

    private void createTaskDelayedWarning(EmissionTask task) {
        CarbonWarning warning = new CarbonWarning();
        warning.setWarningCode(generateWarningCode());
        warning.setTaskId(task.getId());
        warning.setOrgId(task.getOrgId());
        warning.setOrgName(task.getOrgName());
        warning.setDeptId(task.getDeptId());
        warning.setDeptName(task.getDeptName());
        warning.setWarningLevel(WarningLevel.WARNING);
        warning.setWarningType("TASK_DELAYED");
        warning.setWarningTitle(String.format("任务延期预警：%s", task.getTaskName()));
        warning.setWarningContent(String.format("任务已超过截止日期 %s，当前完成率 %s%%，请及时处理",
                task.getEndDate(), task.getCompletionRate()));
        warning.setIsHandled(false);
        warningRepository.save(warning);

        todoService.createTodo(
                task.getResponsibilityPerson(),
                "BUDGET_WARNING",
                warning.getWarningTitle(),
                warning.getWarningContent(),
                "TASK",
                task.getId(),
                LocalDateTime.now().plusDays(1)
        );
    }

    private void createTaskDeadlineWarning(EmissionTask task, long daysRemaining) {
        CarbonWarning warning = new CarbonWarning();
        warning.setWarningCode(generateWarningCode());
        warning.setTaskId(task.getId());
        warning.setOrgId(task.getOrgId());
        warning.setOrgName(task.getOrgName());
        warning.setDeptId(task.getDeptId());
        warning.setDeptName(task.getDeptName());
        warning.setWarningLevel(WarningLevel.ATTENTION);
        warning.setWarningType("TASK_DEADLINE");
        warning.setWarningTitle(String.format("任务即将截止：%s", task.getTaskName()));
        warning.setWarningContent(String.format("任务还有 %d 天截止，当前完成率 %s%%，请加快进度",
                daysRemaining, task.getCompletionRate()));
        warning.setIsHandled(false);
        warningRepository.save(warning);

        todoService.createTodo(
                task.getResponsibilityPerson(),
                "BUDGET_WARNING",
                warning.getWarningTitle(),
                warning.getWarningContent(),
                "TASK",
                task.getId(),
                LocalDateTime.now().plusHours(12)
        );
    }

    @Override
    public PageResult<WarningVO> queryWarnings(WarningQueryDTO dto) {
        Specification<CarbonWarning> spec = buildWarningSpecification(dto);
        PageRequest pageRequest = PageRequest.of(
                dto.getPageNum() - 1,
                dto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdTime")
        );
        Page<CarbonWarning> page = warningRepository.findAll(spec, pageRequest);
        List<WarningVO> records = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(records, page.getTotalElements(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    public List<WarningVO> getWarningsByBudgetId(Long budgetId) {
        return warningRepository.findByBudgetId(budgetId).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarningVO> getWarningsByTaskId(Long taskId) {
        return warningRepository.findByTaskId(taskId).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarningVO> getWarningsByOrgId(String orgId) {
        return warningRepository.findByOrgId(orgId).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarningVO> getUnhandledWarningsByOrgId(String orgId) {
        return warningRepository.findByOrgIdAndIsHandled(orgId, false).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarningVO> getUnhandledWarningsByDeptId(String deptId) {
        return warningRepository.findByDeptIdAndIsHandled(deptId, false).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void handleWarning(Long id, String handleRemark) {
        CarbonWarning warning = warningRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预警记录不存在"));
        warning.setIsHandled(true);
        warning.setHandleRemark(handleRemark);
        warning.setHandleTime(LocalDateTime.now());
        warningRepository.save(warning);
    }

    private Specification<CarbonWarning> buildWarningSpecification(WarningQueryDTO dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dto.getBudgetId() != null) {
                predicates.add(cb.equal(root.get("budgetId"), dto.getBudgetId()));
            }
            if (dto.getTaskId() != null) {
                predicates.add(cb.equal(root.get("taskId"), dto.getTaskId()));
            }
            if (dto.getOrgId() != null && !dto.getOrgId().isEmpty()) {
                predicates.add(cb.equal(root.get("orgId"), dto.getOrgId()));
            }
            if (dto.getDeptId() != null && !dto.getDeptId().isEmpty()) {
                predicates.add(cb.equal(root.get("deptId"), dto.getDeptId()));
            }
            if (dto.getWarningLevel() != null) {
                predicates.add(cb.equal(root.get("warningLevel"), dto.getWarningLevel()));
            }
            if (dto.getWarningType() != null && !dto.getWarningType().isEmpty()) {
                predicates.add(cb.equal(root.get("warningType"), dto.getWarningType()));
            }
            if (dto.getIsHandled() != null) {
                predicates.add(cb.equal(root.get("isHandled"), dto.getIsHandled()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private WarningVO convertToVO(CarbonWarning warning) {
        WarningVO vo = new WarningVO();
        BeanUtils.copyProperties(warning, vo);
        vo.setWarningLevelDesc(warning.getWarningLevel().getDesc());
        return vo;
    }

    private String generateWarningCode() {
        return "WRN-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
    }
}
