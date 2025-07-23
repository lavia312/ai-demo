package org.agent;

import org.agent.service.DailyReportService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * MCP服务主启动类
 * 基于STDIO协议提供Git提交记录日报生成服务
 * @author djw
 * @date 2025-01-22
 */
@SpringBootApplication
public class ReportMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportMcpApplication.class, args);
    }

    @Bean
    ToolCallbackProvider toolCallbackProvider(DailyReportService dailyReportService) {
        return MethodToolCallbackProvider.builder()
            .toolObjects(dailyReportService)
            .build();
    }
}