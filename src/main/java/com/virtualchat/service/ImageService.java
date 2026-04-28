package com.virtualchat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Value("${spring.ai.dashscope.api-key:}")
    private String apiKey;
    
    // 图片保存目录
    private static final String IMAGE_DIR = "generated-images";
    
    // 初始化图片目录
    public ImageService() {
        File dir = new File(IMAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 从消息中提取场景描述
     * 格式: "帮我生成'XX'一段场景的描述"
     */
    public String extractSceneDescription(String message) {
        Pattern pattern = Pattern.compile("帮我生成['\"]([^'\"]+)['\"]一段场景的描述");
        Matcher matcher = pattern.matcher(message);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }

    /**
     * 生成4张场景图片并保存到本地
     */
    public List<String> generateSceneImages(String sceneDescription) {
        List<String> imagePaths = new ArrayList<>();
        
        try {
            // 确保目录存在
            File dir = new File(IMAGE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            if (apiKey == null || apiKey.isEmpty()) {
                logger.warn("未配置API Key，使用占位图片");
                // 如果没有配置API Key，生成本地占位图片
                for (int i = 0; i < 4; i++) {
                    String fileName = "image_" + System.currentTimeMillis() + "_" + i + ".png";
                    String filePath = IMAGE_DIR + "/" + fileName;
                    generatePlaceholderImage(filePath, sceneDescription, i + 1);
                    imagePaths.add("/images/" + fileName);
                }
                return imagePaths;
            }
            
            // 调用通义万相AI模型生成图片
            logger.info("开始调用AI生成图片，场景描述: {}", sceneDescription);
            
            for (int i = 0; i < 4; i++) {
                try {
                    String fileName = "image_" + System.currentTimeMillis() + "_" + i + ".png";
                    String filePath = IMAGE_DIR + "/" + fileName;
                    
                    // 构建提示词
                    String prompt = sceneDescription + "，高清，精美，细节丰富，4K画质";
                    
                    // 调用DashScope API生成图片
                    String imageUrl = callDashScopeAPI(prompt);
                    
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // 从URL下载图片
                        downloadImage(imageUrl, filePath);
                        logger.info("AI图片下载成功: {}", filePath);
                    } else {
                        logger.warn("AI返回图片URL为空，使用占位图片");
                        generatePlaceholderImage(filePath, sceneDescription, i + 1);
                    }
                    
                    imagePaths.add("/images/" + fileName);
                    
                } catch (Exception e) {
                    logger.error("生成第{}张图片失败: {}", i + 1, e.getMessage());
                    // 如果AI生成失败，使用占位图片
                    String fileName = "image_" + System.currentTimeMillis() + "_" + i + ".png";
                    String filePath = IMAGE_DIR + "/" + fileName;
                    generatePlaceholderImage(filePath, sceneDescription, i + 1);
                    imagePaths.add("/images/" + fileName);
                }
            }
            
            logger.info("图片生成完成，共生成{}张", imagePaths.size());
            
        } catch (Exception e) {
            logger.error("图片生成失败: {}", e.getMessage());
            e.printStackTrace();
        }
        
        return imagePaths;
    }
    
    /**
     * 调用DashScope API生成图片(异步)
     */
    private String callDashScopeAPI(String prompt) {
        try {
            // 第一步: 提交任务
            String taskId = submitImageTask(prompt);
            if (taskId == null) {
                logger.error("提交图片生成任务失败");
                return null;
            }
            
            logger.info("图片生成任务已提交，task_id: {}", taskId);
            
            // 第二步: 轮询任务状态
            return waitForTaskCompletion(taskId);
            
        } catch (Exception e) {
            logger.error("调用DashScope API失败: {}", e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 提交图片生成任务
     */
    private String submitImageTask(String prompt) {
        try {
            URL url = new URL("https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("X-DashScope-Async", "enable");
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            
            String jsonInputString = String.format(
                "{\"model\":\"wanx-v1\",\"input\":{\"prompt\":\"%s\"},\"parameters\":{\"size\":\"1024*1024\",\"n\":1}}",
                prompt.replace("\"", "\\\"")
            );
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            logger.info("提交任务响应码: {}", responseCode);
            
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                    
                    String responseBody = response.toString();
                    logger.info("提交任务响应: {}", responseBody);
                    
                    // 提取task_id
                    int taskIdStart = responseBody.indexOf("\"task_id\":\"");
                    if (taskIdStart > 0) {
                        taskIdStart += 11;
                        int taskIdEnd = responseBody.indexOf("\"", taskIdStart);
                        if (taskIdEnd > taskIdStart) {
                            return responseBody.substring(taskIdStart, taskIdEnd);
                        }
                    }
                }
            } else {
                logger.error("提交任务失败，响应码: {}", responseCode);
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            logger.error("提交图片生成任务失败: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 等待任务完成并获取图片URL
     */
    private String waitForTaskCompletion(String taskId) {
        int maxRetries = 20; // 最多轮询20次
        int retryInterval = 2000; // 每次间隔2秒
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                Thread.sleep(retryInterval);
                
                URL url = new URL("https://dashscope.aliyuncs.com/api/v1/tasks/" + taskId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == 200) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line.trim());
                        }
                        
                        String responseBody = response.toString();
                        logger.info("任务状态查询({}/{}): {}", i + 1, maxRetries, responseBody);
                        
                        // 检查任务状态
                        if (responseBody.contains("\"task_status\":\"SUCCEEDED\"")) {
                            // 任务成功，提取图片URL
                            int urlStart = responseBody.indexOf("\"url\":\"");
                            if (urlStart > 0) {
                                urlStart += 7;
                                int urlEnd = responseBody.indexOf("\"", urlStart);
                                if (urlEnd > urlStart) {
                                    String imageUrl = responseBody.substring(urlStart, urlEnd);
                                    logger.info("图片生成成功: {}", imageUrl);
                                    return imageUrl;
                                }
                            }
                        } else if (responseBody.contains("\"task_status\":\"FAILED\"")) {
                            logger.error("图片生成任务失败");
                            return null;
                        }
                        // 其他状态(PENDING/RUNNING)继续等待
                    }
                }
                
                connection.disconnect();
                
            } catch (Exception e) {
                logger.error("查询任务状态失败: {}", e.getMessage());
            }
        }
        
        logger.error("等待图片生成超时");
        return null;
    }
    
    /**
     * 从URL下载图片
     */
    private void downloadImage(String imageUrl, String filePath) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(filePath)) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                logger.info("图片下载成功: {}", filePath);
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            logger.error("下载图片失败: {}", e.getMessage());
            throw new RuntimeException("下载图片失败", e);
        }
    }
    
    /**
     * 生成带文字的占位图片
     */
    private void generatePlaceholderImage(String filePath, String description, int index) {
        try {
            int width = 512;
            int height = 512;
            
            // 创建图片
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // 启用抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 生成随机背景色
            Random random = new Random();
            Color bgColor = new Color(
                150 + random.nextInt(105),
                150 + random.nextInt(105),
                150 + random.nextInt(105)
            );
            g2d.setColor(bgColor);
            g2d.fillRect(0, 0, width, height);
            
            // 绘制边框
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(10, 10, width - 20, height - 20);
            
            // 绘制文字
            g2d.setColor(new Color(50, 50, 50));
            g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 28));
            
            String title = "场景图片 " + index;
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, height / 2 - 20);
            
            // 绘制场景描述
            g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 18));
            g2d.setColor(new Color(80, 80, 80));
            
            // 分割描述文字
            String[] words = description.split("");
            StringBuilder line = new StringBuilder();
            int y = height / 2 + 30;
            
            for (String word : words) {
                line.append(word);
                if (fm.stringWidth(line.toString()) > width - 60) {
                    int lineWidth = fm.stringWidth(line.toString());
                    g2d.drawString(line.toString(), (width - lineWidth) / 2, y);
                    line = new StringBuilder();
                    y += 25;
                }
            }
            if (line.length() > 0) {
                int lineWidth = fm.stringWidth(line.toString());
                g2d.drawString(line.toString(), (width - lineWidth) / 2, y);
            }
            
            g2d.dispose();
            
            // 保存图片
            ImageIO.write(image, "PNG", new File(filePath));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}