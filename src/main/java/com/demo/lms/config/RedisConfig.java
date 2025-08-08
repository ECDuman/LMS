package com.demo.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Configures and provides a RedisTemplate bean for String keys and Object values.
     * This bean is essential for interacting with Redis for operations like blacklisting tokens.
     *
     * @param connectionFactory The RedisConnectionFactory provided by Spring Boot's auto-configuration,
     * which handles the actual connection to the Redis server.
     * @return A configured RedisTemplate instance.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory); // Set the connection factory

        // Configure serializers for keys and values
        // Keys will be serialized as Strings (e.g., JWT tokens)
        template.setKeySerializer(new StringRedisSerializer());
        // Values will be serialized using Jackson for broader object support (e.g., Boolean true)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Configure hash key and value serializers (if you plan to use hash operations)
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer()); // Corrected method name

        template.afterPropertiesSet(); // Initialize the template
        return template;
    }
}
