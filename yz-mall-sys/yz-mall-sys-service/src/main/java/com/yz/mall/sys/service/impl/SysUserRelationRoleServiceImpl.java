package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.advice.exception.BusinessException;
import com.yz.mall.sys.dto.SysUserRelationRoleBindDto;
import com.yz.mall.sys.dto.SysUserRelationRoleQueryDto;
import com.yz.mall.sys.entity.SysUserRelationRole;
import com.yz.mall.sys.mapper.SysUserRelationRoleMapper;
import com.yz.mall.sys.service.SysUserRelationRoleService;
import com.yz.tools.PageFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统-用户与组织关联角色表(SysUserRelationRole)表服务实现类
 *
 * @author yunze
 * @since 2024-11-26 11:46:13
 */
@Service
public class SysUserRelationRoleServiceImpl extends ServiceImpl<SysUserRelationRoleMapper, SysUserRelationRole> implements SysUserRelationRoleService {

    @Override
    public boolean bind(SysUserRelationRoleBindDto dto) {
        if (0 == dto.getType()) {
            return bindRoleForUser(dto.getRelationId(), dto.getRoleIds());
        }
        return bindRoleForOrg(dto.getRelationId(), dto.getRoleIds());
    }

    /**
     * 给用户绑定角色权限
     *
     * @param userId  用户Id
     * @param roleIds 角色Id列表
     * @return 是否绑定成功
     */
    private boolean bindRoleForUser(Long userId, List<Long> roleIds) {
        // 查询出用户拥有的所有角色
        List<Long> alreadyBindRoleIds = this.getRoleIdsByRelationId(userId);
        // 归类出用户已经拥有的哪些角色是需要去除的
        List<Long> removeRoleIds = alreadyBindRoleIds.stream().filter(roleId -> !roleIds.contains(roleId)).collect(Collectors.toList());
        // 归类出哪些角色是新分配给用户的
        List<Long> bindRoleIds = roleIds.stream().filter(roleId -> !alreadyBindRoleIds.contains(roleId)).collect(Collectors.toList());

        // 删除需要去除的角色关联信息
        if (!CollectionUtils.isEmpty(removeRoleIds)) {
            LambdaUpdateWrapper<SysUserRelationRole> removeWrapper = new LambdaUpdateWrapper<>();
            removeWrapper.eq(SysUserRelationRole::getRelationId, userId)
                    .in(SysUserRelationRole::getRoleId, removeRoleIds);
            if (baseMapper.delete(removeWrapper) <= 0) {
                throw new BusinessException("用户分配角色操作失败");
            }
        }

        // 新增新分配的角色关联信息
        if (CollectionUtils.isEmpty(bindRoleIds)) {
            return true;
        }
        List<SysUserRelationRole> addInfos = new ArrayList<>();
        bindRoleIds.forEach(roleId -> {
            SysUserRelationRole info = new SysUserRelationRole();
            info.setId(IdUtil.getSnowflakeNextId());
            info.setRoleId(roleId);
            info.setRelationId(userId);
            addInfos.add(info);
        });
        return super.saveBatch(addInfos);
    }

    private boolean bindRoleForOrg(Long orgId, List<Long> roleIds) {
        return false;
    }

    @Override
    public Page<SysUserRelationRole> page(PageFilter<SysUserRelationRoleQueryDto> filter) {
        LambdaQueryWrapper<SysUserRelationRole> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @DS("slave")
    @Override
    public List<Long> getRoleIdsByRelationId(Long relationId) {
        LambdaQueryWrapper<SysUserRelationRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysUserRelationRole::getRoleId);
        queryWrapper.eq(SysUserRelationRole::getRelationId, relationId);
        List<SysUserRelationRole> userRelationRoleList = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userRelationRoleList)) {
            return Collections.emptyList();
        }
        return userRelationRoleList.stream().map(SysUserRelationRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleCodesByRelationId(Long relationId) {
        return baseMapper.selectRoleCodesByRelationId(relationId);
    }
}

