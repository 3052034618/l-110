package com.carbon.management.service.impl;

import com.carbon.management.common.PageResult;
import com.carbon.management.common.enums.EmissionScope;
import com.carbon.management.common.enums.TaskStatus;
import com.carbon.management.common.exception.BusinessException;
import com.carbon.management.dto.AttachmentDTO;
import com.carbon.management.dto.ProgressReportDTO;
import com.carbon.management.dto.TaskCreateDTO;
import com.carbon.management.dto.TaskQueryDTO;
import com.carbon.management.dto.TaskUpdateDTO;
import com.carbon.management.entity.BudgetDecomposition;
import com.carbon.management.entity.CarbonBudget;
import com.carbon.management.entity.EmissionTask;
import com.carbon.management.entity.TaskAttachment;
import com.carbon.management.entity.TaskProgress;
import com.carbon.management.repository.BudgetDecompositionRepository;
import com.carbon.management.repository.CarbonBudgetRepository;
import com.carbon.management.repository.EmissionTaskRepository;
import com.carbon.management.repository.TaskAttachmentRepository;
import com.carbon.management.repository.TaskProgressRepository;
import com.carbon.management.service.TaskService;
import com.carbon.management.service.TodoService;
import com.carbon.management.service.WarningService;
import com.carbon.management.vo.AttachmentVO;
import com.carbon.management.vo.TaskProgressVO;
import com.carbon.management.vo.TaskVO;
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
public class TaskServiceImpl implements TaskService {

    private final EmissionTaskRepository taskRepository;
    private final TaskProgressRepository progressRepository;
    private final TaskAttachmentRepository attachmentRepository;
    private final CarbonBudgetRepository budgetRepository;
    private final BudgetDecompositionRepository decompositionRepository;
    private final TodoService todoService;
    private final WarningService warningService;

    @Override
    @Transactional
    public Long createTask(TaskCreateDTO dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
        EmissionTask task = new EmissionTask();
        BeanUtils.copyProperties(dto, task);
        task.setTaskCode(generateTaskCode());
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setActualReduction(BigDecimal.ZERO);
        task.setCompletionRate(BigDecimal.ZERO);
        EmissionTask saved = taskRepository.save(task);

        todoService.createTodo(
                dto.getResponsibilityPerson(),
                "TASK_DEADLINE",
                "新任务分配：" + dto.getTaskName(),
                "您有新的减排任务，请及时处理",
                "TASK",
                saved.getId(),
                dto.getEndDate().atStartOfDay()
        );

        return saved.getId();
    }

    @Override
    @Transactional
    public void updateTask(Long id, TaskUpdateDTO dto) {
        EmissionTask task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
        if (dto.getTaskName() != null) {
            task.setTaskName(dto.getTaskName());
        }
        if (dto.getCategory() != null) {
            task.setCategory(dto.getCategory());
        }
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }
        if (dto.getEstimatedReduction() != null) {
            task.setEstimatedReduction(dto.getEstimatedReduction());
            updateTaskCompletionRate(task);
        }
        if (dto.getStartDate() != null) {
            task.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            if (task.getStartDate() != null && dto.getEndDate().isBefore(task.getStartDate())) {
                throw new BusinessException("结束日期不能早于开始日期");
            }
            task.setEndDate(dto.getEndDate());
        }
        if (dto.getResponsibilityPerson() != null) {
            task.setResponsibilityPerson(dto.getResponsibilityPerson());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getMeasureDetail() != null) {
            task.setMeasureDetail(dto.getMeasureDetail());
        }
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        EmissionTask task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
        if (task.getStatus() == TaskStatus.IN_PROGRESS || task.getStatus() == TaskStatus.COMPLETED) {
            throw new BusinessException("进行中或已完成的任务不能删除");
        }
        attachmentRepository.deleteByTaskId(id);
        taskRepository.delete(task);
    }

    @Override
    public TaskVO getTaskById(Long id) {
        EmissionTask task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
        return convertToTaskVO(task);
    }

    @Override
    public TaskVO getTaskByCode(String taskCode) {
        EmissionTask task = taskRepository.findByTaskCode(taskCode)
                .orElseThrow(() -> new BusinessException("任务不存在"));
        return convertToTaskVO(task);
    }

    @Override
    public PageResult<TaskVO> queryTasks(TaskQueryDTO dto) {
        Specification<EmissionTask> spec = buildTaskSpecification(dto);
        PageRequest pageRequest = PageRequest.of(
                dto.getPageNum() - 1,
                dto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdTime")
        );
        Page<EmissionTask> page = taskRepository.findAll(spec, pageRequest);
        List<TaskVO> records = page.getContent().stream()
                .map(this::convertToTaskVO)
                .collect(Collectors.toList());
        return PageResult.of(records, page.getTotalElements(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    public List<TaskVO> getTasksByBudgetId(Long budgetId) {
        return taskRepository.findByBudgetId(budgetId).stream()
                .map(this::convertToTaskVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskVO> getTasksByDeptId(String deptId) {
        return taskRepository.findByDeptId(deptId).stream()
                .map(this::convertToTaskVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskVO> getTasksByResponsibilityPerson(String person) {
        return taskRepository.findByResponsibilityPerson(person).stream()
                .map(this::convertToTaskVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long reportProgress(ProgressReportDTO dto) {
        EmissionTask task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new BusinessException("任务不存在"));

        List<TaskProgress> existing = progressRepository.findByTaskIdAndReportPeriod(dto.getTaskId(), dto.getReportPeriod());
        BigDecimal cumulative;
        if (!existing.isEmpty()) {
            throw new BusinessException("该周期已上报过进度");
        }

        List<TaskProgress> allProgress = progressRepository.findByTaskIdOrderByCreatedTimeDesc(dto.getTaskId());
        BigDecimal previousCumulative = allProgress.isEmpty() ? BigDecimal.ZERO
                : allProgress.get(0).getCumulativeReduction();
        cumulative = previousCumulative.add(dto.getPeriodReduction());

        TaskProgress progress = new TaskProgress();
        BeanUtils.copyProperties(dto, progress);
        progress.setCumulativeReduction(cumulative);

        BigDecimal completionRate = task.getEstimatedReduction().compareTo(BigDecimal.ZERO) > 0
                ? cumulative.multiply(new BigDecimal("100"))
                .divide(task.getEstimatedReduction(), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        progress.setCompletionRate(completionRate);

        TaskProgress saved = progressRepository.save(progress);

        task.setActualReduction(cumulative);
        task.setCompletionRate(completionRate);
        if (task.getStatus() == TaskStatus.NOT_STARTED) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
        if (completionRate.compareTo(new BigDecimal("100")) >= 0) {
            task.setStatus(TaskStatus.COMPLETED);
        }
        LocalDate today = LocalDate.now();
        if (task.getEndDate() != null && today.isAfter(task.getEndDate())
                && task.getStatus() != TaskStatus.COMPLETED) {
            task.setStatus(TaskStatus.DELAYED);
        }
        taskRepository.save(task);

        updateDecompositionAchievedReduction(task);
        updateBudgetUsedAmount(task);
        warningService.checkAndCreateWarning(task);

        return saved.getId();
    }

    private void updateDecompositionAchievedReduction(EmissionTask task) {
        if (task.getDecompositionId() == null) {
            return;
        }
        BudgetDecomposition decomposition = decompositionRepository.findById(task.getDecompositionId()).orElse(null);
        if (decomposition == null) {
            return;
        }
        List<EmissionTask> relatedTasks = taskRepository.findByDecompositionId(task.getDecompositionId());
        BigDecimal totalAchieved = relatedTasks.stream()
                .map(EmissionTask::getActualReduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        decomposition.setAchievedReduction(totalAchieved);
        if (decomposition.getTargetReduction().compareTo(BigDecimal.ZERO) > 0) {
            decomposition.setCompletionRate(
                    totalAchieved.multiply(new BigDecimal("100"))
                            .divide(decomposition.getTargetReduction(), 2, RoundingMode.HALF_UP)
            );
        }
        decompositionRepository.save(decomposition);
    }

    private void updateBudgetUsedAmount(EmissionTask task) {
        if (task.getBudgetId() == null) {
            return;
        }
        CarbonBudget budget = budgetRepository.findById(task.getBudgetId()).orElse(null);
        if (budget == null) {
            return;
        }
        List<EmissionTask> relatedTasks = taskRepository.findByBudgetId(task.getBudgetId());
        BigDecimal totalUsed = relatedTasks.stream()
                .map(EmissionTask::getActualReduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        budget.setUsedAmount(totalUsed);
        budget.setRemainingAmount(budget.getTotalBudget().subtract(totalUsed));
        budgetRepository.save(budget);
    }

    @Override
    public List<TaskProgressVO> getProgressByTaskId(Long taskId) {
        return progressRepository.findByTaskIdOrderByCreatedTimeDesc(taskId).stream()
                .map(this::convertToProgressVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long addAttachment(AttachmentDTO dto) {
        TaskAttachment attachment = new TaskAttachment();
        BeanUtils.copyProperties(dto, attachment);
        TaskAttachment saved = attachmentRepository.save(attachment);
        return saved.getId();
    }

    @Override
    @Transactional
    public void removeAttachment(Long attachmentId) {
        if (!attachmentRepository.existsById(attachmentId)) {
            throw new BusinessException("附件不存在");
        }
        attachmentRepository.deleteById(attachmentId);
    }

    @Override
    public List<AttachmentVO> getAttachmentsByTaskId(Long taskId) {
        return attachmentRepository.findByTaskId(taskId).stream()
                .map(this::convertToAttachmentVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentVO> getAttachmentsByProgressId(Long progressId) {
        return attachmentRepository.findByProgressId(progressId).stream()
                .map(this::convertToAttachmentVO)
                .collect(Collectors.toList());
    }

    private Specification<EmissionTask> buildTaskSpecification(TaskQueryDTO dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dto.getTaskCode() != null && !dto.getTaskCode().isEmpty()) {
                predicates.add(cb.like(root.get("taskCode"), "%" + dto.getTaskCode() + "%"));
            }
            if (dto.getTaskName() != null && !dto.getTaskName().isEmpty()) {
                predicates.add(cb.like(root.get("taskName"), "%" + dto.getTaskName() + "%"));
            }
            if (dto.getBudgetId() != null) {
                predicates.add(cb.equal(root.get("budgetId"), dto.getBudgetId()));
            }
            if (dto.getOrgId() != null && !dto.getOrgId().isEmpty()) {
                predicates.add(cb.equal(root.get("orgId"), dto.getOrgId()));
            }
            if (dto.getDeptId() != null && !dto.getDeptId().isEmpty()) {
                predicates.add(cb.equal(root.get("deptId"), dto.getDeptId()));
            }
            if (dto.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), dto.getCategory()));
            }
            if (dto.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), dto.getStatus()));
            }
            if (dto.getEmissionScope() != null) {
                predicates.add(cb.equal(root.get("emissionScope"), dto.getEmissionScope()));
            }
            if (dto.getResponsibilityPerson() != null && !dto.getResponsibilityPerson().isEmpty()) {
                predicates.add(cb.equal(root.get("responsibilityPerson"), dto.getResponsibilityPerson()));
            }
            if (dto.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), dto.getStartDateFrom()));
            }
            if (dto.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), dto.getStartDateTo()));
            }
            if (dto.getEndDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), dto.getEndDateFrom()));
            }
            if (dto.getEndDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), dto.getEndDateTo()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private TaskVO convertToTaskVO(EmissionTask task) {
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);
        vo.setCategoryDesc(task.getCategory().getDesc());
        vo.setStatusDesc(task.getStatus().getDesc());
        if (task.getEmissionScope() != null) {
            vo.setEmissionScopeDesc(task.getEmissionScope().getDesc());
        }
        vo.setProgressList(getProgressByTaskId(task.getId()));
        vo.setAttachmentList(getAttachmentsByTaskId(task.getId()));
        return vo;
    }

    private TaskProgressVO convertToProgressVO(TaskProgress progress) {
        TaskProgressVO vo = new TaskProgressVO();
        BeanUtils.copyProperties(progress, vo);
        return vo;
    }

    private AttachmentVO convertToAttachmentVO(TaskAttachment attachment) {
        AttachmentVO vo = new AttachmentVO();
        BeanUtils.copyProperties(attachment, vo);
        return vo;
    }

    private void updateTaskCompletionRate(EmissionTask task) {
        if (task.getEstimatedReduction().compareTo(BigDecimal.ZERO) > 0) {
            task.setCompletionRate(
                    task.getActualReduction().multiply(new BigDecimal("100"))
                            .divide(task.getEstimatedReduction(), 2, RoundingMode.HALF_UP)
            );
        }
    }

    private String generateTaskCode() {
        return "TSK-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
    }
}
