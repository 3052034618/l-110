package com.carbon.management.controller;

import com.carbon.management.common.PageResult;
import com.carbon.management.common.Result;
import com.carbon.management.dto.AttachmentDTO;
import com.carbon.management.dto.ProgressReportDTO;
import com.carbon.management.dto.TaskCreateDTO;
import com.carbon.management.dto.TaskQueryDTO;
import com.carbon.management.dto.TaskUpdateDTO;
import com.carbon.management.service.TaskService;
import com.carbon.management.vo.AttachmentVO;
import com.carbon.management.vo.TaskProgressVO;
import com.carbon.management.vo.TaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "任务管理", description = "减排任务的创建、查询、进度上报、附件管理等接口")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "创建减排任务")
    @PostMapping
    public Result<Long> createTask(@Valid @RequestBody TaskCreateDTO dto) {
        return Result.success(taskService.createTask(dto));
    }

    @Operation(summary = "更新减排任务")
    @PutMapping("/{id}")
    public Result<Void> updateTask(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestBody TaskUpdateDTO dto) {
        taskService.updateTask(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除减排任务")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        taskService.deleteTask(id);
        return Result.success();
    }

    @Operation(summary = "根据ID获取任务详情")
    @GetMapping("/{id}")
    public Result<TaskVO> getTaskById(@Parameter(description = "任务ID") @PathVariable Long id) {
        return Result.success(taskService.getTaskById(id));
    }

    @Operation(summary = "根据编码获取任务详情")
    @GetMapping("/code/{code}")
    public Result<TaskVO> getTaskByCode(@Parameter(description = "任务编码") @PathVariable String code) {
        return Result.success(taskService.getTaskByCode(code));
    }

    @Operation(summary = "分页查询任务列表")
    @PostMapping("/query")
    public Result<PageResult<TaskVO>> queryTasks(@RequestBody TaskQueryDTO dto) {
        return Result.success(taskService.queryTasks(dto));
    }

    @Operation(summary = "获取预算关联的任务列表")
    @GetMapping("/budget/{budgetId}")
    public Result<List<TaskVO>> getTasksByBudgetId(@Parameter(description = "预算ID") @PathVariable Long budgetId) {
        return Result.success(taskService.getTasksByBudgetId(budgetId));
    }

    @Operation(summary = "获取部门任务列表")
    @GetMapping("/dept/{deptId}")
    public Result<List<TaskVO>> getTasksByDeptId(@Parameter(description = "部门ID") @PathVariable String deptId) {
        return Result.success(taskService.getTasksByDeptId(deptId));
    }

    @Operation(summary = "获取负责人的任务列表")
    @GetMapping("/responsibility/{person}")
    public Result<List<TaskVO>> getTasksByResponsibilityPerson(
            @Parameter(description = "负责人") @PathVariable String person) {
        return Result.success(taskService.getTasksByResponsibilityPerson(person));
    }

    @Operation(summary = "上报任务进度")
    @PostMapping("/progress")
    public Result<Long> reportProgress(@Valid @RequestBody ProgressReportDTO dto) {
        return Result.success(taskService.reportProgress(dto));
    }

    @Operation(summary = "获取任务进度历史")
    @GetMapping("/{taskId}/progress")
    public Result<List<TaskProgressVO>> getProgressByTaskId(
            @Parameter(description = "任务ID") @PathVariable Long taskId) {
        return Result.success(taskService.getProgressByTaskId(taskId));
    }

    @Operation(summary = "添加任务附件")
    @PostMapping("/attachment")
    public Result<Long> addAttachment(@Valid @RequestBody AttachmentDTO dto) {
        return Result.success(taskService.addAttachment(dto));
    }

    @Operation(summary = "删除任务附件")
    @DeleteMapping("/attachment/{attachmentId}")
    public Result<Void> removeAttachment(@Parameter(description = "附件ID") @PathVariable Long attachmentId) {
        taskService.removeAttachment(attachmentId);
        return Result.success();
    }

    @Operation(summary = "获取任务附件列表")
    @GetMapping("/{taskId}/attachments")
    public Result<List<AttachmentVO>> getAttachmentsByTaskId(
            @Parameter(description = "任务ID") @PathVariable Long taskId) {
        return Result.success(taskService.getAttachmentsByTaskId(taskId));
    }

    @Operation(summary = "获取进度上报附件列表")
    @GetMapping("/progress/{progressId}/attachments")
    public Result<List<AttachmentVO>> getAttachmentsByProgressId(
            @Parameter(description = "进度ID") @PathVariable Long progressId) {
        return Result.success(taskService.getAttachmentsByProgressId(progressId));
    }
}
