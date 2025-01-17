package com.lottecard.loca.core.config;

import com.lottecard.loca.ObjectMapperUtils;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.time.Duration;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {
    @Bean
    public NettyServerCustomizer nettyServerCustomizer() {
        return httpServer -> httpServer
                .idleTimeout(Duration.ofSeconds(60));
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(ObjectMapperUtils.defaultMapper()));
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(ObjectMapperUtils.defaultMapper()));
    }
}

