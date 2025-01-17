package com.lottecard.loca.header;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HeaderNames {
    // Request Ip 정보
    IP("Ip", true),
    // Request Url 정보
    URL("Url", false),
    // Referer 정보
    REFERER("Referer", false),
    // Client IP
    CLIENT_IP("clientIp",false),
    // User Agent
    USER_AGENT("userAgent",false),
    ;

    private final String value;
    private final Boolean removableFlag;
}
