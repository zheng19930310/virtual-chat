# Virtual Chat - Docker 快速部署指南

## 📋 概述

本指南将帮助你把 virtual-chat 项目打包成 Docker 镜像，并部署到本地 Docker Desktop。

## ✅ 前置条件

1. **Docker Desktop for Windows** 已安装并运行
   - 确保右下角 Docker 图标显示为绿色
   
2. **项目可以正常编译**
   - 需要 Java 17 和 Maven 环境

## 🚀 快速开始（3步完成）

### 步骤 1：构建 Docker 镜像

在项目根目录打开命令行，执行：

```bash
docker build -t virtual-chat:latest .
```

或使用脚本：
```bash
docker-start.bat
```

### 步骤 2：运行容器

```bash
docker run -d ^
    --name virtual-chat-app ^
    -p 8080:8080 ^
    -v "%cd%\data:/app/data" ^
    virtual-chat:latest
```

或使用 Docker Compose（推荐）：
```bash
docker-compose up -d
```

或使用脚本：
```bash
docker-compose-start.bat
```

### 步骤 3：访问应用

打开浏览器访问：
- **应用首页**: http://localhost:8080
- **H2 控制台**: http://localhost:8080/h2-console

## 📁 创建的文件说明

| 文件 | 用途 |
|------|------|
| `Dockerfile` | Docker 镜像构建配置（多阶段构建优化） |
| `docker-compose.yml` | Docker Compose 编排配置 |
| `.dockerignore` | Docker 构建时忽略的文件 |
| `docker-start.bat` | Docker 启动脚本 |
| `docker-stop.bat` | Docker 停止脚本 |
| `docker-compose-start.bat` | Docker Compose 启动脚本 |
| `docker-compose-stop.bat` | Docker Compose 停止脚本 |
| `DOCKER_DEPLOYMENT.md` | 详细部署文档 |
| `DOCKER_QUICK_START.md` | 本快速指南 |

## 🔧 常用操作

### 查看日志

```bash
# 实时查看日志
docker logs -f virtual-chat-app

# Docker Compose
docker-compose logs -f
```

### 停止服务

```bash
# Docker
docker stop virtual-chat-app
docker rm virtual-chat-app

# Docker Compose
docker-compose down
```

### 重启服务

```bash
docker restart virtual-chat-app
# 或
docker-compose restart
```

### 查看容器状态

```bash
docker ps
# 或
docker-compose ps
```

## 🌐 端口配置

默认暴露 **8080** 端口，如需修改：

**方法 1**: 修改 `docker-compose.yml`
```yaml
ports:
  - "你的端口:8080"  # 例如 "9090:8080"
```

**方法 2**: 修改 docker run 命令
```bash
docker run -d -p 你的端口:8080 ...
```

## 💾 数据持久化

H2 数据库文件保存在 `./data` 目录，通过 volume 挂载实现持久化：

```yaml
volumes:
  - ./data:/app/data
```

即使删除容器，数据也不会丢失。

## 🔍 验证部署

### 1. 检查容器是否运行

```bash
docker ps | findstr virtual-chat
```

应该看到类似输出：
```
CONTAINER ID   IMAGE              STATUS         PORTS                    NAMES
abc123def456   virtual-chat       Up 2 minutes   0.0.0.0:8080->8080/tcp   virtual-chat-app
```

### 2. 检查健康状态

```bash
docker inspect --format='{{.State.Health.Status}}' virtual-chat-app
```

应该返回：`healthy`

### 3. 测试访问

在浏览器打开 http://localhost:8080，应该能看到应用界面。

## ⚙️ 环境变量配置

可以通过环境变量覆盖应用配置：

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - SERVER_PORT=8080
  - SPRING_AI_DASHSCOPE_API_KEY=your-api-key-here
```

## 🐛 故障排查

### 问题 1：Docker 未运行

**症状**: 执行 docker 命令提示无法连接

**解决**: 
1. 启动 Docker Desktop
2. 等待右下角图标变为绿色
3. 重试命令

### 问题 2：端口被占用

**症状**: 启动时提示端口已被使用

**解决**:
```bash
# 查找占用端口的进程
netstat -ano | findstr :8080

# 结束进程（替换 PID 为实际进程ID）
taskkill /F /PID <PID>
```

或修改端口映射。

### 问题 3：构建失败

**症状**: docker build 失败

**解决**:
1. 确保项目可以正常编译：`mvn clean package -DskipTests`
2. 检查网络连接（需要下载依赖）
3. 查看详细错误信息：`docker build --no-cache -t virtual-chat:latest .`

### 问题 4：容器启动后立即退出

**症状**: 容器状态为 Exited

**解决**:
```bash
# 查看日志
docker logs virtual-chat-app

# 常见原因：
# - 端口冲突
# - 配置文件错误
# - 依赖服务未启动
```

## 📊 镜像优化

当前使用**多阶段构建**优化镜像大小：

- **构建阶段**: 使用 JDK + Maven 编译应用
- **运行阶段**: 仅使用 JRE 运行应用

这样可以显著减小最终镜像体积（约减少 50%）。

## 🎯 下一步

1. **配置外部数据库**: 将 H2 替换为 MySQL/PostgreSQL
2. **添加 Nginx 反向代理**: 支持 HTTPS 和负载均衡
3. **配置日志收集**: 集成 ELK 或其他日志系统
4. **设置资源限制**: 防止容器占用过多资源

## 📖 更多资源

- [详细部署文档](DOCKER_DEPLOYMENT.md)
- [Docker 官方文档](https://docs.docker.com/)
- [Docker Compose 文档](https://docs.docker.com/compose/)

---

**祝你部署顺利！** 🎉