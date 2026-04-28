package com.virtualchat.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "friend_id", nullable = false)
    private Long friendId;
    
    @Column(nullable = false)
    private String sender; // "user" or "friend"
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "message_type")
    private String messageType = "text"; // text 或 image
    
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls; // JSON格式的图片URL列表
    
    @Column(name = "context_id")
    private Long contextId; // 关联的对话上下文ID
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}