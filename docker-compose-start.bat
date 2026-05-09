@echo off
chcp 65001 >nul
echo ========================================
echo Virtual Chat - Docker Compose 部署脚本
echo ========================================
echo.

REM 检查 Docker 是否运行
docker info >nul 2>&1
if errorlevel 1 (
    echo ✗ 错误: Docker 未运行或未安装
    echo.
    echo 请确保 Docker Desktop 已启动并正在运行
    echo.
    pause
    exit /b 1
)
echo ✓ Docker 环境检测通过
echo.

echo [1/2] 构建并启动服务...
docker-compose up -d --build
if errorlevel 1 (
    echo ✗ Docker Compose 启动失败
    pause
    exit /b 1
)
echo ✓ 服务启动成功
echo.

echo [2/2] 等待服务就绪...
timeout /t 10 /nobreak >nul

echo.
echo ========================================
echo ✓ Virtual Chat 已成功部署！
echo ========================================
echo.
echo 访问地址: http://localhost:8080
echo H2 控制台: http://localhost:8080/h2-console
echo.
echo 常用命令:
echo   查看日志: docker-compose logs -f
echo   停止服务: docker-compose down
echo   重启服务: docker-compose restart
echo   查看状态: docker-compose ps
echo.
pause