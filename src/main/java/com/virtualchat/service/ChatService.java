package com.virtualchat.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualchat.model.Conversation;
import com.virtualchat.model.Friend;
import com.virtualchat.repository.ConversationRepository;
import com.virtualchat.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    private DashScopeChatModel chatModel;
    
    @Autowired
    private FriendRepository friendRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取AI回复（支持连续对话）
     */
    public String getAIResponse(Long friendId, String userMessage, boolean useContext) {
        // 获取好友信息
        Friend friend = friendRepository.findById(friendId)
            .orElseThrow(() -> new RuntimeException("好友不存在"));
        
        // 构建系统提示词，包含好友的性格和特征
        String systemPrompt = String.format(
            "你是一个虚拟朋友，名叫%s，%d岁，性别%s。" +
            "你的性格特点：%s。" +
            "你的主要特征：%s。" +
            "请以这个角色的身份与用户进行自然、友好的对话。" +
            "回复要简洁、亲切，符合角色设定。",
            friend.getName(),
            friend.getAge() != null ? friend.getAge() : 20,
            friend.getGender() != null ? friend.getGender() : "未知",
            friend.getPersonality() != null ? friend.getPersonality() : "友好、开朗",
            friend.getCharacteristics() != null ? friend.getCharacteristics() : "一个普通的虚拟朋友"
        );
        
        String fullPrompt;
        
        if (useContext) {
            // 连续对话模式：携带上下文
            fullPrompt = buildContextualPrompt(friendId, systemPrompt, userMessage);
        } else {
            // 单条交流模式：不携带上下文
            fullPrompt = systemPrompt + "\n用户: " + userMessage;
        }
        
        // 调用千问大模型生成回复
        try {
            String response = chatModel.call(fullPrompt);
            
            // 如果是连续对话模式，保存对话到上下文
            if (useContext) {
                saveToContext(friendId, userMessage, response);
            }
            
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，我现在有点忙，稍后再聊吧~";
        }
    }
    
    /**
     * 构建带上下文的提示词
     */
    private String buildContextualPrompt(Long friendId, String systemPrompt, String userMessage) {
        StringBuilder prompt = new StringBuilder(systemPrompt);
        prompt.append("\n\n以下是之前的对话历史：\n");
        
        // 获取对话上下文
        Conversation conversation = conversationRepository.findByFriendId(friendId).orElse(null);
        
        if (conversation != null && conversation.getContextData() != null) {
            try {
                List<Map<String, String>> history = objectMapper.readValue(
                    conversation.getContextData(), 
                    new TypeReference<List<Map<String, String>>>() {}
                );
                
                // 只取最近5轮对话
                int start = Math.max(0, history.size() - 5);
                for (int i = start; i < history.size(); i++) {
                    Map<String, String> msg = history.get(i);
                    prompt.append(msg.get("role")).append(": ").append(msg.get("content")).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        prompt.append("\n用户: ").append(userMessage);
        return prompt.toString();
    }
    
    /**
     * 保存对话到上下文
     */
    private void saveToContext(Long friendId, String userMessage, String aiResponse) {
        try {
            Conversation conversation = conversationRepository.findByFriendId(friendId).orElse(null);
            
            List<Map<String, String>> history;
            
            if (conversation == null) {
                history = new ArrayList<>();
                conversation = new Conversation();
                conversation.setFriendId(friendId);
                conversation.setIsActive(true);
            } else {
                // 解析现有历史
                if (conversation.getContextData() != null) {
                    history = objectMapper.readValue(
                        conversation.getContextData(), 
                        new TypeReference<List<Map<String, String>>>() {}
                    );
                } else {
                    history = new ArrayList<>();
                }
            }
            
            // 添加新对话
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "用户");
            userMsg.put("content", userMessage);
            history.add(userMsg);
            
            Map<String, String> aiMsg = new HashMap<>();
            aiMsg.put("role", "助手");
            aiMsg.put("content", aiResponse);
            history.add(aiMsg);
            
            // 保持最多10轮对话（20条消息）
            if (history.size() > 20) {
                history = history.subList(history.size() - 20, history.size());
            }
            
            // 保存
            conversation.setContextData(objectMapper.writeValueAsString(history));
            conversationRepository.save(conversation);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}