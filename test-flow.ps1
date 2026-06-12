# 碳中和管理服务 - 完整流程测试脚本
# 运行前请先启动服务: mvn spring-boot:run 或在 IDE 中运行
# 运行方式: powershell -ExecutionPolicy Bypass -File .\test-flow.ps1

$baseUrl = "http://localhost:8080/api"
$year = (Get-Date).Year

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  碳中和管理服务 - 完整流程测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ========================================
# 0. 健康检查
# ========================================
Write-Host "【0/6】健康检查" -ForegroundColor Yellow
Write-Host "GET $baseUrl/health" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/health" -Method Get -ContentType "application/json"
    Write-Host "✓ 服务状态: $($response.data.status)" -ForegroundColor Green
    Write-Host "  响应: $($response | ConvertTo-Json -Depth 10)" -ForegroundColor DarkGray
} catch {
    Write-Host "✗ 服务未启动，请先启动服务后再运行测试" -ForegroundColor Red
    Write-Host "  启动方式: mvn spring-boot:run" -ForegroundColor Red
    exit 1
}
Write-Host ""

# ========================================
# 1. 创建预算
# ========================================
Write-Host "【1/6】创建年度预算" -ForegroundColor Yellow
$budgetBody = @{
    budgetName = "$year 年度范围1总预算"
    orgId = "ORG001"
    orgName = "集团总部"
    budgetYear = $year
    emissionScope = "SCOPE_1"
    totalBudget = 12000.00
    warningThreshold = 80.00
    dangerThreshold = 95.00
    description = "全公司直接碳排放年度预算"
}
$jsonBody = $budgetBody | ConvertTo-Json
Write-Host "POST $baseUrl/budgets" -ForegroundColor Gray
Write-Host "  请求: $jsonBody" -ForegroundColor DarkGray
$budgetResponse = Invoke-RestMethod -Uri "$baseUrl/budgets" -Method Post -Body $jsonBody -ContentType "application/json"
$budgetId = $budgetResponse.data
Write-Host "✓ 预算创建成功，预算ID: $budgetId" -ForegroundColor Green
Write-Host "  响应: $($budgetResponse | ConvertTo-Json -Depth 10)" -ForegroundColor DarkGray
Write-Host ""

# ========================================
# 2. 预算分解到部门
# ========================================
Write-Host "【2/6】预算分解到部门" -ForegroundColor Yellow
$decompBody = @{
    budgetId = $budgetId
    deptId = "DEPT001"
    deptName = "生产一部"
    targetReduction = 5000.00
    responsibilityPerson = "张三"
    remark = "生产车间减排主责部门"
}
$jsonBody = $decompBody | ConvertTo-Json
Write-Host "POST $baseUrl/budgets/decomposition" -ForegroundColor Gray
Write-Host "  请求: $jsonBody" -ForegroundColor DarkGray
$decompResponse = Invoke-RestMethod -Uri "$baseUrl/budgets/decomposition" -Method Post -Body $jsonBody -ContentType "application/json"
$decompId = $decompResponse.data
Write-Host "✓ 分解创建成功，分解ID: $decompId" -ForegroundColor Green
Write-Host "  响应: $($decompResponse | ConvertTo-Json -Depth 10)" -ForegroundColor DarkGray
Write-Host ""

# ========================================
# 3. 登记减排任务
# ========================================
Write-Host "【3/6】登记减排任务" -ForegroundColor Yellow
$taskBody = @{
    taskName = "锅炉低氮燃烧改造项目"
    budgetId = $budgetId
    decompositionId = $decompId
    orgId = "ORG001"
    orgName = "集团总部"
    deptId = "DEPT001"
    deptName = "生产一部"
    category = "ENERGY_SAVING"
    estimatedReduction = 2000.00
    startDate = "$year-01-15"
    endDate = "$year-12-31"
    responsibilityPerson = "张三"
    description = "对3号锅炉进行低氮燃烧器改造，降低氮氧化物排放"
    measureDetail = "更换低氮燃烧器4台，优化空燃比自动控制系统"
}
$jsonBody = $taskBody | ConvertTo-Json
Write-Host "POST $baseUrl/tasks" -ForegroundColor Gray
Write-Host "  请求: $jsonBody" -ForegroundColor DarkGray
$taskResponse = Invoke-RestMethod -Uri "$baseUrl/tasks" -Method Post -Body $jsonBody -ContentType "application/json"
$taskId = $taskResponse.data
Write-Host "✓ 任务创建成功，任务ID: $taskId" -ForegroundColor Green
Write-Host "  响应: $($taskResponse | ConvertTo-Json -Depth 10)" -ForegroundColor DarkGray
Write-Host ""

# ========================================
# 4. 上报任务进度
# ========================================
Write-Host "【4/6】上报任务进度" -ForegroundColor Yellow
$progressBody = @{
    taskId = $taskId
    reportPeriod = "$year-Q2"
    periodReduction = 450.00
    progressDesc = "已完成锅炉停炉检修，燃烧器设备已到货，准备安装"
    problemDesc = "受雨季影响，现场施工进度略有延迟约1周"
    nextPlan = "预计7月中旬完成安装调试，三季度可投入运行"
    reporter = "张三"
}
$jsonBody = $progressBody | ConvertTo-Json
Write-Host "POST $baseUrl/tasks/progress" -ForegroundColor Gray
Write-Host "  请求: $jsonBody" -ForegroundColor DarkGray
$progressResponse = Invoke-RestMethod -Uri "$baseUrl/tasks/progress" -Method Post -Body $jsonBody -ContentType "application/json"
$progressId = $progressResponse.data
Write-Host "✓ 进度上报成功，进度ID: $progressId" -ForegroundColor Green
Write-Host "  响应: $($progressResponse | ConvertTo-Json -Depth 10)" -ForegroundColor DarkGray
Write-Host ""

# ========================================
# 5. 查询预算进度和预警
# ========================================
Write-Host "【5/6】查询预算执行进度" -ForegroundColor Yellow
Write-Host "GET $baseUrl/budgets/$budgetId/progress" -ForegroundColor Gray
$progressQueryResponse = Invoke-RestMethod -Uri "$baseUrl/budgets/$budgetId/progress" -Method Get -ContentType "application/json"
Write-Host "✓ 预算进度查询成功" -ForegroundColor Green
Write-Host "  预算名称: $($progressQueryResponse.data.budgetName)" -ForegroundColor White
Write-Host "  总预算: $($progressQueryResponse.data.totalBudget) tCO2e" -ForegroundColor White
Write-Host "  已使用: $($progressQueryResponse.data.usedAmount) tCO2e" -ForegroundColor White
Write-Host "  使用率: $($progressQueryResponse.data.usageRate)%" -ForegroundColor White
Write-Host "  预警等级: $($progressQueryResponse.data.warningLevelDesc)" -ForegroundColor White
Write-Host "  关联任务数: $($progressQueryResponse.data.relatedTaskCount)" -ForegroundColor White
Write-Host "  完成率: $($progressQueryResponse.data.reductionRate)%" -ForegroundColor White
Write-Host "  完整响应: $($progressQueryResponse | ConvertTo-Json -Depth 10)" -ForegroundColor DarkGray
Write-Host ""

Write-Host "【5/6】查询预警列表" -ForegroundColor Yellow
$warningQueryBody = @{
    orgId = "ORG001"
    isHandled = $false
    pageNum = 1
    pageSize = 10
}
$jsonBody = $warningQueryBody | ConvertTo-Json
Write-Host "POST $baseUrl/warnings/query" -ForegroundColor Gray
$warningResponse = Invoke-RestMethod -Uri "$baseUrl/warnings/query" -Method Post -Body $jsonBody -ContentType "application/json"
Write-Host "✓ 预警查询成功，共 $($warningResponse.data.total) 条未处理预警" -ForegroundColor Green
Write-Host "  完整响应: $($warningResponse | ConvertTo-Json -Depth 10)" -ForegroundColor DarkGray
Write-Host ""

# ========================================
# 6. 查看年度汇总报表
# ========================================
Write-Host "【6/6】查询年度汇总报表" -ForegroundColor Yellow
Write-Host "GET $baseUrl/reports/yearly?orgId=ORG001&year=$year" -ForegroundColor Gray
$reportResponse = Invoke-RestMethod -Uri "$baseUrl/reports/yearly?orgId=ORG001&year=$year" -Method Get -ContentType "application/json"
Write-Host "✓ 年度汇总查询成功" -ForegroundColor Green
Write-Host "  年度: $($reportResponse.data.year)" -ForegroundColor White
Write-Host "  组织: $($reportResponse.data.orgName)" -ForegroundColor White
Write-Host "  总预算: $($reportResponse.data.totalBudget) tCO2e" -ForegroundColor White
Write-Host "  已使用: $($reportResponse.data.totalUsed) tCO2e" -ForegroundColor White
Write-Host "  剩余: $($reportResponse.data.totalRemaining) tCO2e" -ForegroundColor White
Write-Host "  减排目标: $($reportResponse.data.totalTargetReduction) tCO2e" -ForegroundColor White
Write-Host "  已实现减排: $($reportResponse.data.totalAchievedReduction) tCO2e" -ForegroundColor White
Write-Host "  减排完成率: $($reportResponse.data.reductionCompletionRate)%" -ForegroundColor White
Write-Host "  总任务数: $($reportResponse.data.totalTaskCount)" -ForegroundColor White
Write-Host "  已完成任务: $($reportResponse.data.completedTaskCount)" -ForegroundColor White
Write-Host "  任务完成率: $($reportResponse.data.taskCompletionRate)%" -ForegroundColor White
Write-Host "  预警总数: $($reportResponse.data.warningCount)" -ForegroundColor White
Write-Host "  完整响应: $($reportResponse | ConvertTo-Json -Depth 10)" -ForegroundColor DarkGray
Write-Host ""

# ========================================
# 验证数据持久性查询
# ========================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  验证数据持久化 - 多维度筛选查询" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "【验证1】按组织 + 年度查询预算" -ForegroundColor Yellow
Write-Host "GET $baseUrl/budgets/org/ORG001/year/$year" -ForegroundColor Gray
$verify1 = Invoke-RestMethod -Uri "$baseUrl/budgets/org/ORG001/year/$year" -Method Get -ContentType "application/json"
$createdBudget = $verify1.data | Where-Object { $_.id -eq $budgetId }
if ($createdBudget) {
    Write-Host "✓ 验证成功，刚创建的预算存在: $($createdBudget.budgetName)" -ForegroundColor Green
} else {
    Write-Host "✗ 验证失败，未找到刚创建的预算" -ForegroundColor Red
}
Write-Host ""

Write-Host "【验证2】按排放范围查询任务" -ForegroundColor Yellow
$queryBody = @{
    orgId = "ORG001"
    category = "ENERGY_SAVING"
    pageNum = 1
    pageSize = 10
}
$jsonBody = $queryBody | ConvertTo-Json
Write-Host "POST $baseUrl/tasks/query (按类别筛选)" -ForegroundColor Gray
$verify2 = Invoke-RestMethod -Uri "$baseUrl/tasks/query" -Method Post -Body $jsonBody -ContentType "application/json"
$createdTask = $verify2.data.records | Where-Object { $_.id -eq $taskId }
if ($createdTask) {
    Write-Host "✓ 验证成功，刚创建的任务存在: $($createdTask.taskName)" -ForegroundColor Green
    Write-Host "  任务状态: $($createdTask.statusDesc)" -ForegroundColor White
    Write-Host "  完成率: $($createdTask.completionRate)%" -ForegroundColor White
} else {
    Write-Host "✗ 验证失败，未找到刚创建的任务" -ForegroundColor Red
}
Write-Host ""

Write-Host "【验证3】查询任务进度历史" -ForegroundColor Yellow
Write-Host "GET $baseUrl/tasks/$taskId/progress" -ForegroundColor Gray
$verify3 = Invoke-RestMethod -Uri "$baseUrl/tasks/$taskId/progress" -Method Get -ContentType "application/json"
$foundProgress = $verify3.data | Where-Object { $_.id -eq $progressId }
if ($foundProgress) {
    Write-Host "✓ 验证成功，进度记录存在" -ForegroundColor Green
    Write-Host "  上报周期: $($foundProgress.reportPeriod)" -ForegroundColor White
    Write-Host "  本期减排: $($foundProgress.periodReduction) tCO2e" -ForegroundColor White
    Write-Host "  累计减排: $($foundProgress.cumulativeReduction) tCO2e" -ForegroundColor White
} else {
    Write-Host "✗ 验证失败，未找到进度记录" -ForegroundColor Red
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "提示: 重启服务后再次运行本脚本，验证步骤的查询应该仍然能" -ForegroundColor Yellow
Write-Host "      找到之前写入的数据，确认数据持久化生效。" -ForegroundColor Yellow
Write-Host ""
Write-Host "接口文档: http://localhost:8080/api/swagger-ui.html" -ForegroundColor Cyan
Write-Host "在线文档: http://localhost:8080/api/doc.html" -ForegroundColor Cyan
Write-Host ""
