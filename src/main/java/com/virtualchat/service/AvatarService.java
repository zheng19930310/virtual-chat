package com.virtualchat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${spring.ai.dashscope.api-key:}")
    private String dashscopeApiKey;

    @Value("${aliyun.rtc.app-id:}")
    private String rtcAppId;

    @Value("${aliyun.rtc.app-key:}")
    private String rtcAppKey;

    @Value("${aliyun.avatar.avatar-id:2d_avatar_001}")
    private String defaultAvatarId;

    /**
     * 生成RTC Token用于前端拉流
     */
    public Map<String, String> generateRtcToken(String userId, String channelId) {
        try {
            long timestamp = System.currentTimeMillis() / 1000;
            String nonce = UUID.randomUUID().toString().replace("-", "");
            
            // 生成Token (简化版,实际需要使用阿里云RTC SDK)
            String token = generateRtcTokenInternal(rtcAppId, rtcAppKey, userId, channelId, timestamp, nonce);
            
            Map<String, String> result = new HashMap<>();
            result.put("appId", rtcAppId);
            result.put("channelId", channelId);
            result.put("userId", userId);
            result.put("token", token);
            result.put("nonce", nonce);
            result.put("timestamp", String.valueOf(timestamp));
            result.put("avatarId", defaultAvatarId);
            
            logger.info("生成RTC Token成功, userId: {}, channelId: {}", userId, channelId);
            return result;
            
        } catch (Exception e) {
            logger.error("生成RTC Token失败: {}", e.getMessage());
            throw new RuntimeException("生成RTC Token失败", e);
        }
    }

    /**
     * 生成RTC Token (简化实现)
     * 注意: 实际生产环境建议使用阿里云官方RTC SDK
     */
    private String generateRtcTokenInternal(String appId, String appKey, String userId, 
                                            String channelId, long timestamp, String nonce) {
        try {
            // 简化版Token生成逻辑
            // 实际应该使用阿里云RTC SDK的TokenGenerator
            String plainText = appId + userId + channelId + timestamp + nonce;
            
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(appKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            
            byte[] hash = mac.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
            
        } catch (Exception e) {
            logger.error("Token生成失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 获取数字人WebSocket连接URL
     */
    public String getAvatarWebSocketUrl() {
        return "wss://dashscope.aliyuncs.com/api-ws/v1/inference";
    }

    /**
     * 获取默认的2D数字人形象ID列表
     */
    public String[] getDefaultAvatarIds() {
        // 这些是阿里云公共形象库的示例ID
        // 实际使用时需要从控制台获取真实的形象ID
        return new String[]{
            "2d_avatar_001",  // 示例形象1
            "2d_avatar_002",  // 示例形象2
            "2d_avatar_003"   // 示例形象3
        };
    }
}
