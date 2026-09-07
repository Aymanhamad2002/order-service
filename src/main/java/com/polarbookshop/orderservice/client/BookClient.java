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
    
public Mono<BookDto> getBookByIsbn(String isbn) {
    return webClient
        .get()
        .uri(BOOKS_ROOT_API + isbn)
        .retrieve()
        .bodyToMono(BookDto.class)
        .doOnNext(book ->
            System.out.println("BOOK FOUND: " + book)
        )
        .doOnError(error ->
            System.err.println("CATALOG ERROR: " + error)
        );
}

}
