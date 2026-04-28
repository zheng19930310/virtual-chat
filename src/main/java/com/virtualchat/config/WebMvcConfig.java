package com.virtualchat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /images/** 到 generated-images 目录
        String imagePath = new File("generated-images").getAbsolutePath();
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagePath + "/");
    }
}
