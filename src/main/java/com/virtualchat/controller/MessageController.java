package com.virtualchat.controller;

import com.virtualchat.model.Message;
import com.virtualchat.model.User;
import com.virtualchat.repository.MessageRepository;
import com.virtualchat.service.ChatService;
import com.virtualchat.service.ImageService;
import com.virtualchat.service.FileAgentService;
import com.virtualchat.service.StreamingChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private FileAgentService fileAgentService;
    
    @Autowired
    private StreamingChatService streamingChatService;

    // 获取与指定好友的聊天记录
    @GetMapping("/{friendId}")
    public ResponseEntity<?> getMessages(@PathVariable Long friendId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        
        List<Message> messages = messageRepository.findByFriendIdOrderByTimestampAsc(friendId);
        return ResponseEntity.ok(messages);
    }

    // 发送消息并获取AI回复
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> request, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        
        Long friendId = Long.valueOf(request.get("friendId").toString());
        String content = (String) request.get("content");
        Boolean useContext = (Boolean) request.getOrDefault("useContext", true); // 默认使用上下文
        String thinkingChainMode = (String) request.getOrDefault("thinkingChainMode", "NORMAL"); // 默认使用常规模式
        
        // 保存用户消息
        Message userMessage = new Message();
        userMessage.setFriendId(friendId);
        userMessage.setSender("user");
        userMessage.setContent(content);
        userMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(userMessage);
        
        // 检查是否是图片生成请求
        String sceneDescription = imageService.extractSceneDescription(content);
        if (sceneDescription != null) {
            // 生成图片
            List<String> imageUrls = imageService.generateSceneImages(sceneDescription);
            
            // 保存图片消息
            Message imageMessage = new Message();
            imageMessage.setFriendId(friendId);
            imageMessage.setSender("friend");
            imageMessage.setContent("为你生成了4张图片~");
            imageMessage.setImageUrls(String.join(",", imageUrls));
            imageMessage.setMessageType("image");
            imageMessage.setTimestamp(LocalDateTime.now());
            messageRepository.save(imageMessage);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userMessage", userMessage);
            response.put("aiMessage", imageMessage);
            
            return ResponseEntity.ok(response);
        }
        
        // 检查是否需要调用fileAgent处理文件
        if (fileAgentService.shouldUseFileAgent(content)) {
            List<String> filePaths = fileAgentService.extractFilePaths(content);
            
            if (!filePaths.isEmpty() && fileAgentService.isServiceAvailable()) {
                // 构建提示消息
                String promptMessage = fileAgentService.buildFileAgentPrompt(content, filePaths);
                
                // 保存提示消息
                Message promptMsg = new Message();
                promptMsg.setFriendId(friendId);
                promptMsg.setSender("friend");
                promptMsg.setContent(promptMessage);
                promptMsg.setTimestamp(LocalDateTime.now());
                messageRepository.save(promptMsg);
                
                // 调用fileAgent处理文件
                List<String> fileNames = fileAgentService.extractFileNames(filePaths);
                String fileAgentResponse = fileAgentService.chatWithFiles(
                    content, 
                    filePaths, 
                    fileNames, 
                    "virtual_chat_" + friendId
                );
                
                // 保存fileAgent回复
                Message fileAgentMessage = new Message();
                fileAgentMessage.setFriendId(friendId);
                fileAgentMessage.setSender("friend");
                fileAgentMessage.setContent(fileAgentResponse);
                fileAgentMessage.setMessageType("file_analysis");
                fileAgentMessage.setTimestamp(LocalDateTime.now());
                messageRepository.save(fileAgentMessage);
                
                Map<String, Object> response = new HashMap<>();
                response.put("userMessage", userMessage);
                response.put("promptMessage", promptMsg);
                response.put("aiMessage", fileAgentMessage);
                response.put("reply", fileAgentResponse);
                
                return ResponseEntity.ok(response);
            }
        }
        
        // 获取AI回复（启用上下文记忆和思维链模式）
        String aiResponse = chatService.getAIResponse(friendId, content, useContext.booleanValue(), thinkingChainMode);
        
        // 保存AI回复
        Message aiMessage = new Message();
        aiMessage.setFriendId(friendId);
        aiMessage.setSender("friend");
        aiMessage.setContent(aiResponse);
        
        // 如果有思维链内容，也保存
        if (request.containsKey("thinkingContent")) {
            aiMessage.setThinkingContent((String) request.get("thinkingContent"));
        }
        
        aiMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(aiMessage);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userMessage", userMessage);
        response.put("aiMessage", aiMessage);
        response.put("reply", aiResponse); // 添加reply字段供数字人播报
        
        return ResponseEntity.ok(response);
    }
    
    // 保存消息（用于前端直接保存已生成的内容）
    @PostMapping("/save")
    public ResponseEntity<?> saveMessage(@RequestBody Map<String, Object> request, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        
        Long friendId = Long.valueOf(request.get("friendId").toString());
        String content = (String) request.get("content");
        String thinkingContent = (String) request.get("thinkingContent");
        
        // 保存AI回复
        Message aiMessage = new Message();
        aiMessage.setFriendId(friendId);
        aiMessage.setSender("friend");
        aiMessage.setContent(content);
        
        // 如果有思维链内容，也保存
        if (thinkingContent != null && !thinkingContent.isEmpty()) {
            aiMessage.setThinkingContent(thinkingContent);
        }
        
        aiMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(aiMessage);
        
        return ResponseEntity.ok(Map.of("success", true, "messageId", aiMessage.getId()));
    }
    
    // 流式发送消息并获取AI回复（支持思维链）
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sendMessageStream(@RequestBody Map<String, Object> request, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return Flux.just("data: {\"error\": \"未登录\"}\n\n");
        }
        
        Long friendId = Long.valueOf(request.get("friendId").toString());
        String content = (String) request.get("content");
        Boolean useContext = (Boolean) request.getOrDefault("useContext", true);
        String thinkingChainMode = (String) request.getOrDefault("thinkingChainMode", "NORMAL");
        
        // 保存用户消息
        Message userMessage = new Message();
        userMessage.setFriendId(friendId);
        userMessage.setSender("user");
        userMessage.setContent(content);
        userMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(userMessage);
        
        // 如果是常规模式，使用普通流式输出
        if ("NORMAL".equals(thinkingChainMode)) {
            return streamingChatService.getStreamingAIResponse(friendId, content, useContext, thinkingChainMode)
                .doOnComplete(() -> {
                    // 完成后保存AI回复
                    // 这里需要收集完整的回复，暂时省略
                });
        } else {
            // 思维链模式，使用带标记的流式输出
            return streamingChatService.getStreamingAIResponse(friendId, content, useContext, thinkingChainMode);
        }
    }
}