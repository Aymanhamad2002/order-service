package com.polarbookshop.orderservice.client;

import java.awt.print.Book;
import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.polarbookshop.orderservice.dto.BookDto;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class BookClient {
    private static final String BOOKS_ROOT_API= "/books/";
    private final WebClient webClient;
    public BookClient(WebClient webClient) {
        this.webClient = webClient;
    }
    
    public Mono<BookDto> getBookByIsbn(String isbn){
        return webClient
        .get()
        .uri(BOOKS_ROOT_API + isbn)
        .retrieve()
        .bodyToMono(BookDto.class)
        .timeout(Duration.ofSeconds(5),Mono.empty())
        .onErrorResume(WebClientResponseException.NotFound.class,exception->Mono.empty())
        .retryWhen(
            Retry.backoff(3, Duration.ofMillis(100))
        ).onErrorResume(Exception.class,e -> Mono.empty())
        ;

    }


}
