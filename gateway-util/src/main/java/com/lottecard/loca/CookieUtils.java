package com.lottecard.loca;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.logging.log4j.util.Strings.EMPTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtils {
    public static final String DEFAULT_PATH = "/";
    public static final String DEFAULT_SAME_SITE = EMPTY;     // 크롬80 버전부터 보안때문에 Lax가 자동으로 생성됨
    public static final boolean DEFAULT_SECURE = true;
    public static final boolean DEFAULT_HTTP_ONLY = true;
    public static final long DEFAULT_MAX_AGE = Duration.ofDays(7).toSeconds();
    public static final long SESSION_COOKIE_AGE = -1;

    public static void addCookie(String name, String value, String domain, long maxAge, ServerWebExchange exchange) {
        addCookie(name, value, domain, maxAge, DEFAULT_PATH, DEFAULT_SAME_SITE, DEFAULT_SECURE, DEFAULT_HTTP_ONLY, exchange);
    }

    public static void addCookie(String name, String value, String domain, long maxAge, String path, String sameSite, boolean secure, boolean httpOnly, ServerWebExchange exchange) {
        ResponseCookie cookie = createCookie(name, value, domain, path, maxAge, sameSite, secure, httpOnly);
        exchange.getResponse().addCookie(cookie);
    }

    public static void setCookie(String name, String value, String domain, String path, long maxAge, ServerWebExchange exchange) {
        ResponseCookie cookie = createCookie(name, value, domain, path, maxAge);
        exchange.getResponse().getCookies().set(name, cookie);
    }

    private static ResponseCookie createCookie(String name, String value, String domain, String path, long maxAge) {
        return createCookie(name, value, domain, path, maxAge, DEFAULT_SAME_SITE, DEFAULT_SECURE, DEFAULT_HTTP_ONLY);
    }

    private static ResponseCookie createCookie(String name, String value, String domain, String path, long maxAge,
                                               String sameSite, boolean secure, boolean httpOnly) {
        return ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .secure(secure)
                .sameSite(ofNullable(sameSite).orElse(EMPTY))
                .path(path)
                .domain(ofNullable(domain).orElse(EMPTY))
                .httpOnly(httpOnly)
                .build();
    }

    public static String getCookieValue(String name, ServerWebExchange exchange) {
        MultiValueMap<String, HttpCookie> map = exchange.getRequest().getCookies();
        HttpCookie httpCookie = map.getFirst(name);
        if (isNull(httpCookie)) {
            return EMPTY;
        }

        return httpCookie.getValue();
    }
}
