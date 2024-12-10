package com.yz.mall.sys.impl;

import com.yz.mall.sys.dto.InternalLoginInfoDto;
import com.yz.mall.sys.dto.InternalSysUserCheckLoginDto;
import com.yz.mall.sys.service.InternalSysUserService;
import com.yz.mall.sys.service.SysUserService;
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
public class InternalSysUserServiceImpl implements InternalSysUserService {

    private final SysUserService service;

    public InternalSysUserServiceImpl(SysUserService service) {
        this.service = service;
    }

    @Override
    public void deduct(String userId, BigDecimal amount) {
        this.service.deduct(userId, amount);
    }

    @Override
    public void recharge(String userId, BigDecimal amount) {
        this.service.recharge(userId, amount);
    }

    @Override
    public InternalLoginInfoDto checkLogin(InternalSysUserCheckLoginDto dto) {
        return service.checkLogin(dto);
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        return service.getUserRoles(userId);
    }
}
