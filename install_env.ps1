# 虚拟社交聊天工具 - PowerShell环境安装脚本
# 以管理员身份运行此脚本

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "虚拟社交聊天工具 - 环境安装助手" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查是否为管理员
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "⚠ 警告: 请以管理员身份运行此脚本" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "右键点击此文件 → '以管理员身份运行'" -ForegroundColor Yellow
    Write-Host ""
    pause
    exit
}

Write-Host "[步骤 1/3] 检查并安装 Chocolatey..." -ForegroundColor Green

# 检查Chocolatey
$chocoExists = Get-Command choco -ErrorAction SilentlyContinue
if ($chocoExists) {
    Write-Host "✓ Chocolatey 已安装" -ForegroundColor Green
} else {
    Write-Host "正在安装 Chocolatey..." -ForegroundColor Yellow
    try {
        Set-ExecutionPolicy Bypass -Scope Process -Force
        Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
        Write-Host "✓ Chocolatey 安装成功" -ForegroundColor Green
    } catch {
        Write-Host "✗ Chocolatey 安装失败" -ForegroundColor Red
        Write-Host "请手动下载并安装: https://chocolatey.org/install" -ForegroundColor Yellow
        pause
        exit
    }
}

Write-Host ""
Write-Host "[步骤 2/3] 安装 Java JDK 17 和 Maven..." -ForegroundColor Green

# 安装OpenJDK 17
Write-Host "正在安装 OpenJDK 17..." -ForegroundColor Yellow
try {
    choco install openjdk17 -y --force
    Write-Host "✓ OpenJDK 17 安装成功" -ForegroundColor Green
} catch {
    Write-Host "✗ OpenJDK 17 安装失败" -ForegroundColor Red
    pause
    exit
}

Write-Host ""

# 安装Maven
Write-Host "正在安装 Maven..." -ForegroundColor Yellow
try {
    choco install maven -y --force
    Write-Host "✓ Maven 安装成功" -ForegroundColor Green
} catch {
    Write-Host "✗ Maven 安装失败" -ForegroundColor Red
    pause
    exit
}

Write-Host ""
Write-Host "[步骤 3/3] 验证安装..." -ForegroundColor Green

# 刷新环境变量
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# 验证Java
Write-Host ""
Write-Host "检查 Java..." -ForegroundColor Cyan
try {
    $javaVersion = java -version 2>&1
    Write-Host "✓ Java 安装成功" -ForegroundColor Green
    Write-Host $javaVersion[0] -ForegroundColor Gray
} catch {
    Write-Host "✗ Java 未正确安装，请重启后重试" -ForegroundColor Red
}

# 验证Maven
Write-Host ""
Write-Host "检查 Maven..." -ForegroundColor Cyan
try {
    $mavenVersion = mvn -version 2>&1 | Select-String "Apache Maven"
    Write-Host "✓ Maven 安装成功" -ForegroundColor Green
    Write-Host $mavenVersion -ForegroundColor Gray
} catch {
    Write-Host "✗ Maven 未正确安装，请重启后重试" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "环境安装完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "下一步操作：" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. 关闭此窗口，打开新的PowerShell或CMD" -ForegroundColor White
Write-Host "2. 进入项目目录: cd d:\aiwork\virtual-chat" -ForegroundColor White
Write-Host "3. 启动项目: .\start.bat 或 mvn spring-boot:run" -ForegroundColor White
Write-Host "4. 浏览器访问: http://localhost:8080" -ForegroundColor White
Write-Host ""
Write-Host "登录信息: hibaobao / hibaobao" -ForegroundColor Yellow
Write-Host ""

pause