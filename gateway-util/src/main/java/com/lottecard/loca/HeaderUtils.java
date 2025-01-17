package com.lottecard.loca;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import static com.lottecard.loca.meta.Strings.EMPTY;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderUtils {
    public static String getRequestHeaderValue(String key, String defaultValue, ServerWebExchange exchange) {
        return getHeaderValue(key, defaultValue, exchange.getRequest().getHeaders());
    }

    public static String getRequestHeaderValue(String key, ServerWebExchange exchange) {
        return getHeaderValue(key, exchange.getRequest().getHeaders());
    }

    public static List<String> getRequestHeaderValues(String key, ServerWebExchange exchange) {
        return getHeaderValues(key, exchange.getRequest().getHeaders());
    }

    public static String getHeaderValue(String key, String defaultValue, HttpHeaders httpHeaders) {
        String headerValue = httpHeaders.getFirst(key);
        return ObjectUtils.isEmpty(headerValue) ? defaultValue : headerValue;
    }

    public static String getHeaderValue(String key, HttpHeaders httpHeaders) {
        String headerValue = httpHeaders.getFirst(key);
        return ObjectUtils.isEmpty(headerValue) ? EMPTY : headerValue;
    }

    public static List<String> getHeaderValues(String key, HttpHeaders httpHeaders) {
        List<String> headerValues = httpHeaders.get(key);
        return ObjectUtils.isEmpty(headerValues) ? List.of() : headerValues;
    }
}