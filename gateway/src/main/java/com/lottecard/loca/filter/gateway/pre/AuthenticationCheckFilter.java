package com.lottecard.loca.filter.gateway.pre;

import com.lottecard.loca.HeaderUtils;
import com.lottecard.loca.core.property.ClientProperties;
import com.lottecard.loca.filter.gateway.OrderedGatewayFilterFactory;
import com.lottecard.loca.header.HeaderNames;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.util.*;

/**
 * 요청 ip, siteCode 유효성을 검사하는 필터
 * API용
 *
 * application.yml 아래 설정
 *
 * ---
 *   authorization:
 *     auth-info-map:
 *       qw: //siteCode
 *         client-ip:
 *           - 0:0:0:0:0:0:0:1
 *           - 127.0.0.1
 *           - 172.30.96.101
 * ---
 *
 * @author creep
 */
@Slf4j
@Component
@EnableConfigurationProperties(value = ClientProperties.class)
public class AuthenticationCheckFilter extends OrderedGatewayFilterFactory<AuthenticationCheckFilter.Config> {

    private final Map<String, List<IpSubnetFilterRule>> authorizationInfos;

    public AuthenticationCheckFilter(ClientProperties clientProperties) {
        super(Config.class);
        this.authorizationInfos = new HashMap<>();
        if (ObjectUtils.isEmpty(clientProperties.authInfoMap())) {
            log.error("psg client authorization properties is empty.");
            return;
        }

        for (Map.Entry<String, ClientProperties.AuthorizationProperties> entry : clientProperties.authInfoMap().entrySet()) {
            String siteCode = entry.getKey();
            List<IpSubnetFilterRule> rules = new ArrayList<>();
            for (String clientIp : entry.getValue().clientIp()) {
                if (!clientIp.contains("/")) { // no netmask, add default
                    clientIp = clientIp + "/32";
                }
                String[] ipAddressCidrPrefix = clientIp.split("/", 2);
                String ipAddress = ipAddressCidrPrefix[0];
                int cidrPrefix = Integer.parseInt(ipAddressCidrPrefix[1]);
                rules.add(new IpSubnetFilterRule(ipAddress, cidrPrefix, IpFilterRuleType.ACCEPT));
            }
            authorizationInfos.put(siteCode, rules);
        }
    }

    private boolean isAccessible(ServerWebExchange exchange) {
        String requestIp = HeaderUtils.getRequestHeaderValue(HeaderNames.CLIENT_IP.getValue(), exchange);


        boolean accessible = true;

        log.info("Access {} for requestIp: {}", accessible ? "granted" : "denied", requestIp);
        return accessible;
    }

    @Override
    protected GatewayFilter apply() {
        return (exchange, chain) -> {
            if (isAccessible(exchange)) {
                return chain.filter(exchange);
            }
            Optional<String> clientIpOptional = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(HeaderNames.CLIENT_IP.getValue()));
            log.info("=====================> AuthenticationCheckFilter ClientIP : {}", clientIpOptional);
            clientIpOptional.ifPresent(clientIp -> log.error("IP Access Denied ClientIp: {}", clientIp));

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "IP Access Denied");
        };
    }

    @Getter
    @Builder
    public static class Config {}

    @Override
    public int getOrder() {
        return 10;
    }
}