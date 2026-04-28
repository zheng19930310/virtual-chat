@echo off
chcp 65001 >nul
echo ========================================
echo 虚拟社交聊天工具 - 环境安装助手
echo ========================================
echo.

echo [步骤 1/4] 检查 Chocolatey...
where choco >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Chocolatey 已安装
    goto :install_with_choco
) else (
    echo ✗ Chocolatey 未安装
    goto :manual_install
)

:install_with_choco
echo.
echo [步骤 2/4] 使用 Chocolatey 安装 Java 和 Maven...
echo.
echo 正在安装 OpenJDK 17...
choco install openjdk17 -y --force
if %errorlevel% neq 0 (
    echo ✗ OpenJDK 17 安装失败
    goto :manual_install
)

echo.
echo 正在安装 Maven...
choco install maven -y --force
if %errorlevel% neq 0 (
    echo ✗ Maven 安装失败
    goto :manual_install
)

goto :verify_install

:manual_install
echo.
echo ========================================
echo 请手动安装以下软件：
echo ========================================
echo.
echo 1. Java JDK 17
echo    下载地址: https://adoptium.net/temurin/releases/?version=17
echo    或使用 Oracle JDK: https://www.oracle.com/java/technologies/downloads/#java17
echo.
echo 2. Apache Maven 3.6+
echo    下载地址: https://maven.apache.org/download.cgi
echo.
echo 安装完成后，请配置环境变量并重新运行此脚本。
echo.
echo 详细安装说明请查看: STARTUP_GUIDE.md
echo.
pause
exit /b 1

:verify_install
echo.
echo [步骤 3/4] 验证安装...
echo.

echo 检查 Java...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Java 安装成功
    java -version
) else (
    echo ✗ Java 未正确安装，请重启命令行后重试
    goto :manual_install
)

echo.
echo 检查 Maven...
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Maven 安装成功
    mvn -version | findstr "Apache Maven"
) else (
    echo ✗ Maven 未正确安装，请重启命令行后重试
    goto :manual_install
)

echo.
echo [步骤 4/4] 环境配置完成！
echo.
echo ========================================
echo 下一步操作：
echo ========================================
echo.
echo 1. （可选）配置 API 密钥
echo    编辑文件: src\main\resources\application.properties
echo    替换: spring.ai.alibaba.qwen.api-key=your-qwen-api-key-here
echo.
echo 2. 启动项目
echo    双击运行: start.bat
echo    或执行命令: mvn spring-boot:run
echo.
echo 3. 访问应用
echo    浏览器打开: http://localhost:8080
echo    登录账号: hibaobao / hibaobao
echo.
echo ========================================
echo.

pause