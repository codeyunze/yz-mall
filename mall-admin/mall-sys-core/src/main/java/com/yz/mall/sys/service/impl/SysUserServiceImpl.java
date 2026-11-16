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
import com.yz.mall.sys.service.ExtendSysFilesService;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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

    private final ExtendSysFilesService extendSysFilesService;

    public SysUserServiceImpl(SysProperties sysProperties
            , SysMenuService sysMenuService
            , SysUserRelationRoleService sysUserRelationRoleService
            , SysRoleRelationMenuService sysRoleRelationMenuService
            , RedisTemplate<String, Object> defaultRedisTemplate
            , ExtendSysFilesService extendSysFilesService) {
        this.sysProperties = sysProperties;
        this.sysMenuService = sysMenuService;
        this.sysUserRelationRoleService = sysUserRelationRoleService;
        this.sysRoleRelationMenuService = sysRoleRelationMenuService;
        this.defaultRedisTemplate = defaultRedisTemplate;
        this.extendSysFilesService = extendSysFilesService;
    }

    @Override
    public Page<SysUserVo> page(long current, long size, SysUserQueryDto filter) {
        log.info("用户信息实现类");
        Page<SysUserVo> result = baseMapper.selectPage(new Page<>(current, size), filter);
        fillAvatarPreview(result.getRecords());
        return result;
    }

    /**
     * 有头像文件 id 时，用预签名预览地址覆盖 {@code avatar}。
     * 签名失败时保留库里原来的头像地址。
     *
     * @param records 分页用户
     */
    private void fillAvatarPreview(List<SysUserVo> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        List<SysUserVo> withFile = new ArrayList<>();
        List<Long> fileIds = new ArrayList<>();
        for (SysUserVo record : records) {
            if (record.getAvatarFileId() != null) {
                withFile.add(record);
                fileIds.add(record.getAvatarFileId());
            }
        }
        if (fileIds.isEmpty()) {
            return;
        }
        List<String> urls = extendSysFilesService.getFilePreviewByFileIds(fileIds);
        for (int i = 0; i < withFile.size(); i++) {
            String url = urls != null && i < urls.size() ? urls.get(i) : null;
            if (StringUtils.hasText(url)) {
                withFile.get(i).setAvatar(url);
            }
        }
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
        if (user.getAvatarFileId() != null) {
            List<String> urls = extendSysFilesService.getFilePreviewByFileIds(Collections.singletonList(user.getAvatarFileId()));
            if (!CollectionUtils.isEmpty(urls) && StringUtils.hasText(urls.get(0))) {
                vo.setAvatar(urls.get(0));
            }
        }
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
    public void deduct(Long userId, Long amount) {
        // TODO: 2024/6/27 星期四 yunze 加锁
        SysUser user = baseMapper.selectById(userId);
        if (user.getBalance() < amount) {
            throw new BusinessException("账户余额不足");
        }
        // 扣减余额
        Integer deducted = baseMapper.deduct(userId, amount);
        if (deducted == 0) {
            throw new BusinessException("余额扣减失败");
        }
    }

    @Override
    public void recharge(Long userId, Long amount) {
        Integer recharged = baseMapper.recharge(userId, amount);
        if (recharged == 0) {
            throw new BusinessException("账户充值失败");
        }
    }
}
