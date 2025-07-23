package org.agent.service;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 日报生成服务
 * 基于Git提交记录生成AI日报
 * @author djw
 * @date 2025-01-22
 */
@Service
public class DailyReportService {

    private static final Logger logger = LoggerFactory.getLogger(DailyReportService.class);

    @Resource
    private GitCommitService gitCommitService;
    /**
     * 获取指定日期的Git提交记录
     * @param date 日期，格式：yyyy-MM-dd，不传则默认今天
     * @return JSON格式的提交记录
     */
    @Tool(name = "getDailyCommits",description = "get git commit messages start at midnight of the day")
    public String getDailyCommits(@ToolParam(description = "search date,default today",required = false) String date) {
        try {
            LocalDate targetDate = date != null ?
                LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                LocalDate.now();
            String repositoryPath = System.getenv("PROJECT_PATH");
            List<GitCommitService.CommitInfo> commits = gitCommitService.getCommitsByDate(repositoryPath, targetDate);
            return JSONObject.toJSONString(commits);

        } catch (Exception e) {
            logger.error("获取Git提交记录失败", e);
            return "获取提交记录时发生错误：" + e.getMessage();
        }
    }
}
