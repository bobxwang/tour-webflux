package com.bob.webflux.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.concurrent.TimeUnit;

/**
 * @author: wangx
 * @date: 2022-05-17 16:22
 * @description:
 */
@RestController
@RequestMapping("controller")
public class GreetingController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello(ServerRequest serverRequest) {
        if (serverRequest.exchange().checkNotModified("etag")) {
            return null;
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
                .eTag("version")
                .body("Hello, String...");
    }
}
