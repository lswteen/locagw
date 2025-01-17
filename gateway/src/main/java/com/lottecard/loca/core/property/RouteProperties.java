package com.lottecard.loca.core.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "tour.gateway")
public record RouteProperties(
        Map<String, List<RouteDefinition>> routes
) {
    public record RouteDefinition(
            List<FilterDefinition> filters,
            List<PredicateDefinition> predicates
    ) {}
}