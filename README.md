# 虚拟社交聊天Web工具

## 项目简介
这是一个基于Java Spring Alibaba AI和通义千问大模型的虚拟社交聊天Web工具。用户可以创建具有个性化特征的虚拟朋友，并与之进行智能对话。

## 功能特性

### 1. 用户认证
- 简单的登录验证
- 默认账号: `hibaobao` / 密码: `hibaobao`
- Session会话管理

### 2. 好友管理
- **添加好友**: 创建虚拟朋友，设置姓名、年龄、性别、性格特征和人物描述
- **编辑好友**: 随时修改好友的个人信息
- **删除好友**: 移除不需要的虚拟朋友
- **好友列表**: 展示所有已创建的虚拟朋友

### 3. 智能聊天
- 类似微信的聊天界面
- 基于通义千问大模型的智能回复
- 根据好友的性格和特征生成个性化对话
- **两种对话模式**：
  - 单条交流模式（默认）：每次对话独立
  - 连续对话模式：携带上下文，实现连贯对话
- 聊天记录保存和展示
- 消息时间戳显示
- **场景图片生成**：发送"帮我生成'XX'一段场景的描述"自动生成4张图片

### 4. 数据隔离
- 每个虚拟朋友的数据完全隔离
- 聊天记录仅对对应的好友可见
- 不同好友之间的对话上下文互不影响
- 确保用户隐私和数据安全

## 技术栈

- **后端**: Java 17 + Spring Boot 3.2.0
- **AI框架**: Spring Alibaba AI
- **AI模型**: 
  - 通义千问 (Qwen) - 文本对话
  - 通义万相 (Wanx) - 图像生成
- **数据库**: H2 内存数据库
- **前端**: HTML5 + CSS3 + JavaScript + Bootstrap 5
- **构建工具**: Maven

## 快速开始

### 前置要求
- JDK 17 或更高版本
- Maven 3.6+
- 通义千问API密钥 (从阿里云获取)

### 配置步骤

1. **克隆项目**
```bash
cd virtual-chat
```

2. **配置API密钥**
   
   编辑 `src/main/resources/application.properties` 文件，替换为您的API密钥:
```properties
spring.ai.alibaba.qwen.api-key=your-qwen-api-key-here
```

   获取API密钥:
   - 访问 [阿里云DashScope平台](https://dashscope.console.aliyun.com/)
   - 注册/登录账号
   - 创建API密钥

3. **编译项目**
```bash
mvn clean package
```

4. **运行应用**
```bash
java -jar target/virtual-chat-0.0.1-SNAPSHOT.jar
```

或者使用Maven运行:
```bash
mvn spring-boot:run
```

5. **访问应用**
   
   打开浏览器访问: http://localhost:8080

### 使用说明

1. **登录**: 使用账号 `hibaobao` 和密码 `hibaobao` 登录
2. **添加好友**: 点击左侧“添加”按钮创建虚拟朋友
3. **设置角色**: 填写朋友的姓名、年龄、性别、性格和特征描述
4. **开始聊天**: 点击好友进入聊天界面，发送消息进行对话
5. **切换对话模式**:
   - 点击右上角“连续对话”按钮开启/关闭连续对话模式
   - 开启时：AI会记住之前的对话内容，实现连贯交流
   - 关闭时：每次对话独立，不携带上下文
6. **生成场景图片**:
   - 在聊天中输入：“帮我生成'XX'一段场景的描述”
   - 系统会自动生成4张与描述相关的图片
   - 图片会直接显示在聊天界面中

## 项目结构

```
virtual-chat/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/virtualchat/
│   │   │       ├── VirtualChatApplication.java    # 主启动类
│   │   │       ├── config/
│   │   │       │   └── DataInitializer.java       # 数据初始化
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java        # 认证控制器
│   │   │       │   ├── FriendController.java      # 好友管理控制器
│   │   │       │   ├── MessageController.java     # 消息控制器
│   │   │       │   └── PageController.java        # 页面控制器
│   │   │       ├── model/
│   │   │       │   ├── User.java                  # 用户实体
│   │   │       │   ├── Friend.java                # 好友实体
│   │   │       │   └── Message.java               # 消息实体
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java        # 用户仓库
│   │   │       │   ├── FriendRepository.java      # 好友仓库
│   │   │       │   └── MessageRepository.java     # 消息仓库
│   │   │       └── service/
│   │   │           └── ChatService.java           # 聊天服务
│   │   └── resources/
│   │       ├── application.properties             # 配置文件
│   │       └── templates/
│   │           ├── index.html                     # 首页
│   │           ├── login.html                     # 登录页
│   │           └── chat.html                      # 聊天页
│   └── test/
├── pom.xml                                        # Maven配置
└── README.md                                      # 项目说明
```

## API接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/check` - 检查登录状态

### 好友管理
- `GET /api/friends` - 获取好友列表
- `POST /api/friends` - 添加好友
- `PUT /api/friends/{id}` - 修改好友
- `DELETE /api/friends/{id}` - 删除好友

### 消息管理
- `GET /api/messages/{friendId}` - 获取聊天记录
- `POST /api/messages` - 发送消息
- `POST /api/messages/context` - 切换连续对话模式
- `GET /api/messages/context/{friendId}` - 获取对话上下文状态

### 图片生成
- `POST /api/images/generate` - 生成场景图片

## 数据库

项目使用H2内存数据库，数据在应用重启后会丢失。如需持久化，可以修改配置使用MySQL或其他数据库。

### H2控制台
访问 http://localhost:8080/h2-console 查看数据库
- JDBC URL: `jdbc:h2:mem:virtualchat`
- 用户名: `sa`
- 密码: (空)

## 注意事项

1. **API密钥**: 必须配置以下API密钥才能使用全部功能
   - 通义千问API密钥（聊天功能）
   - 通义万相API密钥（图片生成功能）
2. **网络连接**: 需要能够访问阿里云API服务
3. **费用**: 使用阿里云AI API可能产生费用，请查看阿里云定价
4. **数据安全**: 当前为演示项目，密码未加密，生产环境需加强安全措施
5. **数据隔离**: 每个虚拟朋友的对话上下文独立存储，确保隐私

## 开发计划

- [x] 连续对话模式（携带上下文记忆）
- [x] 单条交流模式（独立对话）
- [x] 数据隔离（每个好友独立）
- [x] 场景图片生成功能
- [x] 上下文记忆功能完善（默认启用）
- [x] 图片生成API集成
- [ ] 支持更多AI模型
- [ ] 添加群组聊天功能
- [ ] 实现消息搜索
- [ ] 支持文件传输
- [ ] 添加语音消息
- [ ] 用户注册功能
- [ ] 数据持久化到MySQL
- [ ] 移动端适配优化

## 许可证

本项目仅供学习和研究使用。

## 联系方式

如有问题或建议，欢迎反馈！