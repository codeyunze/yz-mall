package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.advice.exception.BusinessException;
import com.yz.mall.sys.dto.SysUserAddDto;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.dto.SysUserUpdateDto;
import com.yz.mall.sys.mapper.BaseUserMapper;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.BaseUserVo;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 基础-用户(BaseUser)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<BaseUserMapper, SysUser> implements SysUserService {

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


    @DS("slave")
    @Override
    public Page<SysUser> page(PageFilter<SysUserQueryDto> filter) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(filter.getFilter().getPhone()), SysUser::getPhone, filter.getFilter().getPhone());
        queryWrapper.like(StringUtils.hasText(filter.getFilter().getEmail()), SysUser::getEmail, filter.getFilter().getEmail());
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
}

