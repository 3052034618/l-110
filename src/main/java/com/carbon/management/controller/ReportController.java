package com.carbon.management.controller;

import com.carbon.management.common.Result;
import com.carbon.management.service.ReportService;
import com.carbon.management.vo.BudgetProgressVO;
import com.carbon.management.vo.YearlySummaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "报表管理", description = "年度汇总、预算进度、数据导出等报表接口")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "获取年度汇总报表")
    @GetMapping("/yearly")
    public Result<YearlySummaryVO> getYearlySummary(
            @Parameter(description = "组织ID") @RequestParam String orgId,
            @Parameter(description = "年度") @RequestParam Integer year) {
        return Result.success(reportService.getYearlySummary(orgId, year));
    }

    @Operation(summary = "获取预算进度报表")
    @GetMapping("/budget-progress")
    public Result<List<BudgetProgressVO>> getBudgetProgressReport(
            @Parameter(description = "组织ID") @RequestParam String orgId,
            @Parameter(description = "年度") @RequestParam Integer year) {
        return Result.success(reportService.getBudgetProgressReport(orgId, year));
    }

    @Operation(summary = "导出年度报告")
    @GetMapping("/yearly/export")
    public ResponseEntity<byte[]> exportYearlyReport(
            @Parameter(description = "组织ID") @RequestParam String orgId,
            @Parameter(description = "年度") @RequestParam Integer year) {
        byte[] data = reportService.exportYearlyReport(orgId, year);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", String.format("carbon-report-%s-%d.txt", orgId, year));
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }
}
