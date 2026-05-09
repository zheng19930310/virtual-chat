# -*- coding: utf-8 -*-
"""
生成CSDN文章Word文档
"""

from docx import Document
from docx.shared import Pt, RGBColor, Inches, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.style import WD_STYLE_TYPE
from docx.oxml.ns import qn
import os

def set_cell_border(cell, **kwargs):
    """设置单元格边框"""
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    
    tcBorders = tcPr.first_child_found_in("w:tcBorders")
    if tcBorders is None:
        tcBorders = parse_xml(r'<w:tcBorders %s/>' % nsdecls('w'))
        tcPr.append(tcBorders)
    
    for edge in ('top', 'left', 'bottom', 'right', 'insideH', 'insideV'):
        edge_data = kwargs.get(edge)
        if edge_data:
            tag = 'w:{}'.format(edge)
            element = tcBorders.find(qn(tag))
            if element is None:
                element = parse_xml(r'<w:{} {}/>'.format(edge, nsdecls('w')))
                tcBorders.append(element)
            
            for key in ["sz", "val", "color", "space"]:
                if key in edge_data:
                    element.set(qn('w:{}'.format(key)), str(edge_data[key]))

def add_image_placeholder(doc, placeholder_num, description, height=3.0):
    """添加图片占位符"""
    paragraph = doc.add_paragraph()
    paragraph.alignment = WD_ALIGN_PARAGRAPH.CENTER
    
    # 添加边框框
    run = paragraph.add_run()
    
    # 添加占位符文本
    placeholder_para = doc.add_paragraph()
    placeholder_para.alignment = WD_ALIGN_PARAGRAPH.CENTER
    
    run = placeholder_para.add_run(f'[图片占位符{placeholder_num}]')
    run.bold = True
    run.font.size = Pt(14)
    run.font.color.rgb = RGBColor(0x66, 0x66, 0x66)
    
    desc_para = doc.add_paragraph()
    desc_para.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = desc_para.add_run(f'📸 {description}')
    run.font.size = Pt(11)
    run.font.color.rgb = RGBColor(0x99, 0x99, 0x99)
    run.italic = True
    
    # 添加分隔线
    doc.add_paragraph('─' * 80).alignment = WD_ALIGN_PARAGRAPH.CENTER
    
    return placeholder_num + 1

def main():
    # 创建文档
    doc = Document()
    
    # 设置全局字体
    doc.styles['Normal'].font.name = '微软雅黑'
    doc.styles['Normal']._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    doc.styles['Normal'].font.size = Pt(11)
    
    # 标题
    title = doc.add_heading('基于Spring Boot + AI的虚拟社交聊天工具完整教程', 0)
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    
    # 作者信息
    info = doc.add_paragraph()
    info.alignment = WD_ALIGN_PARAGRAPH.CENTER
    info.add_run('作者: 您的名字  |  发布日期: 2026-04-28').font.size = Pt(10)
    info.add_run('\n技术栈: Spring Boot 3.2.0 + Spring AI + Thymeleaf + H2 Database').font.size = Pt(10)
    
    doc.add_paragraph()
    
    # 一、项目简介
    doc.add_heading('一、项目简介', 1)
    doc.add_paragraph('这是一个基于Spring Boot和AI技术的虚拟社交聊天工具,具有以下核心功能:')
    
    features = [
        ' 智能聊天: 集成通义千问大模型,实现智能对话',
        ' AI图片生成: 支持通义万相图片生成,为聊天增色',
        ' 好友管理: 模拟社交关系,创建虚拟好友',
        '💾 数据持久化: H2数据库保存聊天记录和用户信息',
        '🔐 用户认证: 完整的登录注册系统',
        '🤖 数字人集成: 阿里云数字人框架(待完善)'
    ]
    
    for feature in features:
        p = doc.add_paragraph(feature, style='List Bullet')
    
    doc.add_paragraph()
    p = doc.add_paragraph()
    p.add_run('在线体验: ').bold = True
    p.add_run('http://localhost:8080')
    
    p = doc.add_paragraph()
    p.add_run('项目地址: ').bold = True
    p.add_run('https://gitee.com/mobuhan/virtual-friend-chat')
    
    # 图片占位符1
    global_placeholder = 1
    global_placeholder = add_image_placeholder(doc, global_placeholder, 
        '项目整体效果图或登录页面截图')
    
    # 二、技术栈
    doc.add_heading('二、技术栈', 1)
    
    # 创建表格
    table = doc.add_table(rows=8, cols=3)
    table.style = 'Table Grid'
    
    # 表头
    headers = ['技术', '版本', '说明']
    for i, header in enumerate(headers):
        cell = table.rows[0].cells[i]
        cell.text = header
        cell.paragraphs[0].runs[0].bold = True
        cell.paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.CENTER
    
    # 表格数据
    tech_data = [
        ['Java', '17+', '开发语言'],
        ['Spring Boot', '3.2.0', 'Web框架'],
        ['Spring AI', '1.0.0-M4', 'AI集成'],
        ['Thymeleaf', '3.1.2', '模板引擎'],
        ['H2 Database', '2.2.224', '文件数据库'],
        ['Bootstrap', '5.3.0', 'UI框架'],
        ['DashScope API', '-', '阿里云AI服务']
    ]
    
    for row_idx, row_data in enumerate(tech_data, 1):
        for col_idx, cell_data in enumerate(row_data):
            table.rows[row_idx].cells[col_idx].text = cell_data
            table.rows[row_idx].cells[col_idx].paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.CENTER
    
    # 三、环境准备
    doc.add_heading('三、环境准备', 1)
    doc.add_heading('3.1 必需软件', 2)
    
    software_list = [
        'JDK 17或更高版本',
        'Maven 3.6+',
        'Git (用于代码管理)'
    ]
    
    for software in software_list:
        doc.add_paragraph(software, style='List Number')
    
    doc.add_heading('3.2 阿里云账号配置', 2)
    doc.add_paragraph('需要申请以下两个API Key:')
    
    doc.add_paragraph('1. DashScope API Key (通义千问/通义万相)', style='List Number')
    p = doc.add_paragraph()
    p.add_run('   访问: ').italic = True
    p.add_run('https://dashscope.console.aliyun.com/')
    p.add_run('\n   创建API Key')
    
    doc.add_paragraph('2. 阿里云AccessKey (可选,用于TTS等高级功能)', style='List Number')
    p = doc.add_paragraph()
    p.add_run('   访问: ').italic = True
    p.add_run('https://ram.console.aliyun.com/profile/access-keys')
    p.add_run('\n   创建AccessKey')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '在阿里云控制台创建API Key的截图')
    
    # 四、项目配置
    doc.add_heading('四、项目配置', 1)
    doc.add_heading('4.1 克隆项目', 2)
    
    p = doc.add_paragraph()
    p.add_run('git clone https://gitee.com/mobuhan/virtual-friend-chat.git').font.name = 'Consolas'
    p.add_run('\ncd virtual-friend-chat').font.name = 'Consolas'
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        'Git克隆项目的终端截图')
    
    doc.add_heading('4.2 配置API Key', 2)
    doc.add_paragraph('编辑 src/main/resources/application.properties 文件:')
    
    # 代码块
    code_para = doc.add_paragraph()
    code_para.add_run('# 通义千问API配置\n').font.name = 'Consolas'
    code_para.add_run('spring.ai.dashscope.api-key=您的DashScope_API_Key\n\n').font.name = 'Consolas'
    code_para.add_run('# 聊天模型配置\n').font.name = 'Consolas'
    code_para.add_run('spring.ai.dashscope.chat.options.model=qwen-turbo\n\n').font.name = 'Consolas'
    code_para.add_run('# 图片生成配置\n').font.name = 'Consolas'
    code_para.add_run('spring.ai.dashscope.image.options.model=wanx-v1\n').font.name = 'Consolas'
    code_para.add_run('image.generation.model=wanx-v1\n').font.name = 'Consolas'
    code_para.add_run('image.generation.size=1024*1024\n\n').font.name = 'Consolas'
    code_para.add_run('# 阿里云AccessKey (可选)\n').font.name = 'Consolas'
    code_para.add_run('aliyun.access-key-id=您的AccessKey_ID\n').font.name = 'Consolas'
    code_para.add_run('aliyun.access-key-secret=您的AccessKey_Secret').font.name = 'Consolas'
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '编辑器中打开application.properties文件的截图')
    
    # 五、项目启动
    doc.add_heading('五、项目启动', 1)
    doc.add_heading('5.1 方式一: 使用启动脚本 (推荐)', 2)
    
    p = doc.add_paragraph()
    p.add_run('Windows系统:').bold = True
    doc.add_paragraph('# 双击运行\nstart.bat')
    
    doc.add_paragraph('启动脚本会自动:')
    checklist = ['✅ 检测Java环境', '✅ 检测Maven环境', '✅ 编译并启动应用']
    for item in checklist:
        doc.add_paragraph(item, style='List Bullet')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '运行start.bat后的终端输出截图')
    
    doc.add_heading('5.2 方式二: 使用Maven命令', 2)
    
    code_para = doc.add_paragraph()
    code_para.add_run('# 进入项目目录\ncd virtual-friend-chat\n\n').font.name = 'Consolas'
    code_para.add_run('# 使用Maven启动\nmvn spring-boot:run\n\n').font.name = 'Consolas'
    code_para.add_run('# 或者先编译再运行\nmvn clean package\n').font.name = 'Consolas'
    code_para.add_run('java -jar target/virtual-chat-0.0.1-SNAPSHOT.jar').font.name = 'Consolas'
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        'mvn spring-boot:run命令执行过程截图')
    
    doc.add_heading('5.3 启动成功标志', 2)
    doc.add_paragraph('看到以下日志表示启动成功:')
    
    log_para = doc.add_paragraph()
    log_para.add_run('Tomcat started on port 8080 (http) with context path \'\'\n').font.name = 'Consolas'
    log_para.add_run('Started VirtualChatApplication in 10.711 seconds').font.name = 'Consolas'
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '完整的启动成功日志截图')
    
    doc.add_heading('5.4 访问应用', 2)
    p = doc.add_paragraph()
    p.add_run('浏览器打开: ').bold = True
    p.add_run('http://localhost:8080')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '应用启动后的登录页面截图')
    
    # 六、功能使用说明
    doc.add_heading('六、功能使用说明', 1)
    doc.add_heading('6.1 用户登录', 2)
    
    doc.add_paragraph('系统预置了测试账号:')
    p = doc.add_paragraph()
    p.add_run('用户名: ').bold = True
    p.add_run('admin\n')
    p.add_run('密码: ').bold = True
    p.add_run('admin123')
    
    doc.add_paragraph('或者直接注册新账号。')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '登录界面的完整截图')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '注册新账号的界面截图')
    
    doc.add_heading('6.2 聊天界面', 2)
    doc.add_paragraph('登录成功后进入聊天主界面:')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '聊天主界面的完整截图,包含左侧好友列表和右侧聊天区域')
    
    doc.add_heading('界面说明:', 3)
    interface_desc = [
        '左侧: 好友列表,显示所有虚拟好友',
        '右侧: 聊天窗口,显示消息记录',
        '顶部: 当前聊天对象名称',
        '底部: 消息输入框和发送按钮'
    ]
    for desc in interface_desc:
        doc.add_paragraph(desc, style='List Bullet')
    
    doc.add_heading('6.3 发送消息', 2)
    steps = [
        '在底部输入框输入消息',
        '点击"发送"按钮或按Enter键',
        'AI会自动回复'
    ]
    for i, step in enumerate(steps, 1):
        doc.add_paragraph(f'{i}. {step}')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '输入消息并发送的截图')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        'AI智能回复的消息截图')
    
    doc.add_heading('6.4 AI图片生成', 2)
    doc.add_paragraph('聊天过程中,AI可能会自动生成图片,您也可以主动请求:')
    
    p = doc.add_paragraph()
    p.add_run('示例对话:').bold = True
    doc.add_paragraph('用户: 给我画一只可爱的小猫')
    doc.add_paragraph('AI: [生成图片] 🐱 这是一只可爱的小猫...')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        'AI生成图片的聊天截图,展示图片效果')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        'AI生成的高清图片大图')
    
    doc.add_heading('6.5 添加好友', 2)
    steps = [
        '点击顶部"添加好友"按钮',
        '输入好友昵称和描述',
        '点击"保存"'
    ]
    for i, step in enumerate(steps, 1):
        doc.add_paragraph(f'{i}. {step}')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '添加好友的弹窗或界面截图')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '添加好友后的好友列表截图')
    
    doc.add_heading('6.6 切换好友', 2)
    doc.add_paragraph('点击左侧好友列表中的不同好友,切换到对应的聊天窗口。')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '点击不同好友切换聊天的截图')
    
    doc.add_heading('6.7 查看聊天记录', 2)
    doc.add_paragraph('所有聊天记录自动保存,切换好友后可以查看历史消息。')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '查看历史聊天记录的截图')
    
    # 七、项目结构
    doc.add_heading('七、项目结构', 1)
    
    tree_para = doc.add_paragraph()
    tree_para.add_run('virtual-chat/\n').font.name = 'Consolas'
    tree_para.add_run('├── src/\n').font.name = 'Consolas'
    tree_para.add_run('│   ├── main/\n').font.name = 'Consolas'
    tree_para.add_run('│   │   ├── java/\n').font.name = 'Consolas'
    tree_para.add_run('│   │   │   └── com/virtualchat/\n').font.name = 'Consolas'
    tree_para.add_run('│   │   │       ├── controller/        # 控制器层\n').font.name = 'Consolas'
    tree_para.add_run('│   │   │       ├── model/             # 数据模型\n').font.name = 'Consolas'
    tree_para.add_run('│   │   │       ├── repository/        # 数据访问层\n').font.name = 'Consolas'
    tree_para.add_run('│   │   │       ├── service/           # 业务逻辑层\n').font.name = 'Consolas'
    tree_para.add_run('│   │   │       └── config/            # 配置类\n').font.name = 'Consolas'
    tree_para.add_run('│   │   └── resources/\n').font.name = 'Consolas'
    tree_para.add_run('│   │       ├── templates/             # 前端页面\n').font.name = 'Consolas'
    tree_para.add_run('│   │       └── application.properties # 配置文件\n').font.name = 'Consolas'
    tree_para.add_run('│   └── test/                          # 测试代码\n').font.name = 'Consolas'
    tree_para.add_run('├── data/                              # H2数据库文件\n').font.name = 'Consolas'
    tree_para.add_run('├── generated-images/                  # AI生成的图片\n').font.name = 'Consolas'
    tree_para.add_run('├── pom.xml                            # Maven配置\n').font.name = 'Consolas'
    tree_para.add_run('├── start.bat                          # 启动脚本\n').font.name = 'Consolas'
    tree_para.add_run('└── README.md                          # 项目说明').font.name = 'Consolas'
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        'IDE中显示的项目目录结构截图')
    
    # 八、核心功能实现
    doc.add_heading('八、核心功能实现', 1)
    doc.add_heading('8.1 AI聊天功能', 2)
    doc.add_paragraph('使用Spring AI集成通义千问大模型:')
    
    code_para = doc.add_paragraph()
    code_para.add_run('@Service\npublic class ChatService {\n    \n').font.name = 'Consolas'
    code_para.add_run('    @Autowired\n    private ChatClient chatClient;\n    \n').font.name = 'Consolas'
    code_para.add_run('    public String chat(String message) {\n').font.name = 'Consolas'
    code_para.add_run('        return chatClient.call(message);\n').font.name = 'Consolas'
    code_para.add_run('    }\n}').font.name = 'Consolas'
    
    doc.add_heading('8.2 图片生成功能', 2)
    doc.add_paragraph('使用DashScope API异步生成图片:')
    
    code_para = doc.add_paragraph()
    code_para.add_run('@Service\npublic class ImageService {\n    \n').font.name = 'Consolas'
    code_para.add_run('    public String generateImage(String prompt) {\n').font.name = 'Consolas'
    code_para.add_run('        // 1. 提交图片生成任务\n').font.name = 'Consolas'
    code_para.add_run('        String taskId = submitImageTask(prompt);\n        \n').font.name = 'Consolas'
    code_para.add_run('        // 2. 轮询任务状态\n').font.name = 'Consolas'
    code_para.add_run('        String imageUrl = waitForTaskCompletion(taskId);\n        \n').font.name = 'Consolas'
    code_para.add_run('        // 3. 下载图片到本地\n').font.name = 'Consolas'
    code_para.add_run('        return downloadImage(imageUrl);\n').font.name = 'Consolas'
    code_para.add_run('    }\n}').font.name = 'Consolas'
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        'ImageService.java核心代码截图')
    
    doc.add_heading('8.3 数据持久化', 2)
    doc.add_paragraph('使用Spring Data JPA操作H2数据库:')
    
    code_para = doc.add_paragraph()
    code_para.add_run('@Entity\npublic class Message {\n    @Id\n').font.name = 'Consolas'
    code_para.add_run('    @GeneratedValue(strategy = GenerationType.IDENTITY)\n').font.name = 'Consolas'
    code_para.add_run('    private Long id;\n    \n').font.name = 'Consolas'
    code_para.add_run('    private String content;\n').font.name = 'Consolas'
    code_para.add_run('    private String sender;\n').font.name = 'Consolas'
    code_para.add_run('    private LocalDateTime createdAt;\n    \n').font.name = 'Consolas'
    code_para.add_run('    @ManyToOne\n').font.name = 'Consolas'
    code_para.add_run('    private Conversation conversation;\n}').font.name = 'Consolas'
    
    # 九、常见问题
    doc.add_heading('九、常见问题', 1)
    
    doc.add_heading('Q1: 启动时报"端口8080已被占用"?', 2)
    doc.add_paragraph('解决方案:')
    
    code_para = doc.add_paragraph()
    code_para.add_run('# Windows查找占用端口的进程\n').font.name = 'Consolas'
    code_para.add_run('netstat -ano | findstr :8080\n\n').font.name = 'Consolas'
    code_para.add_run('# 终止进程 (替换PID)\n').font.name = 'Consolas'
    code_para.add_run('taskkill /F /PID <进程ID>').font.name = 'Consolas'
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        'netstat命令查询端口占用的截图')
    
    doc.add_heading('Q2: 图片生成失败?', 2)
    doc.add_paragraph('检查项:')
    
    checks = ['✅ DashScope API Key是否正确', '✅ 网络连接是否正常', '✅ 查看控制台日志是否有错误信息']
    for check in checks:
        doc.add_paragraph(check, style='List Bullet')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '图片生成失败时的错误日志截图')
    
    doc.add_heading('Q3: 数据库文件损坏?', 2)
    doc.add_paragraph('解决方案:')
    doc.add_paragraph('# 删除数据库文件,系统会自动重建\nrm -rf data/virtualchat.mv.db')
    
    doc.add_heading('Q4: Maven依赖下载失败?', 2)
    doc.add_paragraph('解决方案:')
    
    code_para = doc.add_paragraph()
    code_para.add_run('# 清理Maven缓存\nmvn clean\n\n').font.name = 'Consolas'
    code_para.add_run('# 重新下载依赖\nmvn dependency:resolve').font.name = 'Consolas'
    
    # 十、进阶功能
    doc.add_heading('十、进阶功能', 1)
    doc.add_heading('10.1 自定义AI角色', 2)
    doc.add_paragraph('编辑 application.properties:')
    
    code_para = doc.add_paragraph()
    code_para.add_run('# 修改AI模型\n').font.name = 'Consolas'
    code_para.add_run('spring.ai.dashscope.chat.options.model=qwen-plus\n\n').font.name = 'Consolas'
    code_para.add_run('# 修改系统提示词(在代码中配置)').font.name = 'Consolas'
    
    doc.add_heading('10.2 自定义好友形象', 2)
    doc.add_paragraph('修改 chat.html 中的CSS样式:')
    
    code_para = doc.add_paragraph()
    code_para.add_run('.friend-avatar {\n').font.name = 'Consolas'
    code_para.add_run('    background-image: url(\'/images/custom-avatar.jpg\');\n').font.name = 'Consolas'
    code_para.add_run('}').font.name = 'Consolas'
    
    doc.add_heading('10.3 数字人集成 (待完善)', 2)
    doc.add_paragraph('需要先购买阿里云数字人服务,获取实例ID后配置:')
    
    code_para = doc.add_paragraph()
    code_para.add_run('aliyun.avatar.project-id=您的项目ID\n').font.name = 'Consolas'
    code_para.add_run('aliyun.avatar.instance-id=您的实例ID').font.name = 'Consolas'
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '阿里云数字人控制台的截图')
    
    # 十一、项目亮点
    doc.add_heading('十一、项目亮点', 1)
    
    highlights = [
        '🏗️ 完整的全栈架构: 后端Spring Boot + 前端Thymeleaf + Bootstrap',
        '🤖 AI深度集成: 聊天、图片生成均使用AI能力',
        '💾 轻量级部署: H2文件数据库,无需额外安装数据库',
        '🔐 安全认证: Spring Security保护用户数据',
        '📦 开箱即用: 提供启动脚本,一键运行',
        '🎨 美观界面: 现代化UI设计,用户体验良好'
    ]
    
    for highlight in highlights:
        doc.add_paragraph(highlight, style='List Bullet')
    
    # 十二、技术难点与解决方案
    doc.add_heading('十二、技术难点与解决方案', 1)
    
    doc.add_heading('难点1: DashScope图片生成API是异步的', 2)
    doc.add_paragraph('问题: API返回PENDING状态,不能立即获取图片URL')
    doc.add_paragraph('解决方案:')
    solutions = [
        '实现任务提交和状态轮询机制',
        '最多轮询20次,每次间隔2秒',
        '当状态变为SUCCEEDED时提取URL'
    ]
    for i, solution in enumerate(solutions, 1):
        doc.add_paragraph(f'{i}. {solution}')
    
    doc.add_heading('难点2: H2数据库文件锁定', 2)
    doc.add_paragraph('问题: 应用异常退出后,数据库文件被锁定无法再次启动')
    doc.add_paragraph('解决方案:')
    doc.add_paragraph('终止残留的Java进程或使用H2服务器模式', style='List Bullet')
    
    doc.add_heading('难点3: 环境变量配置', 2)
    doc.add_paragraph('问题: 不同环境下JDK和Maven路径不同')
    doc.add_paragraph('解决方案:')
    solutions = [
        '提供多种启动方式',
        '使用IDEA内置的JDK和Maven',
        '编写兼容不同环境的启动脚本'
    ]
    for i, solution in enumerate(solutions, 1):
        doc.add_paragraph(f'{i}. {solution}')
    
    # 十三、后续规划
    doc.add_heading('十三、后续规划', 1)
    
    plans = [
        '完善阿里云数字人视频流集成',
        '添加TTS语音播报功能',
        '实现消息加密传输',
        '添加更多AI模型支持',
        '优化移动端适配',
        '添加文件上传功能',
        '实现群聊功能'
    ]
    
    for plan in plans:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run('☐ ').font.name = 'Wingdings'
        p.add_run(plan)
    
    # 十四、总结
    doc.add_heading('十四、总结', 1)
    doc.add_paragraph('本项目是一个功能完整的AI虚拟社交聊天工具,集成了通义千问大模型和通义万相图片生成能力。项目采用Spring Boot框架,结构清晰,易于理解和二次开发。')
    
    doc.add_heading('适合人群:', 2)
    audiences = [
        '‍💻 学习Spring Boot的初学者',
        '🤖 对AI应用开发感兴趣的开发者',
        '💬 想构建聊天应用的团队',
        '🎓 作为毕业设计或课程项目'
    ]
    for audience in audiences:
        doc.add_paragraph(audience, style='List Bullet')
    
    doc.add_heading('学习价值:', 2)
    values = [
        'Spring Boot Web开发',
        'Spring AI集成',
        '异步任务处理',
        '数据库设计与操作',
        '前端模板引擎使用'
    ]
    for value in values:
        doc.add_paragraph(value, style='List Bullet')
    
    # 十五、参考资源
    doc.add_heading('十五、参考资源', 1)
    
    resources = [
        ('Spring Boot官方文档', 'https://spring.io/projects/spring-boot'),
        ('Spring AI文档', 'https://spring.io/projects/spring-ai'),
        ('阿里云DashScope文档', 'https://help.aliyun.com/zh/dashscope/'),
        ('Thymeleaf官方文档', 'https://www.thymeleaf.org/'),
        ('H2 Database文档', 'https://www.h2database.com/')
    ]
    
    for name, url in resources:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(f'{name}: ').bold = True
        run = p.add_run(url)
        run.font.color.rgb = RGBColor(0x00, 0x70, 0xC0)
    
    # 附录
    doc.add_heading('附录: 完整配置文件', 1)
    doc.add_paragraph('application.properties 完整内容:')
    
    code_para = doc.add_paragraph()
    code_para.add_run('# 服务器配置\n').font.name = 'Consolas'
    code_para.add_run('server.port=8080\n\n').font.name = 'Consolas'
    code_para.add_run('# H2数据库配置\n').font.name = 'Consolas'
    code_para.add_run('spring.datasource.url=jdbc:h2:file:./data/virtualchat\n').font.name = 'Consolas'
    code_para.add_run('spring.datasource.driver-class-name=org.h2.Driver\n').font.name = 'Consolas'
    code_para.add_run('spring.datasource.username=sa\n').font.name = 'Consolas'
    code_para.add_run('spring.datasource.password=\n').font.name = 'Consolas'
    code_para.add_run('spring.jpa.database-platform=org.hibernate.dialect.H2Dialect\n').font.name = 'Consolas'
    code_para.add_run('spring.jpa.hibernate.ddl-auto=update\n').font.name = 'Consolas'
    code_para.add_run('spring.h2.console.enabled=true\n').font.name = 'Consolas'
    code_para.add_run('spring.h2.console.path=/h2-console\n\n').font.name = 'Consolas'
    code_para.add_run('# 通义千问API配置\n').font.name = 'Consolas'
    code_para.add_run('spring.ai.dashscope.api-key=您的API_Key\n').font.name = 'Consolas'
    code_para.add_run('spring.ai.dashscope.chat.options.model=qwen-turbo\n\n').font.name = 'Consolas'
    code_para.add_run('# 图片生成配置\n').font.name = 'Consolas'
    code_para.add_run('spring.ai.dashscope.image.options.model=wanx-v1\n').font.name = 'Consolas'
    code_para.add_run('image.generation.model=wanx-v1\n').font.name = 'Consolas'
    code_para.add_run('image.generation.size=1024*1024\n\n').font.name = 'Consolas'
    code_para.add_run('# 静态资源配置\n').font.name = 'Consolas'
    code_para.add_run('spring.web.resources.static-locations=classpath:/static/,file:./generated-images/\n').font.name = 'Consolas'
    code_para.add_run('spring.mvc.static-path-pattern=/images/**').font.name = 'Consolas'
    
    # 结尾
    doc.add_paragraph()
    doc.add_paragraph('─' * 80).alignment = WD_ALIGN_PARAGRAPH.CENTER
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        '包含登录、聊天、图片生成等多个功能的拼贴图')
    
    global_placeholder = add_image_placeholder(doc, global_placeholder,
        'Gitee仓库主页的截图')
    
    # 写在最后
    doc.add_heading('写在最后', 1)
    doc.add_paragraph('如果您觉得这个项目对您有帮助,欢迎:')
    
    actions = [
        '⭐ 给项目点个Star',
        '🍴 Fork项目二次开发',
        '🐛 提交Issue反馈问题',
        '💬 在评论区交流心得'
    ]
    
    for action in actions:
        doc.add_paragraph(action, style='List Bullet')
    
    p = doc.add_paragraph()
    p.add_run('项目地址: ').bold = True
    p.add_run('https://gitee.com/mobuhan/virtual-friend-chat')
    
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run('感谢您的阅读! 🎉').bold = True
    p.add_run('\n').font.size = Pt(14)
    
    doc.add_paragraph()
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run('声明: 本文仅供学习参考,AI服务需要消耗API额度,请合理使用。').italic = True
    p.runs[0].font.color.rgb = RGBColor(0x99, 0x99, 0x99)
    
    # 保存文档
    output_file = 'CSDN文章-虚拟社交聊天工具完整教程.docx'
    doc.save(output_file)
    
    print(f'✅ Word文档已生成: {output_file}')
    print(f'📊 共包含 {global_placeholder - 1} 个图片占位符')
    print(f'\n请打开Word文档,将占位符替换为实际截图')

if __name__ == '__main__':
    main()
