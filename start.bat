@echo off
chcp 65001 >nul
echo ========================================
echo 虚拟社交聊天Web工具 - 启动脚本
echo ========================================
echo.

REM 设置Maven路径(使用IntelliJ IDEA内置Maven)
set "MAVEN_PATH=D:\JetBrains\IntelliJ IDEA 2025.3.3\plugins\maven\lib\maven3\bin\mvn.cmd"

echo [1/3] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo ✗ 错误: 未检测到Java环境
    echo.
    echo 请先安装 Java JDK 17 或更高版本
    echo.
    pause
    exit /b 1
)
echo ✓ Java环境检测通过
java -version | findstr "version"
echo.

echo [2/3] 检查Maven环境...
if exist "%MAVEN_PATH%" (
    echo ✓ 使用IntelliJ IDEA内置Maven
    "%MAVEN_PATH%" -version | findstr "Apache Maven"
) else (
    mvn -version >nul 2>&1
    if errorlevel 1 (
        echo ✗ 错误: 未检测到Maven环境
        echo.
        echo 请检查Maven安装或配置
        echo.
        pause
        exit /b 1
    )
    echo ✓ Maven环境检测通过
    mvn -version | findstr "Apache Maven"
    set "MAVEN_PATH=mvn"
)
echo.

echo [3/3] 启动应用...
echo.
echo 正在编译和启动应用，请稍候...
echo.

"%MAVEN_PATH%" spring-boot:run

pause