package org.agent.controller;

import org.agent.service.DailyReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder, DailyReportService dailyReportService,
                          SyncMcpToolCallbackProvider syncMcpToolCallbackProvider,
                          ChatMemory chatMemory){
        logger.info("初始化ChatController，配置ChatClient");
        this.chatClient = builder.defaultTools(dailyReportService).defaultSystem("""
                You are good at writing technical documents with the tools below:
                getDailyCommits tool – use only when the user explicitly asks for a daily report.
                For any other request, ignore both tools and answer as a normal AI.
                """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @PostMapping(value = "/chat", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> chat(@RequestBody String message){
        logger.info("收到聊天请求: {}", message);
        try {
            return chatClient.prompt()
                    .messages(new UserMessage(message))
                    .advisors(memo->memo.param(ChatMemory.CONVERSATION_ID,"123"))
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
