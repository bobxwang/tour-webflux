package com.bob.webflux.config;

import com.bob.webflux.controller.GreetingHandler;
import com.bob.webflux.controller.UserHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * @date: 2022-05-17 13:43
 * @description:
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RouterConfig {

    @Autowired
    private UserHandler userHandler;
    @Autowired
    private GreetingHandler greetingHandler;

    @Bean
    public RouterFunction<ServerResponse> route() {

        return RouterFunctions
                .route()
                .path("/users", builder -> // 分组路由
                        builder.nest(
                                accept(MediaType.APPLICATION_JSON),
                                b -> b.GET("/{id}", userHandler::findPerson)
                                        .GET(userHandler::listUser)
                                        .before(request -> ServerRequest.from(request)  // 前置拦截
                                                .header("X-REQUESTHEADER", UUID.randomUUID().toString())
                                                .build())
                                        .after((req, res) -> {                          // 后置拦截
                                            log.info("request:[{}] response:[{}]", req, res);
                                            return res;
                                        })
                                        .filter((req, next) -> {                        // 过滤模拟权限
                                            String auth = req.headers().firstHeader(HttpHeaders.AUTHORIZATION);
                                            if (auth != null && auth.contentEquals("bb")) {
                                                return next.handle(req);
                                            } else {
                                                return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
                                            }
                                        })
                        )
                )
                .POST("/users", accept(MediaType.APPLICATION_JSON), userHandler::createPerson)
                .build()
                .andRoute(GET("/handler/hello").and(accept(MediaType.APPLICATION_JSON)), greetingHandler::getHello)
                .andRoute(POST("/handler/hello").and(accept(MediaType.APPLICATION_JSON)), greetingHandler::postHello);
    }

    @Autowired
    public void setHandlerMapping(RequestMappingHandlerMapping mapping, UserHandler userHandler)
            throws NoSuchMethodException {

        RequestMappingInfo info = RequestMappingInfo.paths("/users/{id}").methods(RequestMethod.GET).build();
        Method method = UserHandler.class.getMethod("getUser", Long.class);
        mapping.registerMapping(info, userHandler, method);
    }
}
