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
     * 验证码缓存
     * @param captchaId 验证码ID
     */
    public static String captcha(String captchaId) {
        return "auth:captcha:" + captchaId;
    }
}
