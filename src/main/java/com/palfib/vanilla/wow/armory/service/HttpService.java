package com.palfib.vanilla.wow.armory.service;

import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

@Component
public class HttpService {

    /**
     * Executes a Rest GET http call, with the given parameters.
     *
     * @param <T>         result's type.
     * @param baseUrl     URI's baseURL
     * @param pathBuilder endpoint pathbuilder function
     * @param clazz       result's class
     * @return The API call's result will be returned mapped to T type.
     */
    public <T> T get(final String baseUrl, final Function<UriBuilder, URI> pathBuilder, final Class<T> clazz) {
        val client = WebClient.builder().baseUrl(baseUrl).build();
        return client.get()
                .uri(pathBuilder)
                .retrieve()
                .bodyToMono(clazz)
                .block();
    }
}
