@echo off
chcp 65001 >nul
echo ========================================
echo Virtual Chat - Docker 部署脚本
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

echo [1/3] 构建 Docker 镜像...
docker build -t virtual-chat:latest .
if errorlevel 1 (
    echo ✗ Docker 镜像构建失败
    pause
    exit /b 1
)
echo ✓ Docker 镜像构建成功
echo.

echo [2/3] 停止并移除旧容器（如果存在）...
docker stop virtual-chat-app >nul 2>&1
docker rm virtual-chat-app >nul 2>&1
echo ✓ 清理完成
echo.

echo [3/3] 启动 Docker 容器...
docker run -d ^
    --name virtual-chat-app ^
    -p 8080:8080 ^
    -v "%cd%\data:/app/data" ^
    -e SPRING_PROFILES_ACTIVE=prod ^
    -e SERVER_PORT=8080 ^
    --restart unless-stopped ^
    virtual-chat:latest

if errorlevel 1 (
    echo ✗ Docker 容器启动失败
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✓ Virtual Chat 已成功部署到 Docker！
echo ========================================
echo.
echo 访问地址: http://localhost:8080
echo H2 控制台: http://localhost:8080/h2-console
echo.
echo 常用命令:
echo   查看日志: docker logs -f virtual-chat-app
echo   停止容器: docker stop virtual-chat-app
echo   启动容器: docker start virtual-chat-app
echo   删除容器: docker rm virtual-chat-app
echo   删除镜像: docker rmi virtual-chat:latest
echo.
pause