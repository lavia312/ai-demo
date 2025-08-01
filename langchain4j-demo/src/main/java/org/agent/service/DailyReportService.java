package org.agent.service;

import com.alibaba.fastjson2.JSONObject;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.spring.AiService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
@Description("日报生成服务")
@Service
public class DailyReportService {

    private static final Logger logger = LoggerFactory.getLogger(DailyReportService.class);

    @Resource
    private GitCommitService gitCommitService;

    @Value("${app.git.repository-path:}")
    private String repositoryPath;

    @Tool(name = "getDailyCommits",value = "获取指定日期的Git提交记录")
    public String getDailyCommits(@P("GIT日期格式,如：n days ago，不传则默认midnight") String date) {
        try {
            List<GitCommitService.CommitInfo> commits = gitCommitService.getCommitsByDate(repositoryPath, date);
            return JSONObject.toJSONString(commits);

        } catch (Exception e) {
            logger.error("获取Git提交记录失败", e);
            return "获取提交记录时发生错误：" + e.getMessage();
        }
    }


    @Tool(name = "getDate",value = "获取指定偏移量的日期")
    public String getDate(@P("天数偏移量，例如：0:今天，-1:昨天") int daysOffset) {
        System.out.println("调用getDate tool 参数:"+daysOffset);
        return LocalDate.now().plusDays(daysOffset)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
