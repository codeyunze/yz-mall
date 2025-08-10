package com.yz.mall.sys.service.impl;

import com.yz.mall.base.enums.EnableEnum;
import com.yz.mall.base.enums.SexEnum;
import com.yz.mall.sys.dto.ExtendSysUserAddDto;
import com.yz.mall.sys.dto.SysUserAddDto;
import com.yz.mall.sys.service.ExtendSysUserService;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.ExtendLoginInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 内部暴露service实现类: 用户信息
 *
 * @author yunze
 * @date 2024/6/19 星期三 23:49
 */
@Service
public class ExtendSysUserServiceImpl implements ExtendSysUserService {

    private final SysUserService service;

    public ExtendSysUserServiceImpl(SysUserService service) {
        this.service = service;
    }

    @Override
    public void deduct(Long userId, BigDecimal amount) {
        this.service.deduct(userId, amount);
    }

    @Override
    public void recharge(Long userId, BigDecimal amount) {
        this.service.recharge(userId, amount);
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        return service.getUserRoles(userId);
    }

    @Override
    public ExtendLoginInfoVo getUserInfoById(Long userId) {
        return service.getUserInfoById(userId);
    }

    @Override
    public Long add(ExtendSysUserAddDto dto) {
        SysUserAddDto userAddDto = new SysUserAddDto();
        BeanUtils.copyProperties(dto, userAddDto);
        userAddDto.setSex(SexEnum.MALE.get());
        userAddDto.setStatus(EnableEnum.ENABLE.get());
        return service.save(userAddDto);
    }
}
