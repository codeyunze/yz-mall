package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.enums.EnableEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.base.exception.DataNotExistException;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.sys.SysProperties;
import com.yz.mall.sys.dto.*;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.mapper.SysUserMapper;
import com.yz.mall.sys.service.SysMenuService;
import com.yz.mall.sys.service.SysRoleRelationMenuService;
import com.yz.mall.sys.service.SysUserRelationRoleService;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.ExtendLoginInfoVo;
import com.yz.mall.sys.vo.MenuByRoleVo;
import com.yz.mall.sys.vo.SysTreeMenuVo;
import com.yz.mall.sys.vo.SysUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yunze
 * @since 2025/7/10 10:51
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysProperties sysProperties;

    private final SysMenuService sysMenuService;

    private final SysUserRelationRoleService sysUserRelationRoleService;

    private final SysRoleRelationMenuService sysRoleRelationMenuService;

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    public SysUserServiceImpl(SysProperties sysProperties
            , SysMenuService sysMenuService
            , SysUserRelationRoleService sysUserRelationRoleService
            , SysRoleRelationMenuService sysRoleRelationMenuService
            , RedisTemplate<String, Object> defaultRedisTemplate) {
        this.sysProperties = sysProperties;
        this.sysMenuService = sysMenuService;
        this.sysUserRelationRoleService = sysUserRelationRoleService;
        this.sysRoleRelationMenuService = sysRoleRelationMenuService;
        this.defaultRedisTemplate = defaultRedisTemplate;
    }

    @Override
    public Page<SysUserVo> page(long current, long size, SysUserQueryDto filter) {
        log.info("用户信息实现类");
        return baseMapper.selectPage(new Page<>(current, size), filter);
    }

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

        // 获取各菜单所对应可以访问的角色Id
        List<MenuByRoleVo> roleByMenuList = sysRoleRelationMenuService.getRoleIdByMenuIds(menuIds);
        // Map<菜单Id, List<角色Id>>
        Map<Long, List<Long>> roleByMenuMap = roleByMenuList.stream()
                .collect(Collectors.groupingBy(
                        MenuByRoleVo::getMenuId,
                        Collectors.mapping(MenuByRoleVo::getRoleId, Collectors.toList())
                ));

        // 解析菜单信息
        return sysMenuService.menusInfoProcessor(menus, 0L, roleByMenuMap);
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

    @Override
    public ExtendLoginInfoVo getUserInfoById(Long userId) {
        SysUser user = baseMapper.selectById(userId);
        ExtendLoginInfoVo vo = new ExtendLoginInfoVo();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public boolean resetPassword(SysUserResetPasswordDto dto) {
        SysUser user = baseMapper.selectById(dto.getId());
        // user.setPassword(RandomUtils.randomPassword());
        user.setPassword(dto.getPassword());
        return baseMapper.updateById(user) > 0;
    }

    @Override
    public Long save(SysUserAddDto dto) {
        SysUser bo = new SysUser();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deduct(Long userId, BigDecimal amount) {
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
    public void recharge(Long userId, BigDecimal amount) {
        Integer recharged = baseMapper.recharge(userId, amount);
        if (recharged == 0) {
            throw new BusinessException("账户充值失败");
        }
    }
}
