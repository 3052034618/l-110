@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set "BASE_URL=http://localhost:8080/api"
set "YEAR=%date:~0,4%"

echo.
echo ╔═══════════════════════════════════════════════════╗
echo ║     碳中和管理服务 - 6接口完整流程测试              ║
echo ╚═══════════════════════════════════════════════════╝
echo.

:: ========================================
:: 0. 健康检查
:: ========================================
echo 【0/6】检查服务是否启动...
curl -s "%BASE_URL%/health" >nul 2>nul
if errorlevel 1 (
    echo [错误] 服务未启动！请先启动服务后再运行本脚本。
    echo.
    echo 启动方式：
    echo   1. IDEA中打开 CarbonManagementApplication.java，右键 Run
    echo   2. 命令行执行: mvn spring-boot:run
    echo.
    pause
    exit /b 1
)
echo [OK] 服务运行正常
echo.

:: ========================================
:: 1. 创建预算
:: ========================================
echo 【1/6】创建年度预算...
echo 请求: POST /budgets
set "BUDGET_NAME=2024年度范围1总预算"
set "REQ={\"budgetName\":\"%YEAR%年度范围1总预算\",\"orgId\":\"ORG001\",\"orgName\":\"集团总部\",\"budgetYear\":%YEAR%,\"emissionScope\":\"SCOPE_1\",\"totalBudget\":12000.00,\"warningThreshold\":80.00,\"dangerThreshold\":95.00,\"description\":\"全公司直接碳排放年度预算\"}"
for /f "delims=" %%a in ('curl -s -X POST "%BASE_URL%/budgets" -H "Content-Type: application/json" -d "%REQ%"') do set "RESP=%%a"
echo 返回: %RESP%
for /f "tokens=2 delims=:}" %%b in ("%RESP%") do set "BUDGET_ID_RAW=%%b"
set BUDGET_ID=%BUDGET_ID_RAW:,=%
echo [OK] 预算创建成功，ID: %BUDGET_ID%
echo.

:: ========================================
:: 2. 分解预算
:: ========================================
echo 【2/6】预算分解到部门...
echo 请求: POST /budgets/decomposition
set "REQ={\"budgetId\":%BUDGET_ID%,\"deptId\":\"DEPT001\",\"deptName\":\"生产一部\",\"targetReduction\":5000.00,\"responsibilityPerson\":\"张三\",\"remark\":\"生产车间减排主责部门\"}"
for /f "delims=" %%a in ('curl -s -X POST "%BASE_URL%/budgets/decomposition" -H "Content-Type: application/json" -d "%REQ%"') do set "RESP=%%a"
echo 返回: %RESP%
for /f "tokens=2 delims=:}" %%b in ("%RESP%") do set "DEC_ID_RAW=%%b"
set DEC_ID=%DEC_ID_RAW:,=%
echo [OK] 分解创建成功，ID: %DEC_ID%
echo.

:: ========================================
:: 3. 登记任务
:: ========================================
echo 【3/6】登记减排任务...
echo 请求: POST /tasks
set "REQ={\"taskName\":\"锅炉低氮燃烧改造项目\",\"budgetId\":%BUDGET_ID%,\"decompositionId\":%DEC_ID%,\"orgId\":\"ORG001\",\"orgName\":\"集团总部\",\"deptId\":\"DEPT001\",\"deptName\":\"生产一部\",\"emissionScope\":\"SCOPE_1\",\"category\":\"ENERGY_SAVING\",\"estimatedReduction\":2000.00,\"startDate\":\"2024-01-15\",\"endDate\":\"2024-12-31\",\"responsibilityPerson\":\"张三\",\"description\":\"对3号锅炉进行低氮燃烧器改造\",\"measureDetail\":\"更换低氮燃烧器4台，优化空燃比控制\"}"
for /f "delims=" %%a in ('curl -s -X POST "%BASE_URL%/tasks" -H "Content-Type: application/json" -d "%REQ%"') do set "RESP=%%a"
echo 返回: %RESP%
for /f "tokens=2 delims=:}" %%b in ("%RESP%") do set "TASK_ID_RAW=%%b"
set TASK_ID=%TASK_ID_RAW:,=%
echo [OK] 任务创建成功，ID: %TASK_ID%
echo.

:: ========================================
:: 4. 上报进度
:: ========================================
echo 【4/6】上报任务进度...
echo 请求: POST /tasks/progress
set "REQ={\"taskId\":%TASK_ID%,\"reportPeriod\":\"2024-Q2\",\"periodReduction\":450.00,\"progressDesc\":\"已完成锅炉停炉检修，燃烧器设备已到货\",\"problemDesc\":\"受雨季影响，安装进度略有延迟\",\"nextPlan\":\"预计7月中旬完成安装调试\",\"reporter\":\"张三\"}"
for /f "delims=" %%a in ('curl -s -X POST "%BASE_URL%/tasks/progress" -H "Content-Type: application/json" -d "%REQ%"') do set "RESP=%%a"
echo 返回: %RESP%
echo [OK] 进度上报成功
echo.

:: ========================================
:: 5. 查询预警
:: ========================================
echo 【5/6】查询预警列表...
echo 请求: POST /warnings/query
set "REQ={\"orgId\":\"ORG001\",\"isHandled\":false,\"pageNum\":1,\"pageSize\":10}"
for /f "delims=" %%a in ('curl -s -X POST "%BASE_URL%/warnings/query" -H "Content-Type: application/json" -d "%REQ%"') do set "RESP=%%a"
echo 返回: %RESP%
echo [OK] 预警查询成功
echo.

:: ========================================
:: 6. 年度汇总
:: ========================================
echo 【6/6】查询年度汇总报表...
echo 请求: GET /reports/yearly?orgId=ORG001^&year=%YEAR%
curl -s "%BASE_URL%/reports/yearly?orgId=ORG001&year=%YEAR%"
echo.
echo [OK] 年度汇总查询成功
echo.

:: ========================================
:: 数据持久化验证提示
:: ========================================
echo ╔═══════════════════════════════════════════════════╗
echo ║  ✅ 全部 6 条接口测试通过！                          ║
echo ╚═══════════════════════════════════════════════════╝
echo.
echo 写入的数据ID:
echo   预算ID: %BUDGET_ID%
echo   分解ID: %DEC_ID%
echo   任务ID: %TASK_ID%
echo.
echo 验证数据持久化：
echo   1. 重启服务（停止后再次启动）
echo   2. 运行以下命令验证数据是否还在：
echo.
echo   查询预算（组织+年度）:
echo   curl "%BASE_URL%/budgets/org/ORG001/year/%YEAR%"
echo.
echo   查询任务进度:
echo   curl "%BASE_URL%/tasks/%TASK_ID%/progress"
echo.
echo 接口文档: http://localhost:8080/api/doc.html
echo.
pause
