# Gradle Setting
```groovy
ext {
    set('testcontainersVersion', "1.18.0")
}

dependencies {
    testImplementation 'org.testcontainers:junit-jupiter'
    testImplementation 'org.testcontainers:mysql'
    testImplementation 'org.testcontainers:postgresql'
    testImplementation 'org.testcontainers:kafka'
    testImplementation 'com.redis.testcontainers:testcontainers-redis'
}
```

# Test code using Test Container 
```java
class CustomerControllerWithMySQLTest {

    private final static String TEST_CONTAINER_IMAGE_TAG = "mysql:8.0.26";
    
    static MySQLContainer<?> mysql = new MySQLContainer<>(TEST_CONTAINER_IMAGE_TAG);

    @BeforeAll
    static void beforeAll() {
        mysql.start();
    }

    @AfterAll
    static void afterAll() {
        mysql.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
}
```

# Test code using Test Container for Redis
```java
class RedisContainerTest {
    private static final String REDIS_DOCKER_IMAGE_TAG = "redis:7.2.3-alpine";

    private static final int REDIS_PORT = 6379;

    static GenericContainer<?> redis = new GenericContainer<>(
        REDIS_DOCKER_IMAGE_TAG
    ).withExposedPorts(REDIS_PORT);

    @BeforeAll
    static void beforeAll() {
        redis.start();
    }

    @AfterAll
    static void afterAll() {
        redis.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(REDIS_PORT));
    }
}
```

# Test code using Test Container for Kafka
```java
class KafkaTestContainer {
    private final static String TEST_CONTAINER_IMAGE_TAG = "confluentinc/cp-kafka:6.2.1";

    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse(TEST_CONTAINER_IMAGE_TAG))
        .withExposedPorts(9092);

    @BeforeAll
    static void beforeAll() {
        if (kafka.isRunning()) {
            return;
        }

        kafka.start();
    }

    @AfterAll
    static void afterAll() {
        kafka.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.producer.key-serializer", StringSerializer.class::getName);
        registry.add("spring.kafka.producer.value-serializer", LongSerializer.class::getName);
    }
}
```
