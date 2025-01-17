package com.lottecard.loca;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormatUtils {
    public static String formatLogMessage(String template, Object... args) {
        return String.format(template, args)
                .replaceAll("\\s", "")
                .replace("\"", "'")
                .replace("/", "")
                ;
    }

}
