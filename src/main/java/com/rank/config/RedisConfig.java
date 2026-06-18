package com.rank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * <p>
 * 配置 RedisTemplate 实例，统一使用 String 序列化器，
 * 确保 Redis 中的 key 和 value 都以字符串形式存储和读取。
 * </p>
 */
@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate<String, String> 实例
     * <p>
     * 设置 Key 和 Value 的序列化器为 StringRedisSerializer，
     * 避免默认序列化器导致的 key 乱码问题。
     * </p>
     *
     * @param connectionFactory Redis连接工厂，由Spring自动注入
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 1. 创建 RedisTemplate 实例
        RedisTemplate<String, String> template = new RedisTemplate<>();
        
        // 2. 设置连接工厂
        template.setConnectionFactory(connectionFactory);

        // 3. 创建字符串序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 4. 设置 Key 和 Hash Key 序列化器
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // 5. 设置 Value 和 Hash Value 序列化器
        template.setValueSerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        // 6. 初始化模板配置
        template.afterPropertiesSet();
        
        return template;
    }
}