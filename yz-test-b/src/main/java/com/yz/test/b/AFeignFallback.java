package com.yz.test.b;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author yunze
 * @date 2024/11/5 17:46
 */
@Component
public class AFeignFallback implements FallbackFactory<AFeign> {

    @Override
    public AFeign create(Throwable cause) {
        return new AFeign() {
            @Override
            public String test() {
                return "服务A请求失败了";
            }
        };
    }
}
