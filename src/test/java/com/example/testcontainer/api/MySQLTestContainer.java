package com.example.testcontainer.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import com.example.testcontainer.domain.Customer;
import com.example.testcontainer.domain.repository.CustomerRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MySQLTestContainer {

    @LocalServerPort
    private Integer port;

    private static final String MYSQL_DOCKER_IMAGE_TAG = "mysql:8.0.26";
    private static final String REDIS_DOCKER_IMAGE_TAG = "redis:7.2.3-alpine";
    private static final int REDIS_PORT = 6379;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(MYSQL_DOCKER_IMAGE_TAG);

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(
        REDIS_DOCKER_IMAGE_TAG
    ).withExposedPorts(REDIS_PORT);

    @BeforeAll
    static void beforeAll() {
        redis.start();
        mysql.start();
    }

    @AfterAll
    static void afterAll() {
        redis.stop();
        mysql.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(REDIS_PORT));
    }

    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        customerRepository.deleteAll();
    }

    @Test
    void shouldGetAllCustomersByMySQL() {
        List<Customer> customers = List.of(
            new Customer(null, "John", "john@mail.com"),
            new Customer(null, "Dennis", "dennis@mail.com")
        );
        customerRepository.saveAll(customers);

        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1/customers")
        .then()
            .statusCode(200)
            .body(".", hasSize(2));
    }
}
