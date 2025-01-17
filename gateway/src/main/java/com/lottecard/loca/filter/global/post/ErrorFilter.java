package com.lottecard.loca.filter.global.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * gateway filter 예외 처리 필터
 *
 * @author creep
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        String predicate = route.getPredicate().toString();

        // if not match path

        // filter errror case

        // response error case
        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new CustomServerHttpResponseDecorator(originalResponse);

        // replace response with decorator
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private class CustomServerHttpResponseDecorator extends ServerHttpResponseDecorator {

        public CustomServerHttpResponseDecorator(ServerHttpResponse delegate) {
            super(delegate);
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            return super.writeWith(body);
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1001;
    }
}
