package com.carbon.management.controller;

import com.carbon.management.common.PageResult;
import com.carbon.management.common.Result;
import com.carbon.management.dto.ApprovalDTO;
import com.carbon.management.dto.TodoQueryDTO;
import com.carbon.management.service.ApprovalService;
import com.carbon.management.service.TodoService;
import com.carbon.management.vo.ApprovalRecordVO;
import com.carbon.management.vo.TodoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "待办与审批", description = "待办提醒、审批处理、审批记录查询接口")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TodoApprovalController {

    private final TodoService todoService;
    private final ApprovalService approvalService;

    @Operation(summary = "分页查询待办列表")
    @PostMapping("/todos/query")
    public Result<PageResult<TodoVO>> queryTodos(@RequestBody TodoQueryDTO dto) {
        return Result.success(todoService.queryTodos(dto));
    }

    @Operation(summary = "标记待办为已读")
    @PostMapping("/todos/{id}/read")
    public Result<Void> markAsRead(@Parameter(description = "待办ID") @PathVariable Long id) {
        todoService.markAsRead(id);
        return Result.success();
    }

    @Operation(summary = "标记待办为已完成")
    @PostMapping("/todos/{id}/complete")
    public Result<Void> markAsCompleted(@Parameter(description = "待办ID") @PathVariable Long id) {
        todoService.markAsCompleted(id);
        return Result.success();
    }

    @Operation(summary = "全部标记为已读")
    @PostMapping("/todos/read-all")
    public Result<Void> markAllAsRead(@RequestBody Map<String, String> body) {
        todoService.markAllAsRead(body.get("assignee"));
        return Result.success();
    }

    @Operation(summary = "获取待办统计")
    @GetMapping("/todos/statistics")
    public Result<Map<String, Long>> getTodoStatistics(
            @Parameter(description = "处理人") @RequestParam String assignee) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("unreadCount", todoService.getUnreadCount(assignee));
        stats.put("uncompletedCount", todoService.getUncompletedCount(assignee));
        return Result.success(stats);
    }

    @Operation(summary = "创建审批记录")
    @PostMapping("/approvals")
    public Result<Long> createApprovalRecord(@Valid @RequestBody ApprovalDTO dto) {
        return Result.success(approvalService.createApprovalRecord(dto));
    }

    @Operation(summary = "处理审批")
    @PostMapping("/approvals/{id}/process")
    public Result<Void> processApproval(
            @Parameter(description = "审批ID") @PathVariable Long id,
            @Valid @RequestBody ApprovalDTO dto) {
        approvalService.processApproval(id, dto);
        return Result.success();
    }

    @Operation(summary = "获取业务审批记录")
    @GetMapping("/approvals")
    public Result<List<ApprovalRecordVO>> getApprovalRecords(
            @Parameter(description = "业务类型") @RequestParam String businessType,
            @Parameter(description = "业务ID") @RequestParam Long businessId) {
        return Result.success(approvalService.getApprovalRecords(businessType, businessId));
    }

    @Operation(summary = "根据业务编号获取审批记录")
    @GetMapping("/approvals/business-no/{businessNo}")
    public Result<List<ApprovalRecordVO>> getApprovalRecordsByBusinessNo(
            @Parameter(description = "业务编号") @PathVariable String businessNo) {
        return Result.success(approvalService.getApprovalRecordsByBusinessNo(businessNo));
    }

    @Operation(summary = "获取我的待审批列表")
    @GetMapping("/approvals/pending")
    public Result<List<ApprovalRecordVO>> getPendingApprovals(
            @Parameter(description = "审批人") @RequestParam String approver) {
        return Result.success(approvalService.getPendingApprovals(approver));
    }
}
