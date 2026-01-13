package com.wychesterso.transit.brisbane_bus.config;

import com.wychesterso.transit.brisbane_bus.dto.StopArrivalResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, List<StopArrivalResponse>> stopArrivalRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, List<StopArrivalResponse>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<List<StopArrivalResponse>> serializer =
                new Jackson2JsonRedisSerializer<>(
                        (Class<List<StopArrivalResponse>>) (Class<?>) List.class
                );

        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}