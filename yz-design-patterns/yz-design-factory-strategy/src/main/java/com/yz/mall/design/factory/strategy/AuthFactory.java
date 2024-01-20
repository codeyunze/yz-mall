package com.yz.mall.design.factory.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yunze
 * @date 2024/1/20 16:52
 */
public class AuthFactory {

    // 所有策略
    private static final Map<AuthEnum, AuthStrategy> STRATEGIES = new HashMap<>();

    static {
        STRATEGIES.put(AuthEnum.PERSONAL, new PersonalAuthFactory());
        STRATEGIES.put(AuthEnum.DEPARTMENT, new DepartmentAuthFactory());
        STRATEGIES.put(AuthEnum.UNIT, new UnitAuthFactory());
    }

    public static void setFilterConditions(User user, Map<String, Object> params) {
        AuthStrategy strategy = STRATEGIES.get(user.getAuth());
        if (strategy != null) {
            strategy.setFilterConditions(user, params);
        } else {
            // 处理未注册的AuthEnum值，可以抛出异常或打印日志等。
            throw new IllegalArgumentException("Unsupported auth type: " + user.getAuth());
        }
    }
}
