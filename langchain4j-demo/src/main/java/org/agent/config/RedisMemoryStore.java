package org.agent.config;

import ch.qos.logback.core.spi.LifeCycle;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisMemoryStore implements ChatMemoryStore {

    private static final String CHAT_MEMORY_KEY = "langchain:chat:memory:";

    @Resource
    private RedisTemplate<String,String> chatMemoryTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String value = chatMemoryTemplate.opsForValue().get(CHAT_MEMORY_KEY+memoryId);
        if(value==null){
            return List.of();
        }
        return ChatMessageDeserializer.messagesFromJson(value);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String s = ChatMessageSerializer.messagesToJson(messages);
        chatMemoryTemplate.opsForValue().set(CHAT_MEMORY_KEY+memoryId, s);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        chatMemoryTemplate.delete(CHAT_MEMORY_KEY+memoryId);
    }
}
