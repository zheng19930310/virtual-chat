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
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StreamingChatService {

    @Autowired
    private DashScopeChatModel chatModel;
    
    @Autowired
    private FriendRepository friendRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 流式获取AI回复（支持思维链模式）
     */
    public Flux<String> getStreamingAIResponse(Long friendId, String userMessage, boolean useContext, String thinkingChainMode) {
        // 获取好友信息
        Friend friend = friendRepository.findById(friendId)
            .orElseThrow(() -> new RuntimeException("好友不存在"));
        
        // 构建系统提示词
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
            fullPrompt = buildContextualPrompt(friendId, systemPrompt, userMessage);
        } else {
            fullPrompt = systemPrompt + "\n用户: " + userMessage;
        }
        
        // 根据思维链模式添加相应的指令
        boolean isThinkingMode = "COT".equals(thinkingChainMode) || "TOT".equals(thinkingChainMode);
        if ("COT".equals(thinkingChainMode)) {
            fullPrompt += "\n\n【重要】请严格按照以下格式输出（使用XML标签）：\n" +
                         "1. 首先输出 <think>\n" +
                         "2. 然后展示你的逐步思考过程（思维链）\n" +
                         "3. 输出 </think>\n" +
                         "4. 输出 <answer>\n" +
                         "5. 给出简洁的最终回答\n" +
                         "6. 输出 </answer>\n" +
                         "\n示例：\n" +
                         "<think>\n" +
                         "让我来分析这个问题...首先...其次...\n" +
                         "</think>\n" +
                         "<answer>\n" +
                         "这是最终答案。\n" +
                         "</answer>";
        } else if ("TOT".equals(thinkingChainMode)) {
            fullPrompt += "\n\n【重要】请严格按照以下格式输出（使用XML标签）：\n" +
                         "1. 首先输出 <think>\n" +
                         "2. 然后展示你的多种思路和方案比较（思维树）\n" +
                         "3. 输出 </think>\n" +
                         "4. 输出 <answer>\n" +
                         "5. 给出最佳方案的简洁回答\n" +
                         "6. 输出 </answer>\n" +
                         "\n示例：\n" +
                         "<think>\n" +
                         "方案A:... 方案B:... 方案C:... 经过比较，方案B最优...\n" +
                         "</think>\n" +
                         "<answer>\n" +
                         "基于方案B的最终答案。\n" +
                         "</answer>";
        }
        
        // 如果是思维链模式，添加标记到 prompt 中，让 AI 输出格式化的内容
        if (isThinkingMode) {
            System.out.println("[流式聊天] 思维链模式: " + thinkingChainMode);
        } else {
            System.out.println("[流式聊天] 常规模式");
        }
        
        // 直接流式输出 AI 响应（思维链模式下 AI 会按 prompt 要求输出标记）
        System.out.println("[流式聊天] 准备发送Prompt，长度: " + fullPrompt.length());
        
        return chatModel.stream(fullPrompt)
            .doOnNext(text -> System.out.println("[流式聊天] AI输出chunk: " + text.substring(0, Math.min(50, text.length()))))
            .filter(t -> t != null && !t.trim().isEmpty());
    }
    
    /**
     * 构建带上下文的提示词
     */
    private String buildContextualPrompt(Long friendId, String systemPrompt, String userMessage) {
        StringBuilder prompt = new StringBuilder(systemPrompt);
        prompt.append("\n\n以下是之前的对话历史：\n");
        
        Conversation conversation = conversationRepository.findByFriendId(friendId).orElse(null);
        
        if (conversation != null && conversation.getContextData() != null) {
            try {
                List<Map<String, String>> history = objectMapper.readValue(
                    conversation.getContextData(), 
                    new TypeReference<List<Map<String, String>>>() {}
                );
                
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
}
