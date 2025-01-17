package com.lottecard.loca;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataBufferUtils {
    public static DataBuffer getDataBuffer(Object object) {
        try {
            return DefaultDataBufferFactory.sharedInstance.wrap(ObjectMapperUtils.defaultMapper().writeValueAsBytes(object));
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException");
            return DefaultDataBufferFactory.sharedInstance.wrap(new byte[]{});
        }
    }
}
