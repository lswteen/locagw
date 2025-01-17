package com.lottecard.loca;

import com.google.common.collect.ImmutableList;
import com.lottecard.loca.dto.UserAgentDto;
import com.lottecard.loca.meta.PlatformType;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Slf4j
public class UserAgentParseUtils {
    private static final String[] KNOWN_MOBILE_USER_AGENT_PREFIXES = new String[]{
            "w3c ", "w3c-", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq",
            "bird", "blac", "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco",
            "eric", "hipt", "htc_", "inno", "ipaq", "ipod", "jigs", "kddi", "keji",
            "leno", "lg-c", "lg-d", "lg-g", "lge-", "lg/u", "maui", "maxo", "midp",
            "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-", "newt", "noki",
            "palm", "pana", "pant", "phil", "play", "port", "prox", "qwap", "sage",
            "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-",
            "siem", "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-",
            "tosh", "tsm-", "upg1", "upsi", "vk-v", "voda", "wap-", "wapa", "wapi",
            "wapp", "wapr", "webc", "winw", "winw", "xda ", "xda-"
    };

    private static final String[] KNOWN_MOBILE_USER_AGENT_KEYWORDS = new String[]{
            "blackberry", "webos", "ipod", "lge vx", "midp", "maemo", "mmp", "mobile",
            "netfront", "hiptop", "nintendo DS", "novarra", "openweb", "opera mobi",
            "opera mini", "palm", "psp", "phone", "smartphone", "symbian", "up.browser",
            "up.link", "wap", "windows ce"
    };

    private static final List<BrowserType> MOBILE_BROWSER_LIST = ImmutableList.of(BrowserType.MOBILE_BROWSER);

    public static UserAgentDto getUserAgentParse(String agent) {
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        return UserAgentDto.builder()
                .platform(MOBILE_BROWSER_LIST.contains(userAgent.getBrowser().getBrowserType()) ? PlatformType.MOBILE_WEB.getCode() : PlatformType.PC_WEB.getCode())
                .os(userAgent.getOperatingSystem().toString()).build();
    }

    public static boolean isMobile(ServerWebExchange exchange) {
        String userAgent = HeaderUtils.getRequestHeaderValue(HttpHeaders.USER_AGENT, exchange);
        // User-Agent prefix detection
        if (ObjectUtils.isEmpty(userAgent)) {
            return false;
        }

        if (userAgent.length() >= 4) {
            for (String keyword : KNOWN_MOBILE_USER_AGENT_PREFIXES) {
                if (userAgent.startsWith(keyword)) {
                    return true;
                }
            }
        }
        // UserAgent keyword detection for Mobile devices
        // Android special case || Apple special case
        if (userAgent.contains("android")
                || userAgent.contains("iphone") || userAgent.contains("ipod") || userAgent.contains("ipad")) {
            return true;
        }
        for (String keyword : KNOWN_MOBILE_USER_AGENT_KEYWORDS) {
            if (userAgent.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}