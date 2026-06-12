package com.carbon.management.controller;

import com.carbon.management.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "健康检查", description = "服务健康状态检查接口")
@RestController
@RequestMapping
public class HealthController {

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", "UP");
        info.put("service", "carbon-management");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return Result.success("服务运行正常", info);
    }

    @Operation(summary = "服务信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "碳中和管理后端服务");
        info.put("description", "面向企业内部系统的碳预算与减排任务统一能力平台");
        info.put("version", "1.0.0");
        info.put("apiBasePath", "/api");
        info.put("swaggerUrl", "/api/swagger-ui.html");
        info.put("knife4jUrl", "/api/doc.html");
        info.put("h2ConsoleUrl", "/api/h2-console");
        return Result.success(info);
    }
}
