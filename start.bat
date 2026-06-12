@echo off
chcp 65001 >nul
echo ========================================
echo   碳中和管理后端服务 - 启动脚本
echo ========================================
echo.

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java 环境，请先安装 JDK 17 或更高版本
    echo 下载地址: https://adoptium.net/
    pause
    exit /b 1
)

where mvn >nul 2>nul
if %errorlevel% neq 0 (
    if exist mvnw.cmd (
        echo 使用 Maven Wrapper 启动...
        call mvnw.cmd spring-boot:run
    ) else (
        echo [错误] 未找到 Maven 环境，请先安装 Maven 或使用 IDE 启动
        echo.
        echo 推荐使用 IntelliJ IDEA 打开项目，直接运行 CarbonManagementApplication 类
        pause
        exit /b 1
    )
) else (
    echo 使用系统 Maven 启动...
    call mvn spring-boot:run
)

pause
