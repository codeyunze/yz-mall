package com.yz.mall.design.factory;

import java.util.Map;

/**
 * 单位权限
 *
 * @author yunze
 * @date 2024/1/20 15:47
 */
public class UnitAuthFactory implements AuthFactory {

    @Override
    public void setFilterConditions(User user, Map<String, Object> filter) {
        filter.put("unitId", user.getUnitId());
        System.out.println("数据过滤: 单位权限过滤");
    }
}
