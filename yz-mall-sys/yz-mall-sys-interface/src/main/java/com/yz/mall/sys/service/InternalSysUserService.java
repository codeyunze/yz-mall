package com.yz.mall.sys.service;

import java.math.BigDecimal;

/**
 * @author yunze
 * @date 2024/6/19 星期三 23:44
 */
public interface InternalSysUserService {

    /**
     * 扣减账户金额
     *
     * @param userId 扣减用户
     * @param amount 扣减金额
     */
    void deduct(String userId, BigDecimal amount);

    /**
     * 账户充值
     *
     * @param userId 充值用户
     * @param amount 充值金额
     */
    void recharge(String userId, BigDecimal amount);

}
