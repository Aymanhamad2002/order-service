package com.polarbookshop.orderservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import com.polarbookshop.orderservice.config.SecurityConfig;

import com.polarbookshop.orderservice.controller.OrderController;
import com.polarbookshop.orderservice.dto.OrderRequest;
import com.polarbookshop.orderservice.order.domain.Order;
import com.polarbookshop.orderservice.order.domain.OrderStatus;
import com.polarbookshop.orderservice.service.OrderService;

import reactor.core.publisher.Mono;

@WebFluxTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerWebFluxTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    void whenBookNotAvailableThenRejectedOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = OrderService.buildRejectedOrder(
                orderRequest.isbn(), orderRequest.quantity());

        given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
                .willReturn(Mono.just(expectedOrder));

        given(jwtDecoder.decode("test-token")).willReturn(Mono.just(Jwt.withTokenValue("test-token")
                .header("alg", "RS256").subject("test-user").build()));

        webTestClient.post().uri("/orders")
                .headers(headers -> headers.setBearerAuth("test-token"))
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class)
                .value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
                });
    }

    @Test
    void whenUnauthenticatedThenUnauthorized() {
        webTestClient.post().uri("/orders")
                .bodyValue(new OrderRequest("1234567890", 3))
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
