package org.agent.service;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Value("${app.git.repository-path:}")
    private String repositoryPath;

    @Tool(name = "getDailyCommits", description = "获取指定日期的Git提交记录")
    public String getDailyCommits(
            @ToolParam(description = "GIT日期格式,如：n days ago，不传则默认midnight") String date) {
        try {
            List<GitCommitService.CommitInfo> commits = gitCommitService.getCommitsByDate(repositoryPath, date);
            return JSONObject.toJSONString(commits);

        } catch (Exception e) {
            logger.error("获取Git提交记录失败", e);
            return "获取提交记录时发生错误：" + e.getMessage();
        }
    }

}
