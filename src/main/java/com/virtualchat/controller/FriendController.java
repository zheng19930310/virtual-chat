package com.virtualchat.controller;

import com.virtualchat.model.Friend;
import com.virtualchat.model.User;
import com.virtualchat.repository.FriendRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "*")
public class FriendController {

    @Autowired
    private FriendRepository friendRepository;

    // 获取当前用户的好友列表
    @GetMapping
    public ResponseEntity<?> getFriends(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        
        List<Friend> friends = friendRepository.findByUserId(user.getId());
        return ResponseEntity.ok(friends);
    }

    // 添加好友
    @PostMapping
    public ResponseEntity<?> addFriend(@RequestBody Friend friend, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        
        friend.setUserId(user.getId());
        Friend savedFriend = friendRepository.save(friend);
        
        return ResponseEntity.ok(savedFriend);
    }

    // 修改好友信息
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFriend(@PathVariable Long id, @RequestBody Friend friendDetails, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        
        Friend friend = friendRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("好友不存在"));
        
        // 验证好友属于当前用户
        if (!friend.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "无权操作"));
        }
        
        friend.setName(friendDetails.getName());
        friend.setAge(friendDetails.getAge());
        friend.setGender(friendDetails.getGender());
        friend.setPersonality(friendDetails.getPersonality());
        friend.setCharacteristics(friendDetails.getCharacteristics());
        
        Friend updatedFriend = friendRepository.save(friend);
        
        return ResponseEntity.ok(updatedFriend);
    }

    // 删除好友
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFriend(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        
        Friend friend = friendRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("好友不存在"));
        
        // 验证好友属于当前用户
        if (!friend.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "无权操作"));
        }
        
        friendRepository.delete(friend);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "好友已删除");
        
        return ResponseEntity.ok(response);
    }
}