package com.polarbookshop.orderservice.service;


import org.springframework.stereotype.Service;

import com.polarbookshop.orderservice.client.BookClient;
import com.polarbookshop.orderservice.dto.BookDto;
import com.polarbookshop.orderservice.order.domain.Order;
import com.polarbookshop.orderservice.order.domain.OrderStatus;
import com.polarbookshop.orderservice.repository.OrderRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookClient bookClient;

    public OrderService(OrderRepository orderRepository,BookClient bookClient) {
        this.orderRepository = orderRepository;
        this.bookClient = bookClient;
    }
    
    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> submitOrder(String isbn, int quantity){
        return  bookClient.getBookByIsbn(isbn)
        .map(book -> buildAcceptedOrder(book, quantity))
        .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
        .flatMap(orderRepository::save);
       
    }
    private static Order buildAcceptedOrder(BookDto book,int quantity){
        return Order.of(book.isbn(), book.title() + "-" + book.author(), book.price(), quantity,OrderStatus.ACCEPTED);

    }
    public static Order buildRejectedOrder(String isbn, int quantity) {
        // TODO Auto-generated method stub
        return Order.of(isbn, null, null, quantity, OrderStatus.REJECTED);
    }
}
