package com.example.testcontainer.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RedissonConfig {
    @Value("${spring.data.redis.host:localhost}")
    private String REDIS_HOST;

    @Value("${spring.data.redis.port:6379}")
    private int REDIS_PORT;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
            .setAddress("redis://%s:%d".formatted(REDIS_HOST, REDIS_PORT));

        return Redisson.create(config);
    }
}
