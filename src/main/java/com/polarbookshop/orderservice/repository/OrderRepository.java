package com.polarbookshop.orderservice.repository;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.polarbookshop.orderservice.order.domain.Order;

public interface OrderRepository  extends ReactiveCrudRepository<Order, Long> {

}
