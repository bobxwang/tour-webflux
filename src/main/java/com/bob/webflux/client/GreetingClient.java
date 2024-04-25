package com.bob.webflux.client;

import com.bob.webflux.vm.GreetingRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

/**
 * @date: 2022-05-17 13:53
 * @description:
 */
@Slf4j
@Component
public class GreetingClient {

    private final WebClient webClient;

    @Autowired
    public GreetingClient(WebClient.Builder builder) {
        Consumer<ClientCodecConfigurer> consumer = c -> c.defaultCodecs().enableLoggingRequestDetails(true);
        this.webClient = builder.baseUrl("http://127.0.0.1:8080")
                .exchangeStrategies(ExchangeStrategies.builder().codecs(consumer).build())
                .build();
    }

    public Mono<String> getMessage() {
        return this.webClient.get().uri("/handler/hello").accept(MediaType.APPLICATION_JSON)
                .retrieve() // retrieve method can be used to declare how to extract the response
                .bodyToMono(GreetingRes.class)
                .map(GreetingRes::getMessage);
    }

    public void testWebClient() {

        WebClient client = WebClient.builder()
                .filter((req, next) ->
                        Mono.deferContextual(contextView -> {
                            String v = contextView.get("foo");
                            log.info("v [{}]", v);
                            return next.exchange(req);
                        }))
                .build();
        client.get().uri("https://www.baidu.com/")
                .retrieve().bodyToMono(String.class).flatMap(body -> {
                    log.info("body [{}]", body);
                    return Mono.just(body);
                }).contextWrite(context -> context.put("foo", "aaaa"));

    }
}
