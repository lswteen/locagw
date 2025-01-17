package com.lottecard.loca.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("${management.endpoints.web.base-path}")
    private String basePath;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf((csrf) -> {
                    csrf.disable();
                })
                .headers((headerSpec -> {
                    headerSpec.referrerPolicy(referrer -> referrer.policy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.ORIGIN));
                }))
                .authorizeExchange((exchanges -> {
                    exchanges.pathMatchers(basePath + "/health", basePath + "/refresh" , basePath + "/info").permitAll();
                    exchanges.pathMatchers(basePath + "/**").authenticated();
                    exchanges.anyExchange().permitAll();
                }))
                .httpBasic(Customizer.withDefaults())
                .formLogin((formLogin) -> {
                    formLogin.disable();
                })
                .build();
    }
}
