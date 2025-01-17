package com.lottecard.loca.filter.web;

import com.lottecard.loca.HeaderUtils;
import com.lottecard.loca.HttpUtils;
import com.lottecard.loca.header.HeaderNames;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoggingWebFilter implements WebFilter {
    private final Environment environment;

    private final String DEVICE = "device";
    private final String NONE = "none";
    private String APPLICATION;

    private String PROFILE;
    private final String POSITION = "0";
    private final String PAGE_NO = "0";

    private final String SVC_NATEION_CD = "SG";
    private final String APP_NO = "1618";
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?://[^/]+)?(/.*)$");

    @PostConstruct
    public void init() {
        this.APPLICATION = environment.getProperty("spring.application.name");
        this.PROFILE = getActiveProfile();
    }

    private String getActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return (activeProfiles.length > 0) ? activeProfiles[0] : "local";
    }

    private String extractPath(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        if (matcher.matches()) {
            return matcher.group(2); // 그룹 2는 URL의 경로 부분
        }
        return url; // 매칭되지 않으면 원래 URL 반환
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .contextWrite(ctx -> {
                    ctx = ctx
                            .put("uri", HttpUtils.getPath(exchange))
                            .put("svc_nation_cd",SVC_NATEION_CD)
                            .put("appNo", APP_NO)
                            .put("contexturl",extractPath(String.valueOf(exchange.getRequest().getURI())))
                            .put("useragent", HeaderUtils.getRequestHeaderValue(HeaderNames.USER_AGENT.getValue(),"Unknown",exchange))
                            .put("postion", POSITION)
                            .put("location", PROFILE)
                            .put("targetserver", APPLICATION)
                            .put("detailmessage", NONE)
                            .put("page_no", PAGE_NO)
                            .put("device_info", Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(DEVICE)).orElse(NONE))
                            .put("clientIP", HttpUtils.getClientIP(exchange));
                    return ctx;
                });

    }

}
