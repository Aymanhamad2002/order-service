package com.polarbookshop.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.polarbookshop.orderservice.config.DataConfig;
import com.polarbookshop.orderservice.order.domain.OrderStatus;
import com.polarbookshop.orderservice.repository.OrderRepository;
import com.polarbookshop.orderservice.service.OrderService;

import reactor.test.StepVerifier;

@DataR2dbcTest
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
@Import(DataConfig.class)
@Testcontainers
class OrderRepositoryR2dbcTests {

    @Container
    static PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:14.4");

    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", OrderRepositoryR2dbcTests::r2dbcUrl);
        registry.add("spring.r2dbc.username", postgresql::getUsername);
        registry.add("spring.r2dbc.password", postgresql::getPassword);
        registry.add("spring.flyway.url", postgresql::getJdbcUrl);
        registry.add("spring.flyway.user", postgresql::getUsername);
        registry.add("spring.flyway.password", postgresql::getPassword);
    }

    private static String r2dbcUrl() {
        return String.format("r2dbc:postgresql://%s:%d/%s",
                postgresql.getHost(),
                postgresql.getFirstMappedPort(),
                postgresql.getDatabaseName());
    }

    @Test
    void createRejectedOrder() {
        var rejectedOrder = OrderService.buildRejectedOrder("1234567890", 3);

        StepVerifier.create(orderRepository.save(rejectedOrder))
                .expectNextMatches(order ->
                        order.id() != null
                                && order.createdDate() != null
                                && order.status().equals(OrderStatus.REJECTED))
                .verifyComplete();
    }
}
