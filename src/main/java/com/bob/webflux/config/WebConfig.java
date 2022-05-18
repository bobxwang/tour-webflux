package com.bob.webflux.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebFilter;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author: wangx
 * @date: 2022-05-17 16:27
 * @description:
 */
@Slf4j
@EnableWebFlux
@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Bean
    public WebFilter logWebFilter() {
        return (exchange, chain) -> {
            log.info("logId [{}], ThreadID [{}]", exchange.getLogPrefix(), Thread.currentThread().getId());
            return chain.filter(exchange);
        };
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        ServerCodecConfigurer.ServerDefaultCodecs codecs = configurer.defaultCodecs();
        codecs.maxInMemorySize(512 * 1024);
        codecs.enableLoggingRequestDetails(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/public", "classpath:/static/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                .resourceChain(true);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET")
                .allowedHeaders("header1")
                .exposedHeaders("header1")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public ReactorResourceFactory resourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);
        return factory;
    }

    @Bean
    public WebClient webClient() {

        Function<HttpClient, HttpClient> mapper = client -> {
            // Further customizations...
            return client.doOnConnected(connection ->
                    connection.addHandlerLast(new ReadTimeoutHandler(10))
                            .addHandlerLast(new WriteTimeoutHandler(10))
            ).responseTimeout(Duration.ofSeconds(2));
        };

        ClientHttpConnector connector =
                new ReactorClientHttpConnector(resourceFactory(), mapper);

        Consumer<ClientCodecConfigurer> consumer = c -> c.defaultCodecs().enableLoggingRequestDetails(true);

        return WebClient.builder().clientConnector(connector)
                .exchangeStrategies(ExchangeStrategies.builder().codecs(consumer).build())
                .build();
    }
}
