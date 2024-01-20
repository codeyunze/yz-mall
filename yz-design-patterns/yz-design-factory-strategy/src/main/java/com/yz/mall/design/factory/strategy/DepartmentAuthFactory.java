package com.yz.mall.design.factory.strategy;

import java.util.Map;

/**
 * 部门权限
 *
 * @author yunze
 * @date 2024/1/20 15:47
 */
public class DepartmentAuthFactory implements AuthStrategy {

    @Override
    public void setFilterConditions(User user, Map<String, Object> filter) {
        filter.put("departmentId", user.getDepartmentId());
        System.out.println("数据过滤: 部门权限过滤");
    }
}
