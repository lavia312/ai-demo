package org.agent.controller;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import jakarta.annotation.Resource;
import org.agent.service.DBToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/mcp")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private ChatClient chatClient;

    public ChatController(ChatClient.Builder builder,ToolCallbackProvider toolCallbackProvider){
        logger.info("初始化ChatController，配置ChatClient");
        this.chatClient = builder.defaultTools(toolCallbackProvider).defaultSystem("""
                You are a bilingual data assistant with two independent capabilities:
                Local SQL Tool – use only when the user explicitly asks for data or queries.
                MCP Daily-Report Tool – use only when the user explicitly asks for a daily report.
                For any other request, ignore both tools and answer as a normal AI.
                """).build();
    }

    @PostMapping(value = "/chat", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> chat(@RequestBody String message){
        logger.info("收到聊天请求: {}", message);
        try {
            return chatClient.prompt()
                    .advisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .messages(new UserMessage(message))
                .stream()
                .content()
                .doOnComplete(() -> logger.info("AI响应完成"))
                .doOnError(error -> logger.error("AI响应错误: {}", error.getMessage(), error));
        } catch (Exception e) {
            logger.error("处理聊天请求失败: {}", e.getMessage(), e);
            return Flux.just("抱歉，处理您的请求时出现错误: " + e.getMessage());
        }
    }

}
