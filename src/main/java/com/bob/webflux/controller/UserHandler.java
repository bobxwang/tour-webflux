package com.bob.webflux.controller;

import com.bob.webflux.vm.UserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @author: wangx
 * @date: 2022-05-17 16:31
 * @description:
 */
@Component
public class UserHandler {

    @Autowired
    private Validator validator;

    @ResponseBody
    public String getUser(Long id) {
        return "abcd " + id;
    }

    public Mono<ServerResponse> listUser(ServerRequest serverRequest) {
        return ok().contentType(MediaType.APPLICATION_JSON).body(UserRes.list(), UserRes.class);
    }

    public Mono<ServerResponse> createPerson(ServerRequest serverRequest) {
        Mono<UserRes> userResMono = serverRequest.bodyToMono(UserRes.class).doOnNext(xx -> {
            Errors errors = new BeanPropertyBindingResult(xx, "user");
            validator.validate(xx, errors);
            if (errors.hasErrors()) {
                throw new ServerWebInputException(errors.toString());
            }
        });
        return userResMono.flatMap(xx -> ok().bodyValue(xx));
    }

    public Mono<ServerResponse> findPerson(ServerRequest serverRequest) {
        Integer id = Integer.valueOf(serverRequest.pathVariable("id"));
        return UserRes.list()
                .filter(x -> x.getId() == id)
                .next()
                .flatMap(xx -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(xx))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
