package com.virtualchat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.*;

/**
 * FileAgent集成服务
 * 用于通过对话调用fileAgent的文件处理功能
 */
@Service
public class FileAgentService {

    private static final Logger logger = LoggerFactory.getLogger(FileAgentService.class);

    @Value("${fileagent.service.url:http://localhost:8081}")
    private String fileAgentBaseUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FileAgentService() {
        this.webClient = WebClient.builder().build();
    }

    /**
     * 检查fileAgent服务是否可用
     */
    public boolean isServiceAvailable() {
        try {
            logger.info("检查fileAgent服务可用性: {}", fileAgentBaseUrl);
            // 简单测试连接
            String response = webClient.get()
                    .uri(fileAgentBaseUrl + "/api/chat/sessions")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            logger.info("fileAgent服务可用,响应: {}", response != null ? "成功" : "空");
            return true;
        } catch (Exception e) {
            logger.error("fileAgent服务不可用: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 调用fileAgent进行文件分析
     * 
     * @param message 用户消息
     * @param filePaths 文件路径列表
     * @param fileNames 文件名列表
     * @param sessionId 会话ID
     * @return AI回复
     */
    public String chatWithFiles(String message, List<String> filePaths, 
                                List<String> fileNames, String sessionId) {
        try {
            logger.info("调用fileAgent服务: {}", fileAgentBaseUrl);
            logger.info("文件路径: {}", filePaths);
            logger.info("用户消息: {}", message);
            
            Map<String, Object> request = new HashMap<>();
            request.put("message", message);
            request.put("filePaths", filePaths);
            request.put("fileNames", fileNames != null ? fileNames : new ArrayList<>());
            request.put("sessionId", sessionId != null ? sessionId : generateSessionId());

            String response = webClient.post()
                    .uri(fileAgentBaseUrl + "/api/chat")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("Accept", "text/plain;charset=UTF-8")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("fileAgent回复成功: {}", response != null ? response.substring(0, Math.min(100, response.length())) : "空");
            return response;

        } catch (Exception e) {
            logger.error("调用FileAgent失败: {}", e.getMessage(), e);
            return "抱歉,文件处理服务暂时不可用。请确保fileAgent服务已启动。\n错误详情: " + e.getMessage();
        }
    }

    /**
     * 流式调用fileAgent
     */
    public Flux<String> streamChatWithFiles(String message, List<String> filePaths,
                                            List<String> fileNames, String sessionId) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("message", message);
            request.put("filePaths", filePaths);
            request.put("fileNames", fileNames != null ? fileNames : new ArrayList<>());
            request.put("sessionId", sessionId != null ? sessionId : generateSessionId());

            return webClient.post()
                    .uri(fileAgentBaseUrl + "/api/chat/stream")
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToFlux(String.class);

        } catch (Exception e) {
            logger.error("流式调用FileAgent失败: {}", e.getMessage());
            return Flux.just("抱歉,文件处理服务暂时不可用。");
        }
    }

    /**
     * 智能判断是否需要调用fileAgent
     * 
     * @param message 用户消息
     * @return 是否应该调用fileAgent
     */
    public boolean shouldUseFileAgent(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        String lowerMsg = message.toLowerCase();

        // 检测是否包含文件路径（优先判断路径）
        List<String> paths = extractFilePaths(message);
        if (!paths.isEmpty()) {
            return true;
        }

        // 检测文件操作相关关键词
        String[] fileKeywords = {
            "文件", "文档", "pdf", "word", "excel", "ppt", "读取", "分析",
            "总结", "摘要", "提取", "搜索", "查找", "内容", "报告",
            "帮我看看", "这个文件", "这份文档", "打开", "查看", "读取"
        };

        for (String keyword : fileKeywords) {
            if (lowerMsg.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 从消息中提取文件路径
     * 
     * @param message 用户消息
     * @return 文件路径列表
     */
    public List<String> extractFilePaths(String message) {
        List<String> paths = new ArrayList<>();

        if (message == null) {
            return paths;
        }

        // 匹配Windows路径 (例如: D:\workspace\document.pdf 或 D:/workspace/document.pdf)
        // 改进：支持单反斜杠和双反斜杠
        java.util.regex.Pattern winPathPattern = java.util.regex.Pattern.compile(
            "[A-Za-z]:\\\\[^\\s\"'<>,|?*]+|[A-Za-z]:/[^\\s\"'<>,|?*]+"
        );
        java.util.regex.Matcher winMatcher = winPathPattern.matcher(message);
        while (winMatcher.find()) {
            String path = winMatcher.group();
            // 标准化路径分隔符为反斜杠
            if (path.contains("/")) {
                path = path.replace("/", "\\");
            }
            if (!paths.contains(path)) {
                paths.add(path);
            }
        }

        // 匹配Unix路径 (例如: /home/user/document.pdf)
        java.util.regex.Pattern unixPathPattern = java.util.regex.Pattern.compile(
            "/[^\\s\"'<>,|?*]+"
        );
        java.util.regex.Matcher unixMatcher = unixPathPattern.matcher(message);
        while (unixMatcher.find()) {
            String path = unixMatcher.group();
            if (!paths.contains(path)) {
                paths.add(path);
            }
        }

        return paths;
    }

    /**
     * 从文件路径中提取文件名
     */
    public List<String> extractFileNames(List<String> filePaths) {
        List<String> names = new ArrayList<>();
        for (String path : filePaths) {
            if (path.contains("\\")) {
                names.add(path.substring(path.lastIndexOf("\\") + 1));
            } else if (path.contains("/")) {
                names.add(path.substring(path.lastIndexOf("/") + 1));
            } else {
                names.add(path);
            }
        }
        return names;
    }

    /**
     * 生成会话ID
     */
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 构建fileAgent调用提示
     */
    public String buildFileAgentPrompt(String message, List<String> filePaths) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(" 检测到文件操作请求\n");
        prompt.append("正在调用文件处理服务...\n\n");
        
        if (filePaths != null && !filePaths.isEmpty()) {
            prompt.append("📄 文件列表:\n");
            for (int i = 0; i < filePaths.size(); i++) {
                prompt.append("  ").append(i + 1).append(". ").append(filePaths.get(i)).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("💬 用户问题: ").append(message).append("\n\n");
        prompt.append("⏳ 处理中...");

        return prompt.toString();
    }
}
