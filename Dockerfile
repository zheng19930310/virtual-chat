# 第一阶段：构建应用
FROM eclipse-temurin:17-jdk-alpine AS builder

# 安装 Maven
RUN apk add --no-cache maven

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件（使用阿里云镜像源）
COPY settings.xml /root/.m2/settings.xml

# 复制 pom.xml 并下载依赖（利用 Docker 缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：运行应用
FROM eclipse-temurin:17-jre-alpine

# 安装 curl 用于健康检查
RUN apk add --no-cache curl

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/virtual-chat-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]