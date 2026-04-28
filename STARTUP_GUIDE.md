# 虚拟社交聊天工具 - 启动指南

## ⚠️ 环境检查

当前检测到您的系统**未安装**以下必需软件：
- ❌ Java JDK 17 或更高版本
- ❌ Maven 3.6+

## 📥 第一步：安装必需软件

### 选项1：使用 Chocolatey 自动安装（推荐）

如果您已安装 Chocolatey，可以一键安装：

```powershell
# 以管理员身份运行 PowerShell
choco install openjdk17 maven -y
```

### 选项2：手动安装

#### 1. 安装 Java JDK 17

**下载地址：**
- Oracle JDK: https://www.oracle.com/java/technologies/downloads/#java17
- OpenJDK: https://adoptium.net/temurin/releases/?version=17

**安装步骤：**
1. 下载 Windows x64 Installer
2. 运行安装程序，按提示完成安装
3. 记住安装路径（例如：`C:\Program Files\Java\jdk-17`）

**配置环境变量：**
1. 右键"此电脑" → "属性" → "高级系统设置"
2. 点击"环境变量"
3. 在"系统变量"中找到 `Path`，点击"编辑"
4. 添加 Java bin 目录路径，例如：`C:\Program Files\Java\jdk-17\bin`
5. 新建系统变量 `JAVA_HOME`，值为：`C:\Program Files\Java\jdk-17`

**验证安装：**
```powershell
java -version
```
应该显示类似：`openjdk version "17.0.x"`

#### 2. 安装 Maven

**下载地址：**
https://maven.apache.org/download.cgi

**安装步骤：**
1. 下载 `apache-maven-3.9.x-bin.zip`
2. 解压到某个目录，例如：`C:\Program Files\Apache\maven`

**配置环境变量：**
1. 新建系统变量 `MAVEN_HOME`，值为：`C:\Program Files\Apache\maven`
2. 编辑 `Path` 变量，添加：`%MAVEN_HOME%\bin`

**验证安装：**
```powershell
mvn -version
```
应该显示 Maven 版本信息

---

## 🔑 第二步：配置 API 密钥（可选但推荐）

### 获取 API 密钥

1. 访问阿里云 DashScope 平台：https://dashscope.console.aliyun.com/
2. 注册/登录阿里云账号
3. 进入"API-KEY管理"
4. 创建新的 API 密钥

### 配置密钥

编辑文件：`src/main/resources/application.properties`

找到以下行：
```properties
spring.ai.alibaba.qwen.api-key=your-qwen-api-key-here
```

替换为您的真实密钥：
```properties
spring.ai.alibaba.qwen.api-key=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

**注意：**
- 如果不配置 API 密钥，项目仍可启动，但 AI 功能将使用模拟回复
- 建议配置密钥以获得完整的 AI 对话体验

---

## 🚀 第三步：启动项目

### 方式1：使用启动脚本（推荐）

双击运行项目根目录下的：
```
start.bat
```

脚本会自动：
1. 检查 Java 环境
2. 检查 Maven 环境
3. 编译并启动应用

### 方式2：使用命令行

打开 PowerShell 或 CMD，进入项目目录：

```powershell
cd d:\aiwork\virtual-chat
```

**首次启动（需要下载依赖）：**
```powershell
mvn clean install
mvn spring-boot:run
```

**后续启动：**
```powershell
mvn spring-boot:run
```

### 方式3：使用 IDE（如 IntelliJ IDEA）

1. 用 IDEA 打开项目文件夹
2. 等待 Maven 依赖下载完成
3. 找到 `VirtualChatApplication.java`
4. 右键 → Run 'VirtualChatApplication'

---

## ✅ 第四步：验证启动

### 检查启动成功

看到以下日志表示启动成功：
```
Started VirtualChatApplication in X.XXX seconds
```

### 访问应用

打开浏览器访问：
```
http://localhost:8080
```

应该看到欢迎页面。

### 登录测试

- 用户名：`hibaobao`
- 密码：`hibaobao`

---

## 🧪 第五步：功能测试

### 1. 基础功能测试

- [ ] 成功登录
- [ ] 查看欢迎页面
- [ ] 进入聊天界面

### 2. 好友管理测试

- [ ] 添加一个新朋友
  - 姓名：小美
  - 年龄：25
  - 性别：女
  - 性格：开朗、幽默
  - 特征：喜欢旅行和摄影

- [ ] 编辑朋友信息
- [ ] 删除朋友

### 3. 聊天功能测试

- [ ] 发送消息并收到回复
- [ ] 查看聊天记录
- [ ] 切换连续对话模式
- [ ] 测试数据隔离（与不同朋友聊天）

### 4. 图片生成测试（需配置API密钥）

发送消息：
```
帮我生成'夕阳下的海滩'一段场景的描述
```

应该收到4张图片。

---

## 🔍 常见问题排查

### 问题1：端口8080被占用

**错误信息：**
```
Web server failed to start. Port 8080 was already in use.
```

**解决方案：**
修改 `application.properties`：
```properties
server.port=8081
```

### 问题2：Maven 依赖下载失败

**解决方案：**
```powershell
# 清理后重新下载
mvn clean
mvn dependency:purge-local-repository
mvn install
```

### 问题3：Java 版本不匹配

**错误信息：**
```
Unsupported class file major version
```

**解决方案：**
确保安装的是 Java 17 或更高版本：
```powershell
java -version
```

### 问题4：AI 回复显示"抱歉，我现在有点忙"

**原因：** API 密钥未配置或网络问题

**解决方案：**
1. 检查 `application.properties` 中的 API 密钥是否正确
2. 确认网络连接正常
3. 查看控制台错误日志

### 问题5：编译错误 "找不到符号"

**解决方案：**
```powershell
# 清理并重新编译
mvn clean compile
```

---

## 📊 监控和调试

### 查看应用日志

启动后，控制台会显示实时日志。

### 访问 H2 数据库控制台

浏览器访问：
```
http://localhost:8080/h2-console
```

配置：
- JDBC URL: `jdbc:h2:mem:virtualchat`
- 用户名: `sa`
- 密码: (留空)

可以查看：
- users 表（用户数据）
- friends 表（好友数据）
- messages 表（消息记录）
- conversations 表（对话上下文）

---

## 🎯 快速开始清单

完成以下步骤即可开始使用：

- [ ] 安装 Java JDK 17
- [ ] 安装 Maven 3.6+
- [ ] 配置环境变量
- [ ] 验证 `java -version` 和 `mvn -version`
- [ ] （可选）配置 API 密钥
- [ ] 运行 `start.bat` 或 `mvn spring-boot:run`
- [ ] 浏览器访问 http://localhost:8080
- [ ] 使用 hibaobao/hibaobao 登录
- [ ] 开始创建虚拟朋友并聊天！

---

## 📞 需要帮助？

如果遇到问题：

1. 查看控制台错误日志
2. 检查 [CHECKLIST.md](file:///d:/aiwork/virtual-chat/CHECKLIST.md)
3. 参考 [IMPLEMENTATION_GUIDE.md](file:///d:/aiwork/virtual-chat/IMPLEMENTATION_GUIDE.md)
4. 查看 [QUICK_REFERENCE.md](file:///d:/aiwork/virtual-chat/QUICK_REFERENCE.md)

---

**祝您使用愉快！** 🎉