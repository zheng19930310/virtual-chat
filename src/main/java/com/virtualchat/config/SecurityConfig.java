package com.virtualchat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
                .maximumSessions(1)
            )
            .authorizeHttpRequests(auth -> auth
                // 公开访问的路径
                .requestMatchers(
                    "/api/auth/**",      // 认证API
                    "/login",            // 登录页面
                    "/",                 // 首页
                    "/chat",             // 聊天页面
                    "/chat.html",        // 聊天页面HTML
                    "/css/**",           // CSS资源
                    "/js/**",            // JS资源  
                    "/images/**",        // 图片资源
                    "/h2-console/**"     // H2数据库控制台
                ).permitAll()
                // 所有其他请求都允许访问(已登录用户可以访问所有API)
                .anyRequest().permitAll()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .formLogin(form -> form.disable());
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
