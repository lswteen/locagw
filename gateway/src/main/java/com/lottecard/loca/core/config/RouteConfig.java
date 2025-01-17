package com.lottecard.loca.core.config;

import com.lottecard.loca.core.property.RouteProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = RouteProperties.class)
public class RouteConfig {
    private final GatewayProperties gatewayProperties;
    private final RouteProperties routeProperties;

    private final Environment environment;

    /**
     * @see <a href="https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/event/SpringApplicationEvent.html">SpringApplicationEvent</a>
     */
    @PostConstruct
    @Order(-1)
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void refresh() {
        try{
            String[] activeProfiles = environment.getActiveProfiles();
            if (activeProfiles.length == 0) {
                log.info("No active profiles set.");
            } else {
                log.info("Active profiles: {}", String.join(", ", activeProfiles));
            }
        }catch (Exception e){
            log.info("activeProfiles not set.");
        }

        log.info("RouteProperties refresh.2222222");
        routeProperties.routes()
                .forEach((key, value) -> addRoute(key, value, gatewayProperties.getRoutes()));
    }

    private void addRoute(String id, List<RouteProperties.RouteDefinition> routes, List<RouteDefinition> routeDefinitions) {
        if (ObjectUtils.isEmpty(routes)) {
            return;
        }

        RouteDefinition originRouteDefinition = routeDefinitions.stream()
                .filter(route -> route.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (!ObjectUtils.isEmpty(originRouteDefinition)) {
            // create route and add
            for (RouteProperties.RouteDefinition routeDefinition : routes) {
                if (ObjectUtils.isEmpty(routeDefinition.predicates())) {
                    log.warn("route predicate is empty. {}", routeDefinition);
                    continue;
                }

                List<FilterDefinition> filters = new ArrayList<>(originRouteDefinition.getFilters());
                if (!ObjectUtils.isEmpty(routeDefinition.filters())) {
                    filters.addAll(routeDefinition.filters());
                }

                RouteDefinition definition = new RouteDefinition();
                definition.setUri(originRouteDefinition.getUri());
                definition.setId(routeDefinition.predicates().get(0).getArgs().values().toString());
                definition.setFilters(filters);
                definition.setMetadata(new HashMap<>(originRouteDefinition.getMetadata()));
                definition.setOrder(originRouteDefinition.getOrder());
                definition.setPredicates(routeDefinition.predicates());

                routeDefinitions.add(definition);
                log.info("route[{}] is add to id[{}].", definition, id);
            }
        } else {
            log.warn("id[{}] is not exists.", id);
        }
    }
}