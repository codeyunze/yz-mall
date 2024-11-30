package com.yz.mall.sys.service;

import com.yz.mall.sys.dto.InternalLoginInfoDto;
import com.yz.mall.sys.dto.InternalSysUserCheckLoginDto;
import com.yz.mall.sys.vo.InternalSysUserRoleVo;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

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

    /**
     * 手机号登录校验
     */
    InternalLoginInfoDto checkLogin(@Valid InternalSysUserCheckLoginDto dto);

    /**
     * 获取指定用户所拥有的角色
     *
     * @param userId 用户Id
     * @return 用户所拥有的角色
     */
    List<Long> getUserRoles(Long userId);
}
