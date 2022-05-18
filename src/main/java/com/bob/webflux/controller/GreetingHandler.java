package com.bob.webflux.controller;

import com.bob.webflux.vm.GreetingRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author: wangx
 * @date: 2022-05-17 13:39
 * @description:
 */
@Slf4j
@Component
public class GreetingHandler {

    public Mono<ServerResponse> getHello(ServerRequest request) {
        log.info("logId [{}], ThreadID [{}]", request.exchange().getLogPrefix(), Thread.currentThread().getId());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(GreetingRes.builder().message("Hello, Spring!").build()));
    }

    public Mono<ServerResponse> postHello(ServerRequest request) {
        return request.bodyToMono(String.class).flatMap(x -> {
            log.info("body [{}]", x);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(x);
        });
    }
}
