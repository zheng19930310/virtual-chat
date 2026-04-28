@echo off
chcp 65001 >nul
echo ========================================
echo 虚拟社交聊天Web工具 - 增强启动脚本
echo ========================================
echo.

REM 设置Java和Maven路径
set "JAVA_HOME=C:\Users\11708\.jdks\ms-17.0.18"
set "MAVEN_HOME=D:\JetBrains\IntelliJ IDEA 2025.3.3\plugins\maven\lib\maven3"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo [1/3] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo  错误: Java环境配置失败
    pause
    exit /b 1
)
echo ✓ Java环境检测通过
java -version | findstr "version"
echo.

echo [2/3] 检查Maven环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ✗ 错误: Maven环境配置失败
    pause
    exit /b 1
)
echo ✓ Maven环境检测通过
mvn -version | findstr "Apache Maven"
echo.

echo [3/3] 启动应用...
echo.
echo 正在编译和启动应用，请稍候...
echo.
echo 启动成功后，浏览器访问: http://localhost:8080
echo 登录账号: hibaobao / hibaobao
echo.

mvn spring-boot:run

pause
