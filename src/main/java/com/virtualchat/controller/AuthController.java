package com.virtualchat.controller;

import com.virtualchat.model.User;
import com.virtualchat.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/generate-password")
    public ResponseEntity<Map<String, Object>> generatePassword() {
        Map<String, Object> response = new HashMap<>();
        String password = "hibaobao";
        String hashed = passwordEncoder.encode(password);
        
        response.put("originalPassword", password);
        response.put("hashedPassword", hashed);
        response.put("matches", passwordEncoder.matches(password, hashed));
        
        System.out.println("[密码生成] 原始密码: " + password);
        System.out.println("[密码生成] 哈希值: " + hashed);
        System.out.println("[密码生成] 验证: " + passwordEncoder.matches(password, hashed));
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        System.out.println("[登录调试] ========== 开始登录 ==========");
        System.out.println("[登录调试] 用户名: " + username);
        System.out.println("[登录调试] 输入的密码: " + password);
        System.out.println("[登录调试] 输入密码长度: " + password.length());
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            System.out.println("[登录调试] 用户不存在");
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(response);
        }
        
        User user = userOpt.get();
        String dbPassword = user.getPassword();
        System.out.println("[登录调试] 数据库中的完整密码: " + dbPassword);
        System.out.println("[登录调试] 数据库密码长度: " + dbPassword.length());
        
        // 尝试加密输入的密码进行对比
        String encodedInput = passwordEncoder.encode(password);
        System.out.println("[登录调试] 输入密码加密后: " + encodedInput);
        
        // 验证密码
        boolean matches = passwordEncoder.matches(password, dbPassword);
        System.out.println("[登录调试] 密码匹配结果: " + matches);
        
        // 额外验证:检查加密后的密码是否能匹配
        boolean encodedMatches = passwordEncoder.matches(password, encodedInput);
        System.out.println("[登录调试] 新加密的密码能否匹配: " + encodedMatches);
        
        if (matches) {
            // 登录成功，保存用户信息到session
            session.setAttribute("loggedInUser", user);
            
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("username", username);
            
            System.out.println("[登录调试] 登录成功!");
            System.out.println("[登录调试] ========== 结束 ==========");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("[登录调试] 密码不匹配");
            System.out.println("[登录调试] ========== 结束 ==========");
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已登出");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User user = (User) session.getAttribute("loggedInUser");
        
        if (user != null) {
            response.put("authenticated", true);
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response);
        } else {
            response.put("authenticated", false);
            return ResponseEntity.status(401).body(response);
        }
    }
}