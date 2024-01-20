package com.yz.mall.design.factory.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yunze
 * @date 2024/1/20 15:59
 */
public class Demo {

    public static void main(String[] args) {
        User user = new User();
        user.setPersonId("123");
        user.setDepartmentId("456");
        user.setUnitId("789");
        user.setAuth(AuthEnum.UNIT);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "张三");
        AuthFactory.setFilterConditions(user, params);
        System.out.println("过滤条件: " + params);
    }
}
