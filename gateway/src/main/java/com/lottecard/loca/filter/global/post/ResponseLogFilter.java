package com.lottecard.loca.filter.global.post;

import com.lottecard.loca.FormatUtils;
import com.lottecard.loca.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return chain.filter(exchange)
                .then(Mono.just(exchange))
                .map(serverWebExchange -> {
                    String logMessage = FormatUtils.formatLogMessage(
                            String.format("<=== responsePath: %s, method: %s, ip: %s, hostAddress: %s, responseHeaders: %s",
                            exchange.getRequest().getPath(),
                            exchange.getRequest().getMethod(),
                            HttpUtils.getClientIP(exchange),
                            exchange.getRequest().getRemoteAddress(),
                            exchange.getResponse().getHeaders())
                    );
                    log.info(logMessage);
                    return serverWebExchange;
                })
                .then();
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
