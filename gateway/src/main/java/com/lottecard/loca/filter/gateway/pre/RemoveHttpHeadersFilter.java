package com.lottecard.loca.filter.gateway.pre;

import com.lottecard.loca.filter.gateway.OrderedGatewayFilterFactory;
import com.lottecard.loca.header.HeaderNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;

/**
 * 요청에 포함된 내부 전용 헤더값을 제거하는 필터
 *
 * @author creep
 */
@Slf4j
@Component
public class RemoveHttpHeadersFilter extends OrderedGatewayFilterFactory<RemoveHttpHeadersFilter.Config> {

    private static final List<String> removeHeaderNames = Arrays.stream(HeaderNames.values())
            .filter(HeaderNames::getRemovableFlag)
            .map(HeaderNames::getValue)
            .map(String::toLowerCase)
            .toList();

    public RemoveHttpHeadersFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply() {
        return (exchange, chain) -> {
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .headers(headers -> {
                        for (String key : removeHeaderNames) {
                            headers.remove(key);
                        }
                    }).build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
            return chain.filter(mutatedExchange);
        };
    }

    @Override
    public int getOrder() {
        return 1;
    }

    public static class Config {
    }
}
