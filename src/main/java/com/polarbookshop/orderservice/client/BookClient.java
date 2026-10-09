package com.polarbookshop.orderservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.polarbookshop.orderservice.dto.BookDto;

import reactor.core.publisher.Mono;

@Component
public class BookClient {
    private static final Logger log = LoggerFactory.getLogger(BookClient.class);
    private static final String BOOKS_ROOT_API = "/books/";
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
                .doOnSubscribe(subscription -> log.debug("Looking up catalog book with ISBN {}", isbn))
                .doOnNext(book -> log.debug("Catalog book found for ISBN {}", isbn))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> {
                    log.info("Catalog book unavailable for ISBN {}", isbn);
                    return Mono.empty();
                })
                .doOnError(error -> log.error("Catalog lookup failed for ISBN {}", isbn, error));
    }

}
