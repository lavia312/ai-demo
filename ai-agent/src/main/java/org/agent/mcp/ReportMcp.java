package org.agent.mcp;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
@Component
public class ReportMcp {
    @Bean
    public McpSyncClient mcpSyncClient() {
        // 1. stdio 示例
        ServerParameters parameters = ServerParameters
                .builder("java")
                .arg("-jar")
                .arg("D:\\project\\spring-ai-demo\\report-mcp\\target\\report-mcp-1.0-SNAPSHOT-jar-with-dependencies.jar")
                .build();
        McpClientTransport transport = new StdioClientTransport(parameters);

        // 2. 构建 client
        McpSyncClient client = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(20))
                .build();
        client.initialize();   // 必须
        return client;
    }
}
