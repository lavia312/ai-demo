package org.agent.config;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import jakarta.annotation.Resource;
import org.agent.service.DBToolService;
import org.agent.service.DailyReportService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

/**
 * AI配置类
 * 配置Spring AI相关的Tool和服务
 * @author djw
 * @date 2025-01-22
 */
@Configuration
public class AiConfig {

    @Bean
    ToolCallbackProvider toolCallbackProvider(DBToolService dbToolService, McpSyncClient mcpSyncClient) {
        return MethodToolCallbackProvider.builder()
            .toolObjects(dbToolService,mcpSyncClient)
            .build();
    }
}
