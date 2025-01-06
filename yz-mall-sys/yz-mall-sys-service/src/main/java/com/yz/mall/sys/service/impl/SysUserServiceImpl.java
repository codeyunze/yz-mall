package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.web.exception.BusinessException;
import com.yz.mall.web.exception.DataNotExistException;
import com.yz.mall.sys.SysProperties;
import com.yz.mall.sys.dto.*;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.enums.EnableEnum;
import com.yz.mall.sys.mapper.SysUserMapper;
import com.yz.mall.sys.service.*;
import com.yz.mall.sys.vo.BaseUserVo;
import com.yz.mall.sys.vo.SysTreeMenuVo;
import com.yz.mall.sys.vo.SysUserVo;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.RedisCacheKey;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 基础-用户(BaseUser)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysProperties sysProperties;

    private final SysUserRelationOrgService sysUserRelationOrgService;

    private final SysUserRelationRoleService sysUserRelationRoleService;

    private final SysRoleRelationMenuService sysRoleRelationMenuService;

    private final SysMenuService sysMenuService;

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    public SysUserServiceImpl(SysProperties sysProperties
            , SysUserRelationOrgService sysUserRelationOrgService
            , SysUserRelationRoleService sysUserRelationRoleService
            , SysRoleRelationMenuService sysRoleRelationMenuService
            , SysMenuService sysMenuService
            , RedisTemplate<String, Object> defaultRedisTemplate) {
        this.sysProperties = sysProperties;
        this.sysUserRelationOrgService = sysUserRelationOrgService;
        this.sysUserRelationRoleService = sysUserRelationRoleService;
        this.sysRoleRelationMenuService = sysRoleRelationMenuService;
        this.sysMenuService = sysMenuService;
        this.defaultRedisTemplate = defaultRedisTemplate;
    }

    @Override
    public String save(SysUserAddDto dto) {
        SysUser bo = new SysUser();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextIdStr());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysUserUpdateDto dto) {
        SysUser bo = new SysUser();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public boolean updateUserStatusById(Long id) {
        if (sysProperties.getSuperAdminId().equals(id.toString())) {
            throw new BusinessException("禁止操作默认数据");
        }
        SysUser user = baseMapper.selectById(id);
        if (user == null) {
            throw new DataNotExistException();
        }

        user.setStatus(Objects.equals(EnableEnum.ENABLE.get(), user.getStatus()) ? EnableEnum.Disable.get() : EnableEnum.ENABLE.get());
        return baseMapper.updateById(user) > 0;
    }


    @DS("slave")
    @Override
    public Page<SysUserVo> page(PageFilter<SysUserQueryDto> filter) {
        if (filter.getFilter().getOrgId() != null) {
            List<Long> userIds = sysUserRelationOrgService.getUserIdByOrgId(filter.getFilter().getOrgId());
            if (CollectionUtils.isEmpty(userIds)) {
                return new Page<>();
            }
            filter.getFilter().setUserIds(userIds);
        }

        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
    }

    @DS("slave")
    @Override
    public BaseUserVo get(String account) {
        return baseMapper.get(account);
    }

    @Override
    public void deduct(String userId, BigDecimal amount) {
        // TODO: 2024/6/27 星期四 yunze 加锁
        SysUser user = baseMapper.selectById(userId);
        if (user.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("账户余额不足");
        }
        // 扣减余额
        Integer deducted = baseMapper.deduct(userId, amount);
        if (deducted == 0) {
            throw new BusinessException("余额扣减失败");
        }
    }

    @Override
    public void recharge(String userId, BigDecimal amount) {
        Integer recharged = baseMapper.recharge(userId, amount);
        if (recharged == 0) {
            throw new BusinessException("账户充值失败");
        }
    }

    @DS("slave")
    @Override
    public InternalLoginInfoDto checkLogin(InternalSysUserCheckLoginDto dto) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone, dto.getPhone());
        SysUser user = baseMapper.selectOne(queryWrapper);
        if (user == null || !dto.getPassword().equals(user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        InternalLoginInfoDto loginInfo = new InternalLoginInfoDto();
        BeanUtils.copyProperties(user, loginInfo);
        return loginInfo;
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        List<Long> roleIds = sysUserRelationRoleService.getRoleIdsByRelationId(userId);
        if (!CollectionUtils.isEmpty(roleIds)) {
            // 缓存用户所拥有的角色信息
            String cacheKey = RedisCacheKey.permissionRole(String.valueOf(userId));
            defaultRedisTemplate.delete(cacheKey);
            roleIds.forEach(role -> defaultRedisTemplate.opsForList().rightPush(cacheKey, role));
            defaultRedisTemplate.expire(cacheKey, 86400, TimeUnit.SECONDS);
        }
        return roleIds;
    }

    @DS("slave")
    @Override
    public List<SysTreeMenuVo> getUserMenus(Long userId) {
        List<Long> roleIds = sysUserRelationRoleService.getRoleIdsByRelationId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        // 菜单Id列表
        List<Long> menuIds = sysRoleRelationMenuService.getMenuIdsByRoleIds(roleIds);
        // 查询指定菜单的详细信息
        SysMenuQueryDto menuQueryDto = new SysMenuQueryDto();
        menuQueryDto.setMenuIds(menuIds);
        List<SysMenu> menus = sysMenuService.list(menuQueryDto);
        // 解析菜单信息
        return sysMenuService.menusInfoProcessor(menus, 0L, roleIds);
    }

    @Override
    public boolean resetPassword(SysUserResetPasswordDto dto) {
        SysUser user = baseMapper.selectById(dto.getId());
        // user.setPassword(RandomUtils.randomPassword());
        user.setPassword(dto.getPassword());
        return baseMapper.updateById(user) > 0;
    }
}

