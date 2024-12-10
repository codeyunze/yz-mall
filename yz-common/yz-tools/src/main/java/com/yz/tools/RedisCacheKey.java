package com.yz.tools;

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
        return "permission:role:" + userId;
    }

    /**
     * 权限信息-按钮
     */
    public static String permissionButton(String roleId) {
        return "permission:button:" + roleId;
    }

    /**
     * 权限信息-接口
     */
    public static String permissionApi(String roleId) {
        return "permission:api:" + roleId;
    }
}
