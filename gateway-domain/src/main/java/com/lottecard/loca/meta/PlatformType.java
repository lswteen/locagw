package com.lottecard.loca.meta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PlatformType {
    PC_WEB("pc_web", "pc"),
    MOBILE_WEB("mobile_web", "m_web"),
    ANDROID("android", "ad"),
    IOS("ios", "ios"),

    NONE("none", "none");

    private final String code;

    private final String alias;


}
