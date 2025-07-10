package com.yz.mall.mybatis;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

/**
 * 数据表Id自动生成器（MyBatis-Plus 提供的扩展方式）
 * <p>
 * https://baomidou.com/guides/id-generator/
 *
 * @author yunze
 * @date 2025/1/15 17:16
 */
@Component
public class TableIdGenerator implements IdentifierGenerator {

    @Override
    public Long nextId(Object entity) {
        return IdUtil.getSnowflakeNextId();
    }
}
