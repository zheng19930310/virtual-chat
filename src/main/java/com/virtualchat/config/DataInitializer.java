package com.virtualchat.config;

import com.virtualchat.model.User;
import com.virtualchat.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            // 创建默认用户 hibaobao
            if (userRepository.findByUsername("hibaobao").isEmpty()) {
                User user = new User();
                user.setUsername("hibaobao");
                // 使用当前系统的PasswordEncoder生成哈希
                String encodedPassword = passwordEncoder.encode("hibaobao");
                user.setPassword(encodedPassword);
                System.out.println("[数据初始化] 生成的密码哈希: " + encodedPassword);
                userRepository.save(user);
                System.out.println("默认用户已创建: hibaobao");
            }
        };
    }
}