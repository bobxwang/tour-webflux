package com.bob.webflux;

import com.bob.webflux.client.GreetingClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class WebfluxApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebfluxApplication.class, args);
    }

    @Autowired
    GreetingClient greetingClient;

    @Override
    public void run(String... args) throws Exception {
        String abcd = greetingClient.getMessage().block();
        log.info("received response [{}]", abcd);
        greetingClient.testWebClient();
    }
}
