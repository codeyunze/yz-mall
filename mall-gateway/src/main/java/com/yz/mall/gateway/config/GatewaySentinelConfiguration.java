package com.yz.mall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 将本地限流配置注册为 Sentinel Gateway 规则。
 *
 * @author yunze
 * @date 2026/11/17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewaySentinelConfiguration {

    private final GatewaySentinelProperties properties;

    /**
     * 启动时加载 API 分组与网关流控规则。
     */
    @PostConstruct
    public void loadRules() {
        if (!properties.isEnabled() || properties.getRules() == null || properties.getRules().isEmpty()) {
            return;
        }
        Set<ApiDefinition> apis = new HashSet<>();
        Set<GatewayFlowRule> rules = new HashSet<>();
        for (GatewaySentinelProperties.ApiRule rule : properties.getRules()) {
            if (!StringUtils.hasText(rule.getResource()) || rule.getPaths() == null || rule.getPaths().isEmpty()) {
                continue;
            }
            apis.add(new ApiDefinition(rule.getResource()).setPredicateItems(toPredicates(rule.getPaths())));
            rules.add(new GatewayFlowRule(rule.getResource())
                    .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                    .setCount(rule.getCount())
                    .setIntervalSec(rule.getIntervalSec())
                    .setGrade(1));
        }
        if (apis.isEmpty()) {
            return;
        }
        GatewayApiDefinitionManager.loadApiDefinitions(apis);
        GatewayRuleManager.loadRules(rules);
        log.info("已加载网关限流规则 {} 条", rules.size());
    }

    private Set<ApiPredicateItem> toPredicates(List<String> paths) {
        Set<ApiPredicateItem> items = new HashSet<>();
        for (String path : paths) {
            if (!StringUtils.hasText(path)) {
                continue;
            }
            ApiPathPredicateItem item = new ApiPathPredicateItem();
            if (path.endsWith("/**")) {
                item.setPattern(path.substring(0, path.length() - 2));
                item.setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX);
            } else {
                item.setPattern(path);
                item.setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_EXACT);
            }
            items.add(item);
        }
        return items;
    }
}
