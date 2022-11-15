package com.yz.auth.business.baseRole.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.auth.business.baseRole.entity.BaseRole;
import com.yz.auth.business.baseRole.mapper.BaseRoleMapper;
import com.yz.auth.business.baseRole.service.BaseRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 基础-角色表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2022-09-13
 */
@Service
public class BaseRoleServiceImpl extends ServiceImpl<BaseRoleMapper, BaseRole> implements BaseRoleService {

    @Override
    public List<String> queryUserOwnedRoleCodes(String userId) {
        LambdaQueryWrapper<BaseRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(BaseRole::getUserId, BaseRole::getRoleId, BaseRole::getRoleCode, BaseRole::getRoleName);
        queryWrapper.eq(BaseRole::getUserId, userId);
        queryWrapper.orderByAsc(BaseRole::getId);
        List<BaseRole> baseRoles = baseMapper.selectList(queryWrapper);
        return baseRoles != null ? baseRoles.stream().map(BaseRole::getRoleCode).collect(Collectors.toList()) : null;
    }
}
