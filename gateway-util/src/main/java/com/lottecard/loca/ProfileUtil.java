package com.lottecard.loca;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
public class ProfileUtil {
    private final Environment environment;

    public String getActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return (activeProfiles.length > 0) ? activeProfiles[0] : "local";
    }
}
