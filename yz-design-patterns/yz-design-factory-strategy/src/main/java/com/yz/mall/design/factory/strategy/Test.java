package com.yz.mall.design.factory.strategy;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yunze
 * @date 2024/1/20 15:59
 */
public class Test {

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


        System.err.println(LocalDateTimeUtil.between(LocalDateTime.now(), LocalDateTimeUtil.endOfDay(LocalDateTime.now())).toMinutes());
    }
}
