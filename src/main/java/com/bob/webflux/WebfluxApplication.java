package com.bob.webflux;

import com.bob.webflux.client.GreetingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
        System.out.println(abcd);

        greetingClient.testWebClient();
    }
}
