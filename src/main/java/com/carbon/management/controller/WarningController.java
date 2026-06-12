package com.carbon.management.controller;

import com.carbon.management.common.PageResult;
import com.carbon.management.common.Result;
import com.carbon.management.dto.WarningQueryDTO;
import com.carbon.management.service.WarningService;
import com.carbon.management.vo.WarningVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "预警管理", description = "预算超支预警、任务延期预警的查询与处理接口")
@RestController
@RequestMapping("/warnings")
@RequiredArgsConstructor
public class WarningController {

    private final WarningService warningService;

    @Operation(summary = "分页查询预警列表")
    @PostMapping("/query")
    public Result<PageResult<WarningVO>> queryWarnings(@RequestBody WarningQueryDTO dto) {
        return Result.success(warningService.queryWarnings(dto));
    }

    @Operation(summary = "获取预算关联的预警列表")
    @GetMapping("/budget/{budgetId}")
    public Result<List<WarningVO>> getWarningsByBudgetId(
            @Parameter(description = "预算ID") @PathVariable Long budgetId) {
        return Result.success(warningService.getWarningsByBudgetId(budgetId));
    }

    @Operation(summary = "获取任务关联的预警列表")
    @GetMapping("/task/{taskId}")
    public Result<List<WarningVO>> getWarningsByTaskId(
            @Parameter(description = "任务ID") @PathVariable Long taskId) {
        return Result.success(warningService.getWarningsByTaskId(taskId));
    }

    @Operation(summary = "获取组织预警列表")
    @GetMapping("/org/{orgId}")
    public Result<List<WarningVO>> getWarningsByOrgId(
            @Parameter(description = "组织ID") @PathVariable String orgId) {
        return Result.success(warningService.getWarningsByOrgId(orgId));
    }

    @Operation(summary = "获取组织未处理预警列表")
    @GetMapping("/org/{orgId}/unhandled")
    public Result<List<WarningVO>> getUnhandledWarningsByOrgId(
            @Parameter(description = "组织ID") @PathVariable String orgId) {
        return Result.success(warningService.getUnhandledWarningsByOrgId(orgId));
    }

    @Operation(summary = "获取部门未处理预警列表")
    @GetMapping("/dept/{deptId}/unhandled")
    public Result<List<WarningVO>> getUnhandledWarningsByDeptId(
            @Parameter(description = "部门ID") @PathVariable String deptId) {
        return Result.success(warningService.getUnhandledWarningsByDeptId(deptId));
    }

    @Operation(summary = "处理预警")
    @PostMapping("/{id}/handle")
    public Result<Void> handleWarning(
            @Parameter(description = "预警ID") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        warningService.handleWarning(id, body.get("handleRemark"));
        return Result.success();
    }
}
