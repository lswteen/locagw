package com.lottecard.loca.filter.gateway.pre;

import com.lottecard.loca.DataBufferUtils;
import com.lottecard.loca.HeaderUtils;
import com.lottecard.loca.filter.gateway.OrderedGatewayFilterFactory;
import com.lottecard.loca.meta.ResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

/**
 * 요청 http method를 제한하는 필터
 *
 * @author creep
 */
@Slf4j
@Component
public class HttpMethodAllowFilter extends OrderedGatewayFilterFactory<HttpMethodAllowFilter.Config> {

    private static final String MODIFIED_HEADER_KEY = "";

    private final List<HttpMethod> allowedMethods = List.of(GET, POST);
    private final List<HttpMethod> modifiableMethods = List.of(DELETE, PUT);

    public HttpMethodAllowFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply() {
        return (exchange, chain) -> {
            HttpMethod httpMethod = exchange.getRequest().getMethod();

            if (!allowedMethods.contains(httpMethod)) {
                log.error("method:{}", httpMethod);
                exchange.getResponse().setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                ResponseObject responseObject = ResponseObject.errorWithMessage(HttpStatus.METHOD_NOT_ALLOWED.value(),HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),"PSG-GATEWAY HttpMethodAllowFilter ERROR",null);
                return exchange.getResponse().writeWith(Mono.just(DataBufferUtils.getDataBuffer(responseObject)));
            }

            HttpMethod overrideMethod = HttpMethod.valueOf(HeaderUtils.getRequestHeaderValue(MODIFIED_HEADER_KEY, exchange));
            if (modifiableMethods.contains(overrideMethod)) {
                ServerHttpRequest mutateRequest = exchange.getRequest().mutate()
                        .method(overrideMethod)
                        .build();
                return chain.filter(exchange.mutate().request(mutateRequest).build());
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}
