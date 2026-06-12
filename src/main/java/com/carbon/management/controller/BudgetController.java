package com.carbon.management.controller;

import com.carbon.management.common.PageResult;
import com.carbon.management.common.Result;
import com.carbon.management.dto.BudgetAdjustmentDTO;
import com.carbon.management.dto.BudgetCreateDTO;
import com.carbon.management.dto.BudgetDecompositionDTO;
import com.carbon.management.dto.BudgetQueryDTO;
import com.carbon.management.dto.BudgetUpdateDTO;
import com.carbon.management.service.BudgetService;
import com.carbon.management.vo.BudgetAdjustmentVO;
import com.carbon.management.vo.BudgetDecompositionVO;
import com.carbon.management.vo.BudgetProgressVO;
import com.carbon.management.vo.BudgetVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "预算管理", description = "碳预算的创建、查询、分解、调整等接口")
@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "创建预算")
    @PostMapping
    public Result<Long> createBudget(@Valid @RequestBody BudgetCreateDTO dto) {
        return Result.success(budgetService.createBudget(dto));
    }

    @Operation(summary = "更新预算")
    @PutMapping("/{id}")
    public Result<Void> updateBudget(
            @Parameter(description = "预算ID") @PathVariable Long id,
            @RequestBody BudgetUpdateDTO dto) {
        budgetService.updateBudget(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除预算")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBudget(@Parameter(description = "预算ID") @PathVariable Long id) {
        budgetService.deleteBudget(id);
        return Result.success();
    }

    @Operation(summary = "根据ID获取预算详情")
    @GetMapping("/{id}")
    public Result<BudgetVO> getBudgetById(@Parameter(description = "预算ID") @PathVariable Long id) {
        return Result.success(budgetService.getBudgetById(id));
    }

    @Operation(summary = "根据编码获取预算详情")
    @GetMapping("/code/{code}")
    public Result<BudgetVO> getBudgetByCode(@Parameter(description = "预算编码") @PathVariable String code) {
        return Result.success(budgetService.getBudgetByCode(code));
    }

    @Operation(summary = "分页查询预算列表")
    @PostMapping("/query")
    public Result<PageResult<BudgetVO>> queryBudgets(@RequestBody BudgetQueryDTO dto) {
        return Result.success(budgetService.queryBudgets(dto));
    }

    @Operation(summary = "获取组织年度预算列表")
    @GetMapping("/org/{orgId}/year/{year}")
    public Result<List<BudgetVO>> getBudgetsByOrgAndYear(
            @Parameter(description = "组织ID") @PathVariable String orgId,
            @Parameter(description = "年度") @PathVariable Integer year) {
        return Result.success(budgetService.getBudgetsByOrgAndYear(orgId, year));
    }

    @Operation(summary = "获取组织月度预算列表")
    @GetMapping("/org/{orgId}/year/{year}/month/{month}")
    public Result<List<BudgetVO>> getBudgetsByOrgAndYearAndMonth(
            @Parameter(description = "组织ID") @PathVariable String orgId,
            @Parameter(description = "年度") @PathVariable Integer year,
            @Parameter(description = "月份") @PathVariable Integer month) {
        return Result.success(budgetService.getBudgetsByOrgAndYearAndMonth(orgId, year, month));
    }

    @Operation(summary = "提交预算审批")
    @PostMapping("/{id}/submit")
    public Result<Void> submitForApproval(@Parameter(description = "预算ID") @PathVariable Long id) {
        budgetService.submitForApproval(id);
        return Result.success();
    }

    @Operation(summary = "创建预算分解")
    @PostMapping("/decomposition")
    public Result<Long> createDecomposition(@Valid @RequestBody BudgetDecompositionDTO dto) {
        return Result.success(budgetService.createDecomposition(dto));
    }

    @Operation(summary = "更新预算分解")
    @PutMapping("/decomposition/{id}")
    public Result<Void> updateDecomposition(
            @Parameter(description = "分解ID") @PathVariable Long id,
            @RequestBody BudgetDecompositionDTO dto) {
        budgetService.updateDecomposition(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除预算分解")
    @DeleteMapping("/decomposition/{id}")
    public Result<Void> deleteDecomposition(@Parameter(description = "分解ID") @PathVariable Long id) {
        budgetService.deleteDecomposition(id);
        return Result.success();
    }

    @Operation(summary = "获取预算分解列表")
    @GetMapping("/{budgetId}/decomposition")
    public Result<List<BudgetDecompositionVO>> getDecompositionsByBudgetId(
            @Parameter(description = "预算ID") @PathVariable Long budgetId) {
        return Result.success(budgetService.getDecompositionsByBudgetId(budgetId));
    }

    @Operation(summary = "获取部门预算分解列表")
    @GetMapping("/decomposition/dept/{deptId}")
    public Result<List<BudgetDecompositionVO>> getDecompositionsByDeptId(
            @Parameter(description = "部门ID") @PathVariable String deptId) {
        return Result.success(budgetService.getDecompositionsByDeptId(deptId));
    }

    @Operation(summary = "创建预算调整申请")
    @PostMapping("/adjustment")
    public Result<Long> createAdjustment(@Valid @RequestBody BudgetAdjustmentDTO dto) {
        return Result.success(budgetService.createAdjustment(dto));
    }

    @Operation(summary = "获取预算调整历史")
    @GetMapping("/{budgetId}/adjustments")
    public Result<List<BudgetAdjustmentVO>> getAdjustmentsByBudgetId(
            @Parameter(description = "预算ID") @PathVariable Long budgetId) {
        return Result.success(budgetService.getAdjustmentsByBudgetId(budgetId));
    }

    @Operation(summary = "获取组织年度预算执行进度列表")
    @GetMapping("/progress")
    public Result<List<BudgetProgressVO>> getBudgetProgressList(
            @Parameter(description = "组织ID") @RequestParam String orgId,
            @Parameter(description = "年度") @RequestParam Integer year) {
        return Result.success(budgetService.getBudgetProgressList(orgId, year));
    }

    @Operation(summary = "获取单个预算执行进度")
    @GetMapping("/{id}/progress")
    public Result<BudgetProgressVO> getBudgetProgress(@Parameter(description = "预算ID") @PathVariable Long id) {
        return Result.success(budgetService.getBudgetProgress(id));
    }
}
