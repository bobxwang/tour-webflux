package com.bob.webflux.vm;

import lombok.Builder;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * @date: 2022-05-17 18:16
 * @description:
 */
@Data
@Builder
public class UserRes {

    private Integer id;
    private Integer age;
    private String name;

    public static Flux<UserRes> list() {
        List<UserRes> userRes = new ArrayList<>();
        userRes.add(UserRes.builder().id(1).age(1).name("a").build());
        userRes.add(UserRes.builder().id(2).age(2).name("b").build());
        userRes.add(UserRes.builder().id(3).age(3).name("c").build());
        return Flux.fromIterable(userRes);
    }
}
