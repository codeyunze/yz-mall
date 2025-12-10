package com.yz.mall.redis;

/**
 * 缓存信息 Key 值同一管理
 *
 * @author yunze
 * @date 2024/7/11 13:00
 */
public class RedisCacheKey {

    /**
     * 获取序列号信息的 key
     *
     * @param prefix 序列号前缀
     * @return 序列号信息的 key
     */
    public static String objUnqid(String prefix) {
        return "obj:unqid:" + prefix;
    }

    /**
     * 权限信息-用户所拥有的角色
     */
    public static String permissionRole(String userId) {
        return "permission:role-by-user:" + userId;
    }


    /**
     * 权限信息缓存 key
     *
     * @param menuTypeKey 菜单类型 (button或api)
     * @param roleId      角色 Id
     */
    public static String permission(String menuTypeKey, String roleId) {
        return "permission:" + menuTypeKey.toLowerCase() + "-by-role:" + roleId;
    }

    /**
     * 登录用户信息
     *
     * @param userId 用户信息
     */
    public static String loginInfo(Long userId) {
        return "Authorization:login:info:" + userId;
    }

    /**
     * 重复提交校验缓存
     * @param key 校验 key
     */
    public static String repeatSubmit(String key) {
        return "system:repeat-submit:" + key;
    }

    /**
     * 数据字典缓存
     * @param key 数据字典 key
     */
    public static String dictionary(String key) {
        return "system:dictionary:" + key;
    }

    /**
     * 接口权限映射缓存
     * 存储格式：key = "permission:api-mapping:服务名:URI", value = 权限标识
     * 
     * @param serviceName 服务名称，如：mall-sys, mall-oms
     * @param uri 接口URI，如：/sys/dictionary/add
     * @return Redis key
     */
    public static String apiPermissionMapping(String serviceName, String uri) {
        return "permission:api-mapping:" + serviceName + ":" + uri;
    }

    /**
     * 获取服务所有接口权限映射的匹配模式
     * 
     * @param serviceName 服务名称
     * @return Redis key 匹配模式
     */
    public static String apiPermissionMappingPattern(String serviceName) {
        return "permission:api-mapping:" + serviceName + ":*";
    }
}
