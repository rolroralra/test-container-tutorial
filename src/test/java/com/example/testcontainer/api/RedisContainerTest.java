package com.example.testcontainer.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisContainerTest {

    @LocalServerPort
    private Integer port;

    private static final String REDIS_DOCKER_IMAGE_TAG = "redis:7.2.3-alpine";

    private static final int REDIS_PORT = 6379;

    @Autowired
    private RedissonClient redissonClient;

    static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>(
        "postgres:15-alpine"
    );

    static GenericContainer<?> redis = new GenericContainer<>(
        REDIS_DOCKER_IMAGE_TAG
    ).withExposedPorts(REDIS_PORT);

    @BeforeAll
    static void beforeAll() {
        postgreSQL.start();
        redis.start();
    }

    @AfterAll
    static void afterAll() {
        postgreSQL.stop();
        redis.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQL::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQL::getUsername);
        registry.add("spring.datasource.password", postgreSQL::getPassword);
        registry.add("spring.sql.init.schema-locations", () -> "classpath:sql/schema-postgre.sql");

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(REDIS_PORT));
    }

    @Test
    void lockAndUnLockTest() {
        assertDoesNotThrow(() -> {
            String lockKeyName = "test";

            redissonClient.getLock(lockKeyName).lock();

            redissonClient.getLock(lockKeyName).unlock();
        });
    }
}

