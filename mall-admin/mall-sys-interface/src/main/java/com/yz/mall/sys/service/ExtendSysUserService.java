package com.yz.mall.sys.service;

import com.yz.mall.base.IdsDto;
import com.yz.mall.sys.dto.ExtendSysUserAddDto;
import com.yz.mall.sys.vo.ExtendLoginInfoVo;
import com.yz.mall.sys.vo.ExtendSysUserSlimVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 内部开放接口: 系统管理-用户信息
 *
 * @author yunze
 * @date 2024/6/19 星期三 23:44
 */
public interface ExtendSysUserService {

    /**
     * 扣减账户金额
     *
     * @param userId 扣减用户
     * @param amount 扣减金额
     */
    void deduct(Long userId, BigDecimal amount);

    /**
     * 账户充值
     *
     * @param userId 充值用户
     * @param amount 充值金额
     */
    void recharge(Long userId, BigDecimal amount);

    /**
     * 获取指定用户所拥有的角色
     *
     * @param userId 用户Id
     * @return 用户所拥有的角色
     */
    List<Long> getUserRoles(Long userId);

    /**
     * 获取用户信息
     *
     * @param userId 用户Id
     * @return 用户信息
     */
    ExtendLoginInfoVo getUserInfoById(Long userId);

    /**
     * 新增用户
     *
     * @param dto 新增用户信息数据
     * @return 主键Id
     */
    Long add(@Valid ExtendSysUserAddDto dto);

    /**
     * 获取用户基础信息
     *
     * @param idsDto 用户 Id列表
     * @return 用户信息
     */
    Map<Long, ExtendSysUserSlimVo> getUserSlimByIds(@NotNull IdsDto<Long> idsDto);
}
