package com.lottecard.loca.filter.gateway.post;

import com.lottecard.loca.filter.gateway.OrderedGatewayFilterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PostGatewayFilter extends OrderedGatewayFilterFactory<PostGatewayFilter.Config> {
    public PostGatewayFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply() {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.just(exchange))
                .map(serverWebExchange -> {
                    log.info("PostGatewayFilter");
                    return serverWebExchange;
                })
                .then();
    }

    public static class Config {

    }
}
