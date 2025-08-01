package org.agent.config;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class RedisMemory implements ChatMemory {

    private static final String CHAT_MEMORY_KEY = "chat:memory:";

    @Resource
    private RedisTemplate<String,Message> chatMemoryTemplate;
    @Override
    public void add(String conversationId, Message message) {
        chatMemoryTemplate.opsForList().rightPush(CHAT_MEMORY_KEY+conversationId,message);
    }
    @Override
    public void add(String conversationId, List<Message> messages) {
        chatMemoryTemplate.opsForList().rightPushAll(CHAT_MEMORY_KEY+conversationId,messages);
    }

    @Override
    public List<Message> get(String conversationId) {
        return Objects.requireNonNull(chatMemoryTemplate.opsForList().range(CHAT_MEMORY_KEY + conversationId, 0, -1));
    }

    @Override
    public void clear(String conversationId) {
        chatMemoryTemplate.delete(CHAT_MEMORY_KEY+conversationId);
    }
}
