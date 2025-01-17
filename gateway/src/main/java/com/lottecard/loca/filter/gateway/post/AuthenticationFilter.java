package com.lottecard.loca.filter.gateway.post;

import com.lottecard.loca.filter.gateway.OrderedGatewayFilterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.stereotype.Component;

/**
 * 로그인 요청 시 쿠키를 생성해주는 필터
 * 인증 서버 구현 시 rewrite path 설정으로 치환
 */
@Slf4j
@Component
public class AuthenticationFilter extends OrderedGatewayFilterFactory<AuthenticationFilter.Config> {
    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply() {
        return (exchange, chain) -> chain.filter(exchange);
    }

    public static class Config {

    }
}
