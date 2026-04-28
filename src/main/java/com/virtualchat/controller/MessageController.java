package com.virtualchat.controller;

import com.virtualchat.model.Message;
import com.virtualchat.model.User;
import com.virtualchat.repository.MessageRepository;
import com.virtualchat.service.ChatService;
import com.virtualchat.service.ImageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        
        // 获取AI回复（启用上下文记忆）
        String aiResponse = chatService.getAIResponse(friendId, content, useContext);
        
        // 保存AI回复
        Message aiMessage = new Message();
        aiMessage.setFriendId(friendId);
        aiMessage.setSender("friend");
        aiMessage.setContent(aiResponse);
        aiMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(aiMessage);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userMessage", userMessage);
        response.put("aiMessage", aiMessage);
        response.put("reply", aiResponse); // 添加reply字段供数字人播报
        
        return ResponseEntity.ok(response);
    }
}