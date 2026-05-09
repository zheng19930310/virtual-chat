@echo off
chcp 65001 >nul
echo ========================================
echo Virtual Chat - Docker Compose 停止脚本
echo ========================================
echo.

REM 检查 Docker 是否运行
docker info >nul 2>&1
if errorlevel 1 (
    echo ✗ 错误: Docker 未运行
    pause
    exit /b 1
)

echo 正在停止服务...
docker-compose down

echo.
echo ✓ 服务已停止
echo.
pause