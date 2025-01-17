package com.lottecard.loca.filter.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * AbstractGatewayFilterFactory에 Ordered 포함시킨 추상 필터 팩토리
 *
 * @author creep
 */
@Slf4j
@Component
public abstract class OrderedGatewayFilterFactory<C> extends AbstractGatewayFilterFactory<C> implements Ordered {
    protected OrderedGatewayFilterFactory() {
        super((Class<C>) Object.class);
    }

    protected OrderedGatewayFilterFactory(Class<C> configClass) {
        super(configClass);
    }

    public C config;

    @Override
    public GatewayFilter apply(C config) {
        this.config = config;
        return new OrderedGatewayFilter(this.apply(), this.getOrder());
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public C getConfig() {
        return config;
    }

    protected abstract GatewayFilter apply();
}
