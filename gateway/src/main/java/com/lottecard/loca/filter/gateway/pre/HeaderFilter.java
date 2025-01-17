package com.lottecard.loca.filter.gateway.pre;

import com.lottecard.loca.HeaderUtils;
import com.lottecard.loca.HttpUtils;
import com.lottecard.loca.filter.gateway.OrderedGatewayFilterFactory;
import com.lottecard.loca.header.HeaderNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 * 요청 정보를 플랫폼 표준 헤더로 제공하는 필터
 *
 * @author creep
 */
@Slf4j
@Component
public class HeaderFilter extends OrderedGatewayFilterFactory<HeaderFilter.Config> {

    @Override
    public GatewayFilter apply() {
        return (exchange, chain) -> {
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .headers(headers -> {
                        headers.set(HeaderNames.USER_AGENT.getValue(), HeaderUtils.getRequestHeaderValue(HttpHeaders.USER_AGENT,
                                "default-user-agent" ,exchange));
                        headers.set(HeaderNames.CLIENT_IP.getValue(), HttpUtils.getClientIP(exchange));
                    }).build();

            log.info("HeaderFilter clientIp : {}",HeaderUtils.getRequestHeaderValue("X-Forwarded-For", exchange));
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }


    @Override
    public int getOrder() {
        return 2;
    }

    public class Config {
    }
}
