package com.lottecard.loca.cookie;

import java.time.Duration;

@SuppressWarnings("javadoc")
public enum CookieSpec {
    AUTH_COOKIE("login_cookie", Duration.ofDays(7).toSeconds());

    private final String name;
    private final long timeout;

    CookieSpec(String name, long timeout) {
        this.name = name;
        this.timeout = timeout;
    }
    @SuppressWarnings("javadoc")
    public String getName() {
        return name;
    }
    @SuppressWarnings("javadoc")
    public long getTimeout() {
        return timeout;
    }
}
