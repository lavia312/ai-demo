package org.agent.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.agent.ai.ChatService;
import org.agent.service.DailyReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiServiceFactory {
    @Resource
    private StreamingChatModel streamingChatModel;
    @Resource
    private RedisMemoryStore redisMemory;
    @Bean
    public ChatService createChatService(){
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(30)
                .chatMemoryStore(redisMemory)
                .build();
        return AiServices.builder(ChatService.class)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(new DailyReportService())
                .build();
    }
}
