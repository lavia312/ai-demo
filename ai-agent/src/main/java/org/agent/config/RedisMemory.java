package org.agent.config;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisMemory implements ChatMemory {
    @Resource
    private RedisTemplate<String,Message> chatMemoryTemplate;
    @Override
    public void add(String conversationId, Message message) {
        chatMemoryTemplate.opsForList().rightPush(conversationId,message);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        chatMemoryTemplate.opsForList().rightPushAll(conversationId,messages);
    }

    @Override
    public List<Message> get(String conversationId) {
        return chatMemoryTemplate.opsForList().range(conversationId,0,-1);
    }

    @Override
    public void clear(String conversationId) {
        chatMemoryTemplate.delete(conversationId);
    }
}
