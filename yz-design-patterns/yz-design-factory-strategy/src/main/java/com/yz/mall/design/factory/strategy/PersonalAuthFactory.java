package com.yz.mall.design.factory.strategy;

import java.util.Map;

/**
 * 个人权限
 *
 * @author yunze
 * @date 2024/1/20 15:35
 */
public class PersonalAuthFactory implements AuthStrategy {

    @Override
    public void setFilterConditions(User user, Map<String, Object> filter) {
        filter.put("personId", user.getPersonId());
        System.out.println("数据过滤: 个人权限过滤");
    }
}
