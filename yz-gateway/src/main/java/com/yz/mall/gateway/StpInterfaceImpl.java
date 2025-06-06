package com.yz.mall.gateway;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 * @author yunze
 * @date 2024/11/15 16:09
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 返回此 loginId 拥有的权限列表
        return Arrays.asList("admin", "unqid", "user", "oms");
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        // 返回此 loginId 拥有的角色列表
        return Collections.singletonList("admin");
    }
}
