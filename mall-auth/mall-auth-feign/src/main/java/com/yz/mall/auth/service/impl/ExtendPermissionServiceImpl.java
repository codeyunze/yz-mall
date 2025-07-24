package com.yz.mall.auth.service.impl;

import com.yz.mall.auth.dto.AuthRolePermissionQueryDto;
import com.yz.mall.auth.feign.ExtendPermissionFeign;
import com.yz.mall.auth.service.ExtendPermissionService;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yunze
 * @since 2025/7/24 14:13
 */
@Service
public class ExtendPermissionServiceImpl implements ExtendPermissionService {

    private final ExtendPermissionFeign permissionFeign;

    public ExtendPermissionServiceImpl(ExtendPermissionFeign permissionFeign) {
        this.permissionFeign = permissionFeign;
    }

    @Override
    public Map<String, List<String>> getPermissionsByRoleIds(AuthRolePermissionQueryDto query) {
        Result<Map<String, List<String>>> result = permissionFeign.getPermissionsByRoleIds(query);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }
}
