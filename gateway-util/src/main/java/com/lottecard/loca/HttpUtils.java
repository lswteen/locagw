package com.lottecard.loca;

import com.google.common.net.HttpHeaders;
import com.lottecard.loca.meta.ResponseObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.LinkedHashSet;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HttpUtils {
    private static final String EMPTY = "";

    public static HttpStatus getHttpStatus(HttpStatusCode httpStatusCode) {
        if (httpStatusCode == null) {
            log.error("HttpStatusCode is null");
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        HttpStatus httpStatus = HttpStatus.resolve(httpStatusCode.value());
        if (httpStatus == null) {
            log.error("Unknown httpStatusCode {}", httpStatusCode.value());
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return httpStatus;
    }

    public static String getClientIP(ServerWebExchange exchange) {
        String clientIp = HeaderUtils.getRequestHeaderValue(HttpHeaders.X_FORWARDED_FOR, exchange);

        if (ObjectUtils.isEmpty(clientIp)) {
            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            return String.valueOf(ObjectUtils.isEmpty(remoteAddress) ? "" : remoteAddress.getAddress().getHostAddress());
        }

        int commaIndex = clientIp.indexOf(",");
        if (commaIndex > 0) {
            return clientIp.substring(0, commaIndex).trim();
        }

        return clientIp;
    }

    public static boolean isApi(String path) {
        if (ObjectUtils.isEmpty(path)) {
            return false;
        }
        return path.startsWith("/api");
    }

    public static boolean isApi(URI uri) {
        if (ObjectUtils.isEmpty(uri)) {
            return false;
        }
        return uri.getPath().startsWith("/api");
    }

    public static URI getURI(ServerWebExchange exchange) {
        LinkedHashSet<URI> originUris = exchange.getAttribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        if (ObjectUtils.isEmpty(originUris)) {
            LinkedHashSet<URI> uris = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
            if (ObjectUtils.isEmpty(uris)) {
                return null;
            } else {
                return uris.stream().findFirst().orElse(null);
            }
        } else {
            return originUris.stream().findFirst().orElse(null);
        }
    }

    public static String getPath(ServerWebExchange exchange) {
        URI uri = getURI(exchange);
        if (ObjectUtils.isEmpty(uri)) {
            return exchange.getRequest().getPath().toString();
        }
        return uri.getPath();
    }

    public static Mono<Void> unauthorized(String redirectPath, ResponseObject responseObject, ServerWebExchange exchange) {
        return HttpUtils.isApi(getPath(exchange)) ? unauthorized(responseObject, exchange) : sendRedirect(redirectPath, exchange);
    }

    public static Mono<Void> unauthorized(ResponseObject responseObject, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getAttributes().put(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR, MediaType.APPLICATION_JSON.toString());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(
                Mono.just(DataBufferUtils.getDataBuffer(responseObject))
        );
    }

    public static Mono<Void> sendRedirect(String redirectPath, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(redirectPath));
        return response.setComplete();
    }
}
