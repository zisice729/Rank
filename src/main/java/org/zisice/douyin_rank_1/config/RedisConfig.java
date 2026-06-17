package org.zisice.douyin_rank_1.config;

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
     * @param connectionFactory Redis 连接工厂，由 Spring 自动注入
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 创建字符串序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 设置 Key 序列化器
        template.setKeySerializer(stringSerializer);
        // 设置 Hash Key 序列化器
        template.setHashKeySerializer(stringSerializer);
        // 设置 Value 序列化器
        template.setValueSerializer(stringSerializer);
        // 设置 Hash Value 序列化器
        template.setHashValueSerializer(stringSerializer);

        // 初始化模板配置
        template.afterPropertiesSet();
        return template;
    }
}