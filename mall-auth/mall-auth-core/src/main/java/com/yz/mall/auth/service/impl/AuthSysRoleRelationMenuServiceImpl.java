package com.yz.mall.auth.service.impl;

import com.yz.mall.auth.dto.InternalRolePermissionQueryDto;
import com.yz.mall.auth.service.AuthSysRoleRelationMenuService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author yunze
 * @since 2025/7/13 08:44
 */
@Service
public class AuthSysRoleRelationMenuServiceImpl implements AuthSysRoleRelationMenuService {

    @Override
    public Map<String, List<String>> getPermissionsByRoleIds(InternalRolePermissionQueryDto query) {
        return Collections.emptyMap();
    }

}
