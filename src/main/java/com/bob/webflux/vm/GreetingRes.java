package com.bob.webflux.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date: 2022-05-17 13:37
 * @description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GreetingRes {

    private String message;
}
