package com.carbon.management.service.impl;

import com.carbon.management.common.enums.EmissionScope;
import com.carbon.management.common.enums.TaskStatus;
import com.carbon.management.entity.BudgetDecomposition;
import com.carbon.management.entity.CarbonBudget;
import com.carbon.management.entity.CarbonWarning;
import com.carbon.management.entity.EmissionTask;
import com.carbon.management.repository.BudgetDecompositionRepository;
import com.carbon.management.repository.CarbonBudgetRepository;
import com.carbon.management.repository.CarbonWarningRepository;
import com.carbon.management.repository.EmissionTaskRepository;
import com.carbon.management.service.BudgetService;
import com.carbon.management.service.ReportService;
import com.carbon.management.vo.BudgetProgressVO;
import com.carbon.management.vo.DeptSummaryVO;
import com.carbon.management.vo.MonthlySummaryVO;
import com.carbon.management.vo.ScopeSummaryVO;
import com.carbon.management.vo.YearlySummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final CarbonBudgetRepository budgetRepository;
    private final BudgetDecompositionRepository decompositionRepository;
    private final EmissionTaskRepository taskRepository;
    private final CarbonWarningRepository warningRepository;
    private final BudgetService budgetService;

    @Override
    public YearlySummaryVO getYearlySummary(String orgId, Integer year) {
        YearlySummaryVO vo = new YearlySummaryVO();
        vo.setYear(year);
        vo.setOrgId(orgId);

        List<CarbonBudget> budgets = budgetRepository.findByOrgIdAndBudgetYear(orgId, year);
        if (!budgets.isEmpty()) {
            vo.setOrgName(budgets.get(0).getOrgName());
        }

        BigDecimal totalBudget = budgets.stream()
                .map(CarbonBudget::getTotalBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalUsed = budgets.stream()
                .map(CarbonBudget::getUsedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalBudget(totalBudget);
        vo.setTotalUsed(totalUsed);
        vo.setTotalRemaining(totalBudget.subtract(totalUsed));

        List<Long> budgetIds = budgets.stream().map(CarbonBudget::getId).collect(Collectors.toList());
        List<BudgetDecomposition> decompositions = budgetIds.isEmpty() ? new ArrayList<>()
                : decompositionRepository.findByBudgetIdIn(budgetIds);
        BigDecimal totalTarget = decompositions.stream()
                .map(BudgetDecomposition::getTargetReduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAchieved = decompositions.stream()
                .map(BudgetDecomposition::getAchievedReduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalTargetReduction(totalTarget);
        vo.setTotalAchievedReduction(totalAchieved);
        vo.setReductionCompletionRate(totalTarget.compareTo(BigDecimal.ZERO) > 0
                ? totalAchieved.multiply(new BigDecimal("100"))
                .divide(totalTarget, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        List<EmissionTask> tasks = budgetIds.isEmpty() ? new ArrayList<>()
                : taskRepository.findByBudgetIdIn(budgetIds);
        vo.setTotalTaskCount((long) tasks.size());
        long completedCount = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();
        vo.setCompletedTaskCount(completedCount);
        vo.setTaskCompletionRate(tasks.isEmpty() ? BigDecimal.ZERO
                : new BigDecimal(completedCount).multiply(new BigDecimal("100"))
                .divide(new BigDecimal(tasks.size()), 2, RoundingMode.HALF_UP));

        Map<EmissionScope, ScopeSummaryVO> scopeSummary = new LinkedHashMap<>();
        for (EmissionScope scope : EmissionScope.values()) {
            List<CarbonBudget> scopeBudgets = budgets.stream()
                    .filter(b -> b.getEmissionScope() == scope)
                    .collect(Collectors.toList());
            if (!scopeBudgets.isEmpty()) {
                ScopeSummaryVO sVO = new ScopeSummaryVO();
                sVO.setEmissionScope(scope);
                sVO.setEmissionScopeDesc(scope.getDesc());
                BigDecimal sBudget = scopeBudgets.stream()
                        .map(CarbonBudget::getTotalBudget)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sUsed = scopeBudgets.stream()
                        .map(CarbonBudget::getUsedAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                sVO.setBudget(sBudget);
                sVO.setUsed(sUsed);
                sVO.setRemaining(sBudget.subtract(sUsed));
                sVO.setUsageRate(sBudget.compareTo(BigDecimal.ZERO) > 0
                        ? sUsed.multiply(new BigDecimal("100"))
                        .divide(sBudget, 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);
                scopeSummary.put(scope, sVO);
            }
        }
        vo.setScopeSummary(scopeSummary);

        Map<String, DeptSummaryVO> deptMap = new HashMap<>();
        for (BudgetDecomposition d : decompositions) {
            DeptSummaryVO dVO = deptMap.computeIfAbsent(d.getDeptId(), k -> {
                DeptSummaryVO v = new DeptSummaryVO();
                v.setDeptId(d.getDeptId());
                v.setDeptName(d.getDeptName());
                v.setTargetReduction(BigDecimal.ZERO);
                v.setAchievedReduction(BigDecimal.ZERO);
                v.setTaskCount(0L);
                v.setCompletedTaskCount(0L);
                return v;
            });
            dVO.setTargetReduction(dVO.getTargetReduction().add(d.getTargetReduction()));
            dVO.setAchievedReduction(dVO.getAchievedReduction().add(d.getAchievedReduction()));
        }
        for (EmissionTask t : tasks) {
            DeptSummaryVO dVO = deptMap.get(t.getDeptId());
            if (dVO == null) {
                dVO = new DeptSummaryVO();
                dVO.setDeptId(t.getDeptId());
                dVO.setDeptName(t.getDeptName());
                dVO.setTargetReduction(BigDecimal.ZERO);
                dVO.setAchievedReduction(BigDecimal.ZERO);
                dVO.setTaskCount(0L);
                dVO.setCompletedTaskCount(0L);
                deptMap.put(t.getDeptId(), dVO);
            }
            dVO.setTaskCount(dVO.getTaskCount() + 1);
            if (t.getStatus() == TaskStatus.COMPLETED) {
                dVO.setCompletedTaskCount(dVO.getCompletedTaskCount() + 1);
            }
        }
        for (DeptSummaryVO dVO : deptMap.values()) {
            dVO.setCompletionRate(dVO.getTargetReduction().compareTo(BigDecimal.ZERO) > 0
                    ? dVO.getAchievedReduction().multiply(new BigDecimal("100"))
                    .divide(dVO.getTargetReduction(), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO);
        }
        vo.setDeptSummary(new ArrayList<>(deptMap.values()));

        List<MonthlySummaryVO> monthlyTrend = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            final int m = month;
            MonthlySummaryVO mVO = new MonthlySummaryVO();
            mVO.setMonth(month);
            List<CarbonBudget> monthBudgets = budgets.stream()
                    .filter(b -> b.getBudgetMonth() != null && b.getBudgetMonth() == m)
                    .collect(Collectors.toList());
            BigDecimal mBudget = monthBudgets.stream()
                    .map(CarbonBudget::getTotalBudget)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal mUsed = monthBudgets.stream()
                    .map(CarbonBudget::getUsedAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            mVO.setBudget(mBudget);
            mVO.setUsed(mUsed);
            mVO.setRemaining(mBudget.subtract(mUsed));

            BigDecimal mReduction = tasks.stream()
                    .filter(t -> t.getEndDate() != null && t.getEndDate().getMonthValue() == m)
                    .map(EmissionTask::getActualReduction)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            mVO.setReduction(mReduction);

            final LocalDate monthStart = LocalDate.of(year, month, 1);
            final LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            long newTaskCount = tasks.stream()
                    .filter(t -> {
                        LocalDate start = t.getStartDate();
                        return start != null && !start.isBefore(monthStart) && !start.isAfter(monthEnd);
                    })
                    .count();
            mVO.setNewTaskCount(newTaskCount);

            long completedInMonth = tasks.stream()
                    .filter(t -> t.getStatus() == TaskStatus.COMPLETED
                            && t.getEndDate() != null
                            && !t.getEndDate().isBefore(monthStart)
                            && !t.getEndDate().isAfter(monthEnd))
                    .count();
            mVO.setCompletedTaskCount(completedInMonth);

            monthlyTrend.add(mVO);
        }
        vo.setMonthlyTrend(monthlyTrend);

        List<CarbonWarning> warnings = warningRepository.findByOrgId(orgId);
        vo.setWarningCount((long) warnings.size());
        vo.setHandledWarningCount(warnings.stream().filter(CarbonWarning::getIsHandled).count());

        return vo;
    }

    @Override
    public List<BudgetProgressVO> getBudgetProgressReport(String orgId, Integer year) {
        return budgetService.getBudgetProgressList(orgId, year);
    }

    @Override
    public byte[] exportYearlyReport(String orgId, Integer year) {
        YearlySummaryVO summary = getYearlySummary(orgId, year);
        StringBuilder sb = new StringBuilder();
        sb.append("碳中和年度汇总报告\n");
        sb.append("================\n");
        sb.append("组织: ").append(summary.getOrgName()).append("\n");
        sb.append("年度: ").append(summary.getYear()).append("\n");
        sb.append("总预算: ").append(summary.getTotalBudget()).append(" tCO2e\n");
        sb.append("已使用: ").append(summary.getTotalUsed()).append(" tCO2e\n");
        sb.append("剩余预算: ").append(summary.getTotalRemaining()).append(" tCO2e\n");
        sb.append("减排目标: ").append(summary.getTotalTargetReduction()).append(" tCO2e\n");
        sb.append("已实现减排: ").append(summary.getTotalAchievedReduction()).append(" tCO2e\n");
        sb.append("减排完成率: ").append(summary.getReductionCompletionRate()).append("%\n");
        sb.append("总任务数: ").append(summary.getTotalTaskCount()).append("\n");
        sb.append("已完成任务: ").append(summary.getCompletedTaskCount()).append("\n");
        sb.append("任务完成率: ").append(summary.getTaskCompletionRate()).append("%\n");
        sb.append("预警总数: ").append(summary.getWarningCount()).append("\n");
        sb.append("已处理预警: ").append(summary.getHandledWarningCount()).append("\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
