package com.yz.mall.design.factory;

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
        user.setAuth(AuthEnum.DEPARTMENT);

        AuthFactory authFactory;
        if (AuthEnum.UNIT.equals(user.getAuth())) {
            authFactory = new UnitAuthFactory();
        } else if (AuthEnum.DEPARTMENT.equals(user.getAuth())) {
            authFactory = new DepartmentAuthFactory();
        } else {
            authFactory = new PersonalAuthFactory();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("name", "张三");
        authFactory.setFilterConditions(user, params);
        System.out.println("过滤条件: " + params);
    }
}
