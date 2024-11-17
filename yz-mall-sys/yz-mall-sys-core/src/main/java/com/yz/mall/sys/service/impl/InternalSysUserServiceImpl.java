package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.service.InternalSysUserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
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
}
