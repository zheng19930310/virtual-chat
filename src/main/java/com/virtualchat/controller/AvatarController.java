package com.virtualchat.controller;

import com.virtualchat.service.AvatarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/avatar")
@CrossOrigin(origins = "*")
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    /**
     * 获取RTC Token和数字人配置
     */
    @GetMapping("/config")
    public ResponseEntity<?> getAvatarConfig(HttpSession session) {
        // 从session获取用户信息
        Object user = session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }

        try {
            // 生成RTC配置
            String userId = "user_" + System.currentTimeMillis();
            String channelId = "virtual_chat_channel";
            
            Map<String, String> rtcConfig = avatarService.generateRtcToken(userId, channelId);
            
            // 返回配置信息
            Map<String, Object> response = Map.of(
                "success", true,
                "websocketUrl", avatarService.getAvatarWebSocketUrl(),
                "rtcConfig", rtcConfig,
                "apiUrl", "/api/avatar/speak"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 获取可用的数字人形象列表
     */
    @GetMapping("/avatars")
    public ResponseEntity<?> getAvailableAvatars() {
        return ResponseEntity.ok(Map.of(
            "success", true,
            "avatars", avatarService.getDefaultAvatarIds()
        ));
    }
}
