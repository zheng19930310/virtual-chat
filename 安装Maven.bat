@echo off
chcp 65001 >nul
echo ========================================
echo Maven 自动安装脚本
echo ========================================
echo.

echo [1/3] 下载 Maven...
echo 正在从 Apache 官方镜像下载 Maven 3.9.9...
echo.

powershell -Command "& { Invoke-WebRequest -Uri 'https://mirrors.aliyun.com/apache/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip' -OutFile '%TEMP%\maven.zip' }"

if %errorlevel% neq 0 (
    echo ✗ 下载失败，尝试备用下载地址...
    powershell -Command "& { Invoke-WebRequest -Uri 'https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip' -OutFile '%TEMP%\maven.zip' }"
)

echo.
echo [2/3] 解压 Maven...
powershell -Command "& { Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath 'C:\Maven' -Force }"

echo.
echo [3/3] 配置环境变量...

REM 设置 MAVEN_HOME
setx MAVEN_HOME "C:\Maven\apache-maven-3.9.9" /M

REM 添加到 PATH
for /f "tokens=2,*" %%A in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v Path 2^>nul') do (
    set "OLD_PATH=%%B"
)

if not "%OLD_PATH%"=="" (
    setx Path "%OLD_PATH%;C:\Maven\apache-maven-3.9.9\bin" /M
) else (
    setx Path "C:\Maven\apache-maven-3.9.9\bin" /M
)

echo.
echo ========================================
echo Maven 安装完成！
echo ========================================
echo.
echo 安装位置: C:\Maven\apache-maven-3.9.9
echo.
echo ⚠️  重要：请关闭当前窗口，打开新的CMD窗口
echo    然后运行: mvn -version 验证安装
echo.
echo 清理临时文件...
del /q "%TEMP%\maven.zip" >nul 2>&1

echo.
echo ========================================
echo 下一步：
echo ========================================
echo 1. 关闭此窗口
echo 2. 打开新的 CMD 窗口
echo 3. 运行: cd d:\aiwork\virtual-chat
echo 4. 运行: start.bat
echo.
pause