package com.yz.nacos.mall.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.nacos.mall.account.entity.TAccount;

import java.math.BigDecimal;

/**
 * 账号信息(TAccount)表服务接口
 *
 * @author yunze
 * @since 2023-11-07 23:21:27
 */
public interface TAccountService extends IService<TAccount> {

    boolean deduct(Long accountId, BigDecimal amount);

}

