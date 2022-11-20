package com.yz.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName CheckAuthGatewayFilterFactory
 * @Description 自定义局部过滤器
 * @Author yunze
 * @Date 2022/11/19 19:00
 * @Version 1.0
 */
@Component
@Slf4j
public class CheckGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
    @Override
    public GatewayFilter apply(NameValueConfig config) {
        return (exchange, chain) -> {

            log.info("调用CheckGatewayFilterFactory===》" + config.getName() + ":" + config.getValue());

            return chain.filter(exchange);
        };
    }
}
