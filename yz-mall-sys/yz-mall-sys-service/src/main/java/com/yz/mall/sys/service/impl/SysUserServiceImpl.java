package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.advice.exception.BusinessException;
import com.yz.advice.exception.DataNotExistException;
import com.yz.mall.sys.config.SysProperties;
import com.yz.mall.sys.dto.*;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.enums.EnableEnum;
import com.yz.mall.sys.mapper.BaseUserMapper;
import com.yz.mall.sys.service.SysUserRelationOrgService;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.BaseUserVo;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 基础-用户(BaseUser)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<BaseUserMapper, SysUser> implements SysUserService {

    private final SysProperties sysProperties;

    private final SysUserRelationOrgService sysUserRelationOrgService;

    public SysUserServiceImpl(SysProperties sysProperties, SysUserRelationOrgService sysUserRelationOrgService) {
        this.sysProperties = sysProperties;
        this.sysUserRelationOrgService = sysUserRelationOrgService;
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
    public Page<SysUser> page(PageFilter<SysUserQueryDto> filter) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(filter.getFilter().getPhone()), SysUser::getPhone, filter.getFilter().getPhone());
        queryWrapper.like(StringUtils.hasText(filter.getFilter().getEmail()), SysUser::getEmail, filter.getFilter().getEmail());

        if (filter.getFilter().getOrgId() != null) {
            List<Long> userIds = sysUserRelationOrgService.getUserIdByOrgId(filter.getFilter().getOrgId());
            if (CollectionUtils.isEmpty(userIds)) {
                return new Page<>();
            }
            queryWrapper.in(SysUser::getId, userIds);
        }

        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
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
}

