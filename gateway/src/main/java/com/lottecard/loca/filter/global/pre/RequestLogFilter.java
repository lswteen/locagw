package com.lottecard.loca.filter.global.pre;

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
public class RequestLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String logMessage = FormatUtils.formatLogMessage(
                String.format("===> requestPath: %s, method: %s, ip: %s, hostAddress: %s, requestHeaders: %s",
                exchange.getRequest().getPath(),
                exchange.getRequest().getMethod(),
                HttpUtils.getClientIP(exchange),
                exchange.getRequest().getLocalAddress(),
                exchange.getRequest().getHeaders())
        );
        log.info(logMessage);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
