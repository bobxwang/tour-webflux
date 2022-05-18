# Getting Started
##### Programming Models
``` java 
// HttpHandler抽象让你可以使用Tomcat/Netty/Jetty等容器来运行Http服务
public interface HttpHandler {
    Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response);
}
// WebHandler抽象提供像Session/Local/Principal等web应用属性
public interface WebHandler {
    Mono<Void> handle(ServerWebExchange exchange);
}
public class ReactorHttpHandlerAdapter implements BiFunction<HttpServerRequest, HttpServerResponse, Mono<Void>> { }
public interface WebExceptionHandler {
    Mono<Void> handle(ServerWebExchange exchange, Throwable ex);
}
public interface WebFilter {
    Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain);
}
public final class WebHttpHandlerBuilder { 
    public HttpHandler build() { }
}
public class DispatcherHandler implements WebHandler { 
    private List<HandlerMapping> handlerMappings;
    private List<HandlerAdapter> handlerAdapters;
    private List<HandlerResultHandler> resultHandlers;
} 
```
- Annotated Controllers
- Functional Endpoints
##### No-Blocking
> 如果要调用Blocking的方法，那么可以使用Reactor的publishOn算子来达成在另一个线程中消费
- WebClient
##### Codecs
- Encoder/Decoder是低级别的抽象
- HttpMessageWriter/HttpMessageReader用于 encode/decode Http协议的请求跟响应
- Encoder可以被包装成一个EncoderHttpMessageWriter
- Decoder可以被包装成一个DecoderHttpMessageReader
- ServerCodecConfigurer/ClientCodecConfigurer
##### LogId
ServerWebExchange.getLogPrefix()
##### Route
- 参照 RouterConfig 类
``` java
URI uri = UriComponentsBuilder
            .fromUriString("http://example.com/hotels/{hotel}")
            .queryParam("q", "{q}").encode()
            .buildAndExpand("Westin", "123")
            .toUri();  // http://example.com/hotels/Westin?q=123
```
- CORS
  - 参照 WebConfig.addCorsMappings
  - 也可以使用CorsWebFilter类
  ``` java
  @Bean
  CorsWebFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    // Possibly...
    // config.applyPermitDefaultValues()
    config.setAllowCredentials(true);
    config.addAllowedOrigin("https://domain1.com");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsWebFilter(source);
  }
  ```
##### 注意点
- 代码中不能使用toStream/toIterable等blocking方法，会报不支持的错误，在reactor运行模式下