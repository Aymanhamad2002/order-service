package com.polarbookshop.orderservice.repository;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.polarbookshop.orderservice.order.domain.Order;

import reactor.core.publisher.Flux;

public interface OrderRepository  extends ReactiveCrudRepository<Order, Long> {
    Flux<Order> findAllByCreatedBy(String createdBy);

}
