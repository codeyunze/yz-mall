package com.yz.mall.sys.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yunze
 * @date 2025/8/16 星期六 10:12
 */
@FeignClient(name = "mall-sys"
        , contextId = "extendSysTest"
        , path = "extend/sys/test")
public interface ExtendSysTestFeign {

    /**
     * 测试接口
     */
    @RequestMapping("{id}")
    String test(@PathVariable String id);
}
