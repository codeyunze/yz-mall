package com.yz.mall.sys.service.impl;

import com.yz.advice.exception.BusinessException;
import com.yz.mall.sys.feign.InternalSysRoleRelationMenuFeign;
import com.yz.mall.sys.service.InternalSysRoleRelationMenuService;
import com.yz.tools.IdsDto;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
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
public class InternalSysRoleRelationMenuServiceImpl implements InternalSysRoleRelationMenuService {

    private final InternalSysRoleRelationMenuFeign feign;

    public InternalSysRoleRelationMenuServiceImpl(InternalSysRoleRelationMenuFeign feign) {
        this.feign = feign;
    }

    @Override
    public Map<String, List<String>> getPermissionsByRoleIds(List<Long> roleIds) {
        Result<Map<String, List<String>>> result = feign.getPermissionsByRoleIds(new IdsDto(roleIds));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }
}
