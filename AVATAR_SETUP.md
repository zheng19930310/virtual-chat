# 阿里云数字人集成配置指南

## 功能概述

已成功在虚拟聊天页面集成阿里云2D数字人,实现:
- ✅ 可拖拽的视频窗口
- ✅ 聊天头部数字人开关按钮
- ✅ 数字人状态显示
- ✅ AI回复后数字人播报(待完善RTC配置)

## 当前状态

### 已完成
1. 后端AvatarService和AvatarController
2. 前端数字人视频窗口UI
3. 拖拽功能
4. 状态管理
5. 消息播报接口

### 待完成(需要RTC配置)
- ⏳ RTC视频流拉取
-  WebSocket音频推送
- ⏳ TTS语音合成

## 配置步骤

### 1. 创建阿里云RTC应用

1. 访问 [阿里云视频直播控制台](https://live.console.aliyun.com/)
2. 点击左侧 **RTC** → **应用管理**
3. 点击 **创建应用**
4. 填写应用名称(如: `virtual-chat-avatar`)
5. 创建成功后,记录:
   - **应用ID (App ID)**: 格式如 `xxx-xxx-xxx`
   - **AppKey**: 一串密钥

### 2. 配置application.properties

打开 `src/main/resources/application.properties`,修改以下配置:

```properties
# 将占位符替换为您真实的RTC配置
aliyun.rtc.app-id=YOUR_RTC_APP_ID
aliyun.rtc.app-key=YOUR_RTC_APP_KEY

# 数字人形象ID (从阿里云数字人控制台获取)
aliyun.avatar.avatar-id=2d_avatar_001
```

### 3. 获取数字人形象ID

1. 访问 [阿里云数字人控制台](https://avatar.console.aliyun.com/)
2. 进入 **形象管理**
3. 选择或创建一个2D数字人形象
4. 记录形象ID

### 4. 完善RTC集成代码

当前代码中RTC部分标记为TODO,需要您提供真实的RTC配置后完善:

**前端 (chat.html)**:
```javascript
// 在 connectAvatar() 函数中
// 使用阿里云RTC SDK加入频道
rtcClient = new AliyunRTCClient({
    appId: data.rtcConfig.appId,
    channel: data.rtcConfig.channelId,
    userId: data.rtcConfig.userId,
    token: data.rtcConfig.token
});

await rtcClient.join();
rtcClient.on('stream-added', (evt) => {
    const remoteStream = evt.stream;
    rtcClient.subscribe(remoteStream);
});

rtcClient.on('stream-subscribed', (evt) => {
    const remoteStream = evt.stream;
    remoteStream.play('avatarVideo');
});
```

**后端**:
需要实现:
1. TTS语音合成 (调用阿里云智能语音交互API)
2. WebSocket音频推送 (PCM格式,16kHz,单声道)
3. RTC Token生成的完善(使用官方SDK)

## 测试方法

1. 访问 http://localhost:8080
2. 登录 `hibaobao` / `hibaobao`
3. 选择一个好友
4. 点击聊天头部的 **"数字人"** 按钮
5. 看到数字人窗口弹出(当前显示配置提示)
6. 发送消息,查看控制台日志

## 文件清单

### 新增文件
- `src/main/java/com/virtualchat/service/AvatarService.java` - 数字人服务
- `src/main/java/com/virtualchat/controller/AvatarController.java` - 数字人API

### 修改文件
- `src/main/resources/application.properties` - 添加RTC配置
- `src/main/resources/templates/chat.html` - 添加数字人UI和功能
- `src/main/java/com/virtualchat/controller/MessageController.java` - 返回reply字段

## 技术栈

- **阿里云数字人**: avatar-dialog 模型
- **RTC**: 阿里云视频直播RTC
- **WebSocket**: DashScope API
- **前端**: HTML5 + JavaScript + Bootstrap 5

## 注意事项

1. **API Key安全**: 不要在代码中硬编码API Key,使用配置文件
2. **速率限制**: DashScope API有QPS限制,注意控制请求频率
3. **费用**: RTC和数字人API按使用量计费
4. **兼容性**: RTC SDK可能需要HTTPS环境,开发环境注意

## 下一步优化建议

1. 添加语音识别(ASR)支持语音输入
2. 优化TTS音色选择
3. 添加数字人形象切换功能
4. 实现打断功能(用户说话时停止数字人播报)
5. 添加表情和动作驱动

## 问题排查

### 数字人窗口不显示
- 检查浏览器控制台是否有JavaScript错误
- 确认Bootstrap Icons CDN加载成功

### RTC连接失败
- 检查app-id和app-key是否正确
- 确认RTC服务已开通
- 查看浏览器控制台网络请求

### WebSocket连接失败
- 检查DashScope API Key是否有效
- 确认网络连接正常
- 查看控制台日志

## 联系支持

如有问题,可访问:
- [阿里云数字人文档](https://help.aliyun.com/zh/avatar/)
- [RTC文档](https://help.aliyun.com/zh/live/)
- DashScope技术支持钉钉群: 147535001692
