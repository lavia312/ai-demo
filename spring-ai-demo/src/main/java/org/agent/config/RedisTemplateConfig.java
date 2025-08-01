package org.agent.config;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Bean("chatMemoryTemplate")
    public RedisTemplate<String, Message> chatMemoryTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Message> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 使用 String 序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());


        // Value 使用 JSON 序列化
        // 随便填一个Message的实现类, 不然报错,原因待排查
        template.setValueSerializer(new GenericToStringSerializer<>(UserMessage.class));
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }
}
