package com.lottecard.loca.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@SuppressWarnings("javadoc")
public class DeviceInfoDto {
    String viewMode;            // 뷰 모드 ( web 또는 app)
    String platform;            // app platform 정보
    String os;
    String osVersion;           // os vesrion
    String model;
    String adId;                // 안드로이드 adId (광고 ID)
    String version;             // App version
    String launchPath;          // launch_path
    String session;             // session id
    String permanent;           // 기기 고유 아이디
    String clientIp;            // clientIP
    String referer;
}
