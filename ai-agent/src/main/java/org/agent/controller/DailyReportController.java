package org.agent.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.agent.service.DailyReportService;
import org.agent.service.GitCommitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日报生成控制器
 * 提供基于Git提交记录的日报生成API
 * @author djw
 * @date 2025-01-22
 */
@RestController
@RequestMapping("/api/daily-report")
@CrossOrigin(origins = "*")
public class DailyReportController {
    
    private static final Logger logger = LoggerFactory.getLogger(DailyReportController.class);
    
    @Resource
    private DailyReportService dailyReportService;
    
    @Resource
    private GitCommitService gitCommitService;
    
    @Value("${app.git.repository-path:}")
    private String defaultRepositoryPath;

    private ChatClient chatClient;

    private DailyReportController(ChatClient.Builder builder){
        this.chatClient=builder.defaultSystem("""
                你是一个专业的技术文档编写助手，擅长分析代码提交记录并生成工作日报。
                """).build();
    }
    /**
     * 生成日报
     */
    @PostMapping("/generate")
    public String generateReport(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String repoPath,
            @RequestParam(required = false) String focusArea) {
        
        try {
            logger.info("收到日报生成请求，日期：{}，仓库路径：{}，关注领域：{}", date, repoPath, focusArea);

            // 使用智能RAG生成日报
            String commitsJson = JSONObject.toJSONString(dailyReportService.getDailyCommits(null));
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            String prompt = String.format("""
            请基于以下Git提交记录生成一份工作日报：

            日期：%s
            提交记录：
            %s

            请按以下格式生成日报：
            task ${taskId}
            1.
            2.
            3.
            ...

            要求：
            1. 内容要专业、简洁
            2. 使用中文
            3. 格式清晰易读
            4. 不要画蛇添足
            """, dateStr, commitsJson);

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            logger.error("生成日报失败", e);
            return "生成日报时发生错误：" + e.getMessage();
        }
    }
    
    /**
     * 获取Git提交记录
     */
    @GetMapping("/commits")
    public ResponseEntity<Map<String, Object>> getCommits(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String repoPath) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate targetDate = date != null ? 
                LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : 
                LocalDate.now();
            
            String actualRepoPath = repoPath != null ? repoPath : defaultRepositoryPath;
            if (actualRepoPath == null || actualRepoPath.trim().isEmpty()) {
                actualRepoPath = System.getProperty("user.dir");
            }
            
            List<GitCommitService.CommitInfo> commits = 
                gitCommitService.getCommitsByDate(actualRepoPath, targetDate);
            
            response.put("success", true);
            response.put("commits", commits);
            response.put("count", commits.size());
            response.put("statistics", gitCommitService.getCommitStatistics(commits));
            response.put("date", targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
        } catch (Exception e) {
            logger.error("获取提交记录失败", e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取最近几天的提交记录
     */
    @GetMapping("/commits/recent")
    public ResponseEntity<Map<String, Object>> getRecentCommits(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(required = false) String repoPath) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String actualRepoPath = repoPath != null ? repoPath : defaultRepositoryPath;
            if (actualRepoPath == null || actualRepoPath.trim().isEmpty()) {
                actualRepoPath = System.getProperty("user.dir");
            }
            
            List<GitCommitService.CommitInfo> commits = 
                gitCommitService.getRecentCommits(actualRepoPath, days);
            
            response.put("success", true);
            response.put("commits", commits);
            response.put("count", commits.size());
            response.put("days", days);
            response.put("statistics", gitCommitService.getCommitStatistics(commits));
            
        } catch (Exception e) {
            logger.error("获取最近提交记录失败", e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "daily-report");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
