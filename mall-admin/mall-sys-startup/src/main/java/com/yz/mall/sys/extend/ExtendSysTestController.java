package com.yz.mall.sys.extend;

import cn.dev33.satoken.annotation.SaIgnore;
import com.yz.mall.base.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.datasource.DataSourceException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SYS-测试
 *
 * @author yunze
 * @date 2025/8/16 星期六 9:45
 */
@Slf4j
@RestController
@RequestMapping("/extend/sys/test")
public class ExtendSysTestController {

    /**
     * 测试接口
     */
    @SaIgnore
    @RequestMapping("{id}")
    public String test(@PathVariable String id) {
        log.info("mall-sys系统/extend/sys/test接口");
        if ("0".equals(id)) {
            throw new RuntimeException("SYS服务/extend/sys/test接口异常");
        } else if ("1".equals(id)) {
            throw new DataSourceException();
        }
        return "success";
    }
}
