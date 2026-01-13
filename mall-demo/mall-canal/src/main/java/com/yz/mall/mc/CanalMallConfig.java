package com.yz.mall.mc;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * @author yunze
 * @date 2025/12/2 星期二 23:12
 */
@Configuration
public class CanalMallConfig {

    @Bean("mallConnector")
    public CanalConnector canalConnector() {
        return CanalConnectors.newSingleConnector(new InetSocketAddress("127.0.0.1", 9933), "mall", "", "");
    }
}
