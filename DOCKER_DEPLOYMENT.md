# Virtual Chat - Docker 部署指南

## 前置要求

1. **Docker Desktop for Windows** 已安装并正在运行
   - 下载地址: https://www.docker.com/products/docker-desktop/
   - 确保 Docker Desktop 启动后右下角图标显示为绿色

2. **项目准备**
   - 确保项目可以正常编译（需要 Java 17 和 Maven）

## 快速开始

### 方法一：使用 Docker Compose（推荐）

#### 1. 启动服务

双击运行 `docker-compose-start.bat` 或在命令行执行：

```bash
docker-compose up -d --build
```

#### 2. 访问应用

- 应用地址: http://localhost:8080
- H2 控制台: http://localhost:8080/h2-console

#### 3. 停止服务

双击运行 `docker-compose-stop.bat` 或在命令行执行：

```bash
docker-compose down
```

### 方法二：使用 Docker 命令

#### 1. 构建镜像

```bash
docker build -t virtual-chat:latest .
```

#### 2. 运行容器

```bash
docker run -d ^
    --name virtual-chat-app ^
    -p 8080:8080 ^
    -v "%cd%\data:/app/data" ^
    -e SPRING_PROFILES_ACTIVE=prod ^
    -e SERVER_PORT=8080 ^
    --restart unless-stopped ^
    virtual-chat:latest
```

或者直接使用脚本：

```bash
docker-start.bat
```

#### 3. 停止容器

```bash
docker-stop.bat
```

## 常用命令

### 查看日志

```bash
# Docker Compose
docker-compose logs -f

# Docker
docker logs -f virtual-chat-app
```

### 查看容器状态

```bash
# Docker Compose
docker-compose ps

# Docker
docker ps
```

### 重启服务

```bash
# Docker Compose
docker-compose restart

# Docker
docker restart virtual-chat-app
```

### 删除镜像

```bash
docker rmi virtual-chat:latest
```

## 数据持久化

H2 数据库文件存储在 `./data` 目录中，通过 Docker volume 挂载到容器中，确保数据在容器重启后不会丢失。

## 端口配置

默认端口为 **8080**，如需修改：

1. 编辑 `docker-compose.yml` 中的端口映射：
   ```yaml
   ports:
     - "你的端口:8080"
   ```

2. 或者在运行 docker run 时指定：
   ```bash
   docker run -d -p 你的端口:8080 ...
   ```

## 环境变量

可以通过环境变量覆盖应用配置：

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - SERVER_PORT=8080
  - SPRING_AI_DASHSCOPE_API_KEY=your-api-key
```

## 健康检查

容器配置了健康检查，每 30 秒检查一次应用状态：

```bash
docker inspect --format='{{.State.Health.Status}}' virtual-chat-app
```

## 故障排查

### 1. Docker 未运行

确保 Docker Desktop 已启动，任务栏右下角图标为绿色。

### 2. 端口被占用

如果 8080 端口被占用，修改端口映射或使用以下命令查找占用端口的进程：

```bash
netstat -ano | findstr :8080
```

### 3. 构建失败

确保项目可以正常编译：

```bash
mvn clean package -DskipTests
```

### 4. 查看详细日志

```bash
docker-compose logs
# 或
docker logs virtual-chat-app
```

## 生产环境建议

1. **使用外部数据库**: 将 H2 替换为 MySQL 或 PostgreSQL
2. **配置 HTTPS**: 使用 Nginx 反向代理配置 SSL
3. **资源限制**: 在 docker-compose.yml 中添加资源限制：
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '2'
         memory: 2G
   ```
4. **日志管理**: 配置日志轮转和外部日志收集

## 文件说明

- `Dockerfile` - Docker 镜像构建文件
- `docker-compose.yml` - Docker Compose 配置文件
- `.dockerignore` - Docker 构建忽略文件
- `docker-start.bat` - Docker 启动脚本
- `docker-stop.bat` - Docker 停止脚本
- `docker-compose-start.bat` - Docker Compose 启动脚本
- `docker-compose-stop.bat` - Docker Compose 停止脚本

## 更多信息

- [Docker 官方文档](https://docs.docker.com/)
- [Docker Compose 文档](https://docs.docker.com/compose/)
- [Spring Boot Docker 支持](https://spring.io/guides/topicals/spring-boot-docker/)