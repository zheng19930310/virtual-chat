# 基于Spring Boot + AI的虚拟社交聊天工具完整教程

> **作者**: 您的名字  
> **发布日期**: 2026-04-28  
> **技术栈**: Spring Boot 3.2.0 + Spring AI + Thymeleaf + H2 Database

---

## 一、项目简介

这是一个基于Spring Boot和AI技术的虚拟社交聊天工具,具有以下核心功能:

- 💬 **智能聊天**: 集成通义千问大模型,实现智能对话
- 🎨 **AI图片生成**: 支持通义万相图片生成,为聊天增色
- 👥 **好友管理**: 模拟社交关系,创建虚拟好友
- 💾 **数据持久化**: H2数据库保存聊天记录和用户信息
- 🔐 **用户认证**: 完整的登录注册系统
- 🤖 **数字人集成**: 阿里云数字人框架(待完善)

**在线体验**: http://localhost:8080

**项目地址**: https://gitee.com/mobuhan/virtual-friend-chat

---

## 二、技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | 开发语言 |
| Spring Boot | 3.2.0 | Web框架 |
| Spring AI | 1.0.0-M4 | AI集成 |
| Thymeleaf | 3.1.2 | 模板引擎 |
| H2 Database | 2.2.224 | 文件数据库 |
| Bootstrap | 5.3.0 | UI框架 |
| DashScope API | - | 阿里云AI服务 |

---

## 三、环境准备

### 3.1 必需软件

- **JDK 17或更高版本**
- **Maven 3.6+**
- **Git** (用于代码管理)

### 3.2 阿里云账号配置

需要申请以下两个API Key:

1. **DashScope API Key** (通义千问/通义万相)
   - 访问: https://dashscope.console.aliyun.com/
   - 创建API Key

2. **阿里云AccessKey** (可选,用于TTS等高级功能)
   - 访问: https://ram.console.aliyun.com/profile/access-keys
   - 创建AccessKey

---

## 四、项目配置

### 4.1 克隆项目

```bash
git clone https://gitee.com/mobuhan/virtual-friend-chat.git
cd virtual-friend-chat
```

**[图片占位符1: Git克隆命令截图]**
> 📸 请在此处插入: Git克隆项目的终端截图

### 4.2 配置API Key

编辑 `src/main/resources/application.properties` 文件:

```properties
# 通义千问API配置
spring.ai.dashscope.api-key=您的DashScope_API_Key

# 聊天模型配置
spring.ai.dashscope.chat.options.model=qwen-turbo

# 图片生成配置
spring.ai.dashscope.image.options.model=wanx-v1
image.generation.model=wanx-v1
image.generation.size=1024*1024

# 阿里云AccessKey (可选)
aliyun.access-key-id=您的AccessKey_ID
aliyun.access-key-secret=您的AccessKey_Secret
```

**[图片占位符2: API Key配置界面截图]**
> 📸 请在此处插入: 在阿里云控制台创建API Key的截图

**[图片占位符3: application.properties文件编辑截图]**
> 📸 请在此处插入: 编辑器中打开application.properties文件的截图

### 4.3 数据库配置

项目使用H2文件数据库,默认配置:

```properties
spring.datasource.url=jdbc:h2:file:./data/virtualchat
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

数据库文件会自动创建在 `data/virtualchat.mv.db`,无需手动配置。

---

## 五、项目启动

### 5.1 方式一: 使用启动脚本 (推荐)

**Windows系统**:
```bash
# 双击运行
start.bat
```

启动脚本会自动:
- ✅ 检测Java环境
- ✅ 检测Maven环境  
- ✅ 编译并启动应用

**[图片占位符4: 启动脚本运行截图]**
>  请在此处插入: 运行start.bat后的终端输出截图

### 5.2 方式二: 使用Maven命令

```bash
# 进入项目目录
cd virtual-friend-chat

# 使用Maven启动
mvn spring-boot:run

# 或者先编译再运行
mvn clean package
java -jar target/virtual-chat-0.0.1-SNAPSHOT.jar
```

**[图片占位符5: Maven启动命令截图]**
> 📸 请在此处插入: mvn spring-boot:run命令执行过程截图

### 5.3 启动成功标志

看到以下日志表示启动成功:

```
Tomcat started on port 8080 (http) with context path ''
Started VirtualChatApplication in 10.711 seconds
```

**[图片占位符6: 启动成功日志截图]**
> 📸 请在此处插入: 完整的启动成功日志截图

### 5.4 访问应用

浏览器打开: **http://localhost:8080**

**[图片占位符7: 登录页面截图]**
> 📸 请在此处插入: 应用启动后的登录页面截图

---

## 六、功能使用说明

### 6.1 用户登录

系统预置了测试账号:

- **用户名**: admin
- **密码**: admin123

或者直接注册新账号。

**[图片占位符8: 登录界面截图]**
> 📸 请在此处插入: 登录界面的完整截图

**[图片占位符9: 注册界面截图]**
>  请在此处插入: 注册新账号的界面截图

### 6.2 聊天界面

登录成功后进入聊天主界面:

**[图片占位符10: 聊天主界面截图]**
> 📸 请在此处插入: 聊天主界面的完整截图,包含左侧好友列表和右侧聊天区域

#### 界面说明:

- **左侧**: 好友列表,显示所有虚拟好友
- **右侧**: 聊天窗口,显示消息记录
- **顶部**: 当前聊天对象名称
- **底部**: 消息输入框和发送按钮

### 6.3 发送消息

1. 在底部输入框输入消息
2. 点击"发送"按钮或按Enter键
3. AI会自动回复

**[图片占位符11: 发送消息截图]**
> 📸 请在此处插入: 输入消息并发送的截图

**[图片占位符12: AI回复截图]**
> 📸 请在此处插入: AI智能回复的消息截图

### 6.4 AI图片生成

聊天过程中,AI可能会自动生成图片,您也可以主动请求:

**示例对话**:
```
用户: 给我画一只可爱的小猫
AI: [生成图片] 🐱 这是一只可爱的小猫...
```

**[图片占位符13: AI生成图片截图]**
>  请在此处插入: AI生成图片的聊天截图,展示图片效果

**[图片占位符14: 生成的图片大图]**
> 📸 请在此处插入: AI生成的高清图片大图

### 6.5 添加好友

1. 点击顶部"添加好友"按钮
2. 输入好友昵称和描述
3. 点击"保存"

**[图片占位符15: 添加好友界面截图]**
> 📸 请在此处插入: 添加好友的弹窗或界面截图

**[图片占位符16: 好友列表截图]**
> 📸 请在此处插入: 添加好友后的好友列表截图

### 6.6 切换好友

点击左侧好友列表中的不同好友,切换到对应的聊天窗口。

**[图片占位符17: 切换好友截图]**
> 📸 请在此处插入: 点击不同好友切换聊天的截图

### 6.7 查看聊天记录

所有聊天记录自动保存,切换好友后可以查看历史消息。

**[图片占位符18: 历史消息截图]**
> 📸 请在此处插入: 查看历史聊天记录的截图

---

## 七、项目结构

```
virtual-chat/
── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/virtualchat/
│   │   │       ├── controller/        # 控制器层
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── FriendController.java
│   │   │       │   ├── MessageController.java
│   │   │       │   └── AvatarController.java
│   │   │       ├── model/             # 数据模型
│   │   │       │   ├── User.java
│   │   │       │   ├── Friend.java
│   │   │       │   ├── Message.java
│   │   │       │   └── Conversation.java
│   │   │       ├── repository/        # 数据访问层
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── FriendRepository.java
│   │   │       │   └── MessageRepository.java
│   │   │       ├── service/           # 业务逻辑层
│   │   │       │   ├── ChatService.java
│   │   │       │   ├── ImageService.java
│   │   │       │   └── AvatarService.java
│   │   │       └── config/            # 配置类
│   │   │           ├── SecurityConfig.java
│   │   │           ├── WebMvcConfig.java
│   │   │           ── DataInitializer.java
│   │   └── resources/
│   │       ├── templates/             # 前端页面
│   │       │   ├── index.html
│   │       │   ├── login.html
│   │       │   └── chat.html
│   │       └── application.properties # 配置文件
│   └── test/                          # 测试代码
├── data/                              # H2数据库文件
├── generated-images/                  # AI生成的图片
── pom.xml                            # Maven配置
├── start.bat                          # 启动脚本
└── README.md                          # 项目说明
```

**[图片占位符19: 项目结构截图]**
> 📸 请在此处插入: IDE中显示的项目目录结构截图

---

## 八、核心功能实现

### 8.1 AI聊天功能

使用Spring AI集成通义千问大模型:

```java
@Service
public class ChatService {
    
    @Autowired
    private ChatClient chatClient;
    
    public String chat(String message) {
        return chatClient.call(message);
    }
}
```

### 8.2 图片生成功能

使用DashScope API异步生成图片:

```java
@Service
public class ImageService {
    
    public String generateImage(String prompt) {
        // 1. 提交图片生成任务
        String taskId = submitImageTask(prompt);
        
        // 2. 轮询任务状态
        String imageUrl = waitForTaskCompletion(taskId);
        
        // 3. 下载图片到本地
        return downloadImage(imageUrl);
    }
}
```

**[图片占位符20: 代码编辑器截图]**
> 📸 请在此处插入: ImageService.java核心代码截图

### 8.3 数据持久化

使用Spring Data JPA操作H2数据库:

```java
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String content;
    private String sender;
    private LocalDateTime createdAt;
    
    @ManyToOne
    private Conversation conversation;
}
```

---

## 九、常见问题

### Q1: 启动时报"端口8080已被占用"?

**解决方案**:
```bash
# Windows查找占用端口的进程
netstat -ano | findstr :8080

# 终止进程 (替换PID)
taskkill /F /PID <进程ID>
```

**[图片占位符21: 端口占用查询截图]**
> 📸 请在此处插入: netstat命令查询端口占用的截图

### Q2: 图片生成失败?

**检查项**:
1. ✅ DashScope API Key是否正确
2. ✅ 网络连接是否正常
3. ✅ 查看控制台日志是否有错误信息

**[图片占位符22: 错误日志截图]**
> 📸 请在此处插入: 图片生成失败时的错误日志截图

### Q3: 数据库文件损坏?

**解决方案**:
```bash
# 删除数据库文件,系统会自动重建
rm -rf data/virtualchat.mv.db
```

### Q4: Maven依赖下载失败?

**解决方案**:
```bash
# 清理Maven缓存
mvn clean

# 重新下载依赖
mvn dependency:resolve
```

---

## 十、进阶功能

### 10.1 自定义AI角色

编辑 `application.properties`:

```properties
# 修改AI模型
spring.ai.dashscope.chat.options.model=qwen-plus

# 修改系统提示词(在代码中配置)
```

### 10.2 自定义好友形象

修改 `chat.html` 中的CSS样式:

```css
.friend-avatar {
    background-image: url('/images/custom-avatar.jpg');
}
```

### 10.3 数字人集成 (待完善)

需要先购买阿里云数字人服务,获取实例ID后配置:

```properties
aliyun.avatar.project-id=您的项目ID
aliyun.avatar.instance-id=您的实例ID
```

**[图片占位符23: 数字人控制台截图]**
> 📸 请在此处插入: 阿里云数字人控制台的截图

---

## 十一、项目亮点

1. ** 完整的全栈架构**: 后端Spring Boot + 前端Thymeleaf + Bootstrap
2. **🤖 AI深度集成**: 聊天、图片生成均使用AI能力
3. **💾 轻量级部署**: H2文件数据库,无需额外安装数据库
4. **🔐 安全认证**: Spring Security保护用户数据
5. **📦 开箱即用**: 提供启动脚本,一键运行
6. **🎨 美观界面**: 现代化UI设计,用户体验良好

---

## 十二、技术难点与解决方案

### 难点1: DashScope图片生成API是异步的

**问题**: API返回PENDING状态,不能立即获取图片URL

**解决方案**: 
- 实现任务提交和状态轮询机制
- 最多轮询20次,每次间隔2秒
- 当状态变为SUCCEEDED时提取URL

### 难点2: H2数据库文件锁定

**问题**: 应用异常退出后,数据库文件被锁定无法再次启动

**解决方案**:
- 终止残留的Java进程
- 或者使用H2服务器模式

### 难点3: 环境变量配置

**问题**: 不同环境下JDK和Maven路径不同

**解决方案**:
- 提供多种启动方式
- 使用IDEA内置的JDK和Maven
- 编写兼容不同环境的启动脚本

---

## 十三、后续规划

- [ ] 完善阿里云数字人视频流集成
- [ ] 添加TTS语音播报功能
- [ ] 实现消息加密传输
- [ ] 添加更多AI模型支持
- [ ] 优化移动端适配
- [ ] 添加文件上传功能
- [ ] 实现群聊功能

---

## 十四、总结

本项目是一个功能完整的AI虚拟社交聊天工具,集成了通义千问大模型和通义万相图片生成能力。项目采用Spring Boot框架,结构清晰,易于理解和二次开发。

**适合人群**:
-  学习Spring Boot的初学者
-  对AI应用开发感兴趣的开发者
- 💬 想构建聊天应用的团队
-  作为毕业设计或课程项目

**学习价值**:
- Spring Boot Web开发
- Spring AI集成
- 异步任务处理
- 数据库设计与操作
- 前端模板引擎使用

---

## 十五、参考资源

- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Spring AI文档](https://spring.io/projects/spring-ai)
- [阿里云DashScope文档](https://help.aliyun.com/zh/dashscope/)
- [Thymeleaf官方文档](https://www.thymeleaf.org/)
- [H2 Database文档](https://www.h2database.com/)

---

## 附录: 完整配置文件

```properties
# application.properties 完整内容

# 服务器配置
server.port=8080

# H2数据库配置
spring.datasource.url=jdbc:h2:file:./data/virtualchat
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# 通义千问API配置
spring.ai.dashscope.api-key=您的API_Key
spring.ai.dashscope.chat.options.model=qwen-turbo

# 图片生成配置
spring.ai.dashscope.image.options.model=wanx-v1
image.generation.model=wanx-v1
image.generation.size=1024*1024

# 静态资源配置
spring.web.resources.static-locations=classpath:/static/,file:./generated-images/
spring.mvc.static-path-pattern=/images/**
```

---

**[图片占位符24: 项目运行效果汇总图]**
> 📸 请在此处插入: 包含登录、聊天、图片生成等多个功能的拼贴图

**[图片占位符25: 项目Gitee仓库截图]**
> 📸 请在此处插入: Gitee仓库主页的截图

---

## 写在最后

如果您觉得这个项目对您有帮助,欢迎:
-  给项目点个Star
-  Fork项目二次开发
-  提交Issue反馈问题
-  在评论区交流心得

**项目地址**: https://gitee.com/mobuhan/virtual-friend-chat

感谢您的阅读! 🎉

---

> **声明**: 本文仅供学习参考,AI服务需要消耗API额度,请合理使用。
