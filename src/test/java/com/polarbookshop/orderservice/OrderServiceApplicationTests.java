package com.polarbookshop.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "spring.cloud.stream.bindings.dispatchOrder-in-0.consumer.auto-startup=false")
class OrderServiceApplicationTests {

    @MockitoBean
    private ReactiveJwtDecoder jwtDecoder;

	@Test
	void contextLoads() {
	}

}
