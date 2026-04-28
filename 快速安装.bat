@echo off
chcp 65001 >nul
echo ========================================
echo 虚拟社交聊天工具 - 快速环境安装
echo ========================================
echo.
echo 正在安装 Chocolatey 包管理器...
echo.

powershell -NoProfile -ExecutionPolicy Bypass -Command "Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))"

if %errorlevel% neq 0 (
    echo.
    echo ✗ Chocolatey 安装失败
    echo.
    echo 请手动下载安装:
    echo 1. Java JDK 17: https://adoptium.net/temurin/releases/?version=17
    echo 2. Maven: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

echo.
echo ✓ Chocolatey 安装成功
echo.
echo 正在安装 Java JDK 17...
call choco install openjdk17 -y --force

echo.
echo 正在安装 Maven...
call choco install maven -y --force

echo.
echo ========================================
echo 安装完成！
echo ========================================
echo.
echo 请关闭此窗口，打开新的命令行窗口
echo 然后运行: start.bat
echo.
pause