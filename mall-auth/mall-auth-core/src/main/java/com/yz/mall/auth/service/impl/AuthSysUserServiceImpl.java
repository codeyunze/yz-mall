package com.yz.mall.auth.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yz.mall.auth.dto.AuthUserBaseInfoDto;
import com.yz.mall.auth.dto.AuthSysUserCheckLoginDto;
import com.yz.mall.auth.dto.RegisterDto;
import com.yz.mall.auth.service.AuthSysUserService;
import com.yz.mall.auth.vo.AuthUserInfoVo;
import com.yz.mall.base.enums.EnableEnum;
import com.yz.mall.base.enums.SexEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.base.exception.DuplicateException;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.entity.SysUserRelationRole;
import com.yz.mall.sys.mapper.SysUserMapper;
import com.yz.mall.sys.mapper.SysUserRelationRoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yunze
 * @since 2025/7/13 08:39
 */
@Service
public class AuthSysUserServiceImpl implements AuthSysUserService {

    private final SysUserMapper userMapper;

    private final SysUserRelationRoleMapper userRelationRoleMapper;

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    public AuthSysUserServiceImpl(SysUserMapper userMapper, SysUserRelationRoleMapper userRelationRoleMapper, RedisTemplate<String, Object> defaultRedisTemplate) {
        this.userMapper = userMapper;
        this.userRelationRoleMapper = userRelationRoleMapper;
        this.defaultRedisTemplate = defaultRedisTemplate;
    }

    @Override
    public AuthUserBaseInfoDto checkLogin(AuthSysUserCheckLoginDto dto) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone, dto.getPhone());
        SysUser user = userMapper.selectOne(queryWrapper);
        if (user == null || !dto.getPassword().equals(user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        AuthUserBaseInfoDto loginInfo = new AuthUserBaseInfoDto();
        BeanUtils.copyProperties(user, loginInfo);
        return loginInfo;
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        LambdaQueryWrapper<SysUserRelationRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysUserRelationRole::getRoleId);
        queryWrapper.eq(SysUserRelationRole::getRelationId, userId);
        List<SysUserRelationRole> userRelationRoleList = userRelationRoleMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userRelationRoleList)) {
            return Collections.emptyList();
        }

        List<Long> roleIds = userRelationRoleList.stream().map(SysUserRelationRole::getRoleId).collect(Collectors.toList());
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
    public AuthUserInfoVo getUserInfoById(Long userId) {
        SysUser user = userMapper.selectById(userId);
        AuthUserInfoVo vo = new AuthUserInfoVo();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public Long add(RegisterDto dto) {
        // 账号重复校验
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone, dto.getPhone());
        SysUser user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            throw new DuplicateException("手机号已被使用");
        }

        SysUser bo = new SysUser();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setSex(SexEnum.MALE.get());
        bo.setStatus(EnableEnum.ENABLE.get());
        if (userMapper.insert(bo) > 0) {
            return bo.getId();
        }
        throw new BusinessException("用户注册失败");
    }
}
