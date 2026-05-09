# 虚拟聊天 - FileAgent集成功能说明

## 功能概述

虚拟聊天工具现已集成**FileAgent**文件处理功能,可以通过对话直接分析和处理文件。

---

## 使用方法

### 1. 启动fileAgent服务

首先需要启动fileAgent服务(默认端口8081):

```bash
cd d:\aiwork\fileAgent
start.bat
```

或者直接运行:
```bash
mvn spring-boot:run
```

### 2. 启动virtual-chat服务

```bash
cd d:\aiwork\virtual-chat
start.bat
```

### 3. 使用文件分析功能

在聊天窗口中,直接输入包含文件路径的消息即可:

#### 示例对话:

**示例1: 读取PDF文件**
```
用户: 帮我读取这个文件 D:\workspace\report.pdf 的内容

AI: 📄 检测到文件操作请求
    正在调用文件处理服务...
    
    📄 文件列表:
      1. D:\workspace\report.pdf
    
    💬 用户问题: 帮我读取这个文件 D:\workspace\report.pdf 的内容
    
     处理中...

[fileAgent回复文件内容摘要]
```

**示例2: 分析Word文档**
```
用户: 总结一下这份文档 C:\temp\meeting_notes.docx

AI: [自动调用fileAgent分析文档并返回摘要]
```

**示例3: 搜索Excel内容**
```
用户: 在 D:\data\sales.xlsx 中查找销售额超过10000的记录

AI: [调用fileAgent搜索并返回结果]
```

---

## 支持的文件类型

✅ **已支持的文件格式**:

- 📄 **PDF**: .pdf
- 📝 **Word**: .doc, .docx
- 📊 **Excel**: .xls, .xlsx
- 📽️ **PowerPoint**: .ppt, .pptx
- 📃 **文本文件**: .txt, .md, .csv

---

## 功能特性

### 1. 自动识别文件操作

系统会自动检测以下关键词,判断是否需要调用fileAgent:

- 文件、文档、pdf、word、excel、ppt
- 读取、分析、总结、摘要、提取
- 搜索、查找、内容、报告
- 帮我看看、这个文件、这份文档

### 2. 智能路径提取

支持自动从消息中提取文件路径:

**Windows路径**:
- `D:\workspace\document.pdf`
- `C:\Users\username\file.docx`

**Unix路径**:
- `/home/user/document.pdf`
- `/tmp/file.txt`

### 3. 多种分析能力

- 📖 **内容读取**: 读取文件完整内容
-  **文档总结**: 生成文档摘要
- 🔍 **内容搜索**: 在文件中搜索特定信息
- 📊 **数据分析**: 分析表格数据
-  **问题回答**: 基于文件内容回答问题

---

## 配置说明

### virtual-chat配置

在 `application.properties` 中配置fileAgent服务地址:

```properties
# FileAgent服务配置
fileagent.service.url=http://localhost:8081
```

### fileAgent配置

在 `application.yml` 中配置允许访问的目录:

```yaml
file-agent:
  allowed-base-paths:
    - D:/workspace
    - C:/temp
    - ./data
```

⚠️ **安全提示**: 为了安全,fileAgent只允许访问配置的目录。

---

## 故障排查

### 问题1: 提示"文件处理服务暂时不可用"

**原因**: fileAgent服务未启动或端口不正确

**解决方案**:
1. 检查fileAgent是否运行: 访问 http://localhost:8081
2. 修改 `application.properties` 中的 `fileagent.service.url`
3. 确保fileAgent服务端口正确

### 问题2: 文件路径无法识别

**原因**: 路径格式不正确或包含空格

**解决方案**:
1. 使用完整路径,例如: `D:\workspace\file.pdf`
2. 如果路径包含空格,用引号包裹: `"D:\my documents\file.pdf"`
3. 检查文件是否存在

### 问题3: 提示"不允许访问该路径"

**原因**: 文件路径不在fileAgent允许的基础目录中

**解决方案**:
1. 修改fileAgent的 `application.yml`
2. 添加文件所在的基础目录到 `allowed-base-paths`
3. 重启fileAgent服务

### 问题4: 文件读取失败

**原因**: 文件格式不支持或文件损坏

**解决方案**:
1. 确认文件格式在支持列表中
2. 检查文件是否损坏
3. 查看fileAgent服务日志获取详细错误

---

## 进阶用法

### 1. 多文件分析

可以同时分析多个文件:

```
用户: 对比这两个文件 D:\workspace\v1.pdf 和 D:\workspace\v2.pdf 的区别
```

### 2. 结合AI对话

fileAgent返回结果后,可以继续与AI对话:

```
用户: 帮我读取 D:\report.pdf

AI: [返回文件内容]

用户: 根据这个报告,总结一下主要结论

AI: [基于文件内容继续对话]
```

### 3. 流式输出 (待实现)

未来版本将支持流式输出,实时显示文件分析过程。

---

## 技术架构

```
┌─────────────┐
│  用户输入   │
└──────┬──────┘
       │
       ▼
┌─────────────────────┐
│  检测关键词和路径   │
│  (FileAgentService) │
└──────┬──────────────┘
       │ 需要文件处理?
       ├──────────────┐
       │ Yes          │ No
       ▼              ▼
┌──────────────┐  ┌──────────┐
│ 调用fileAgent│  │ 普通AI   │
│ 服务         │  │ 对话     │
└──────┬───────┘  └──────────┘
       │
       ▼
┌──────────────┐
│  返回分析结果 │
│  并展示      │
└──────────────
```

---

## 示例场景

### 场景1: 工作报告分析

```
用户: 读取 D:\workspace\monthly_report.pdf 并总结关键数据

AI: 📄 正在分析月度报告...

    关键数据总结:
    - 本月销售额: ¥1,234,567
    - 环比增长: 15.3%
    - 客户满意度: 92%
    - 新产品上线: 3个
    
    建议: 下月重点关注...
```

### 场景2: 会议记录整理

```
用户: 总结 C:\temp\meeting_20260428.docx 中的行动项

AI: 📄 分析会议记录...

    行动项列表:
    1. [张三] 完成技术方案评审 - 截止: 2026-05-05
    2. [李四] 准备演示文稿 - 截止: 2026-05-03
    3. [王五] 更新项目进度 - 截止: 2026-05-01
```

### 场景3: 数据分析

```
用户: 在 D:\data\sales_q1.xlsx 中找出销售冠军

AI: 📊 分析销售数据...

    Q1销售冠军: 赵六
    - 总销售额: ¥456,789
    - 成交客户数: 28
    - 平均客单价: ¥16,314
```

---

## 注意事项

1. ⚠️ **文件安全**: 只分析授权目录中的文件
2. 📁 **文件大小**: 建议文件不超过50MB
3. 🔐 **隐私保护**: 敏感文件请在本地处理
4. 🌐 **网络依赖**: 需要fileAgent服务运行
5. 💾 **临时文件**: 分析过程中可能生成临时文件

---

## 未来规划

- [ ] 支持图片OCR识别
- [ ] 支持音频文件转录
- [ ] 支持视频文件分析
- [ ] 流式实时输出
- [ ] 批量文件处理
- [ ] 文件对比功能
- [ ] 导出分析报告

---

## 相关链接

- **项目地址**: https://gitee.com/mobuhan/virtual-friend-chat
- **FileAgent项目**: https://gitee.com/mobuhan/file-agent
- **问题反馈**: 提交Issue或联系开发者

---

**祝您使用愉快! 🎉**
