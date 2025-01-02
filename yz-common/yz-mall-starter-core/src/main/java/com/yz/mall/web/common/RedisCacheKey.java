package com.yz.mall.web.common;

/**
 * 缓存信息Key值同一管理
 *
 * @author yunze
 * @date 2024/7/11 13:00
 */
public class RedisCacheKey {

    /**
     * 获取序列号信息的key
     *
     * @param prefix 序列号前缀
     * @return 序列号信息的key
     */
    public static String objUnqid(String prefix) {
        return "obj:unqid:" + prefix;
    }

    /**
     * 权限信息-角色
     */
    public static String permissionRole(String userId) {
        return "permission:role-by-user:" + userId;
    }


    /**
     * 权限信息缓存key
     *
     * @param menuTypeKey 菜单类型 (button或api)
     * @param roleId      角色Id
     */
    public static String permission(String menuTypeKey, String roleId) {
        return "permission:" + menuTypeKey.toLowerCase() + "-by-role:" + roleId;
    }

    /**
     * 登录用户信息
     *
     * @param userId 用户信息
     */
    public static String loginInfo(String userId) {
        return "Authorization:login:info:" + userId;
    }

    /**
     * 重复提交校验缓存
     * @param key 校验key
     */
    public static String repeatSubmit(String key) {
        return "system:repeat-submit:" + key;
    }
}
