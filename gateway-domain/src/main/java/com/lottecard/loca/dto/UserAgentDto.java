package com.lottecard.loca.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@SuppressWarnings("javadoc")
public class UserAgentDto {
    private String platform;
    String os;
    String osVersion;
    String model;
}
