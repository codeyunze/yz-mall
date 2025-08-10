package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.dto.ExtendRolePermissionQueryDto;
import com.yz.mall.sys.feign.ExtendSysRoleRelationMenuFeign;
import com.yz.mall.sys.service.ExtendSysRoleRelationMenuService;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 内部暴露service实现类: 角色关联菜单
 *
 * @author yunze
 * @date 2024/12/9 19:42
 */
@Service
public class ExtendSysRoleRelationMenuServiceImpl implements ExtendSysRoleRelationMenuService {

    private final ExtendSysRoleRelationMenuFeign feign;

    public ExtendSysRoleRelationMenuServiceImpl(ExtendSysRoleRelationMenuFeign feign) {
        this.feign = feign;
    }

    @Override
    public Map<String, List<String>> getPermissionsByRoleIds(ExtendRolePermissionQueryDto query) {
        Result<Map<String, List<String>>> result = feign.getPermissionsByRoleIds(query);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }
}
