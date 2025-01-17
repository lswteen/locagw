package com.lottecard.loca.core.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "tour.authorization")
public record ClientProperties(
        Map<String, AuthorizationProperties> authInfoMap
) {

    public record AuthorizationProperties(
            List<String> clientIp
    ) {}
}
