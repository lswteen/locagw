package com.lottecard.loca.core.config;

import io.micrometer.context.ContextRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.actuate.autoconfigure.tracing.TracingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Hooks;

@Slf4j
@Configuration
@EnableConfigurationProperties(value= TracingProperties.class)
public class MdcContextPropagationConfig {
    public MdcContextPropagationConfig(TracingProperties tracingProperties) {
        if (!ObjectUtils.isEmpty(tracingProperties.getBaggage().getCorrelation().getFields())) {
            tracingProperties.getBaggage().getCorrelation().getFields().forEach(claim -> ContextRegistry.getInstance()
                    .registerThreadLocalAccessor(claim, () -> MDC.get(claim), value -> MDC.put(claim, value), () -> MDC.remove(claim)));
        }
        Hooks.enableAutomaticContextPropagation();
    }
}
