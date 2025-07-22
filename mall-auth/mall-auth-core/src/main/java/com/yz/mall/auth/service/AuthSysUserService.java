package com.yz.mall.auth.service;

import com.yz.mall.auth.dto.AuthUserBaseInfoDto;
import com.yz.mall.auth.dto.AuthSysUserCheckLoginDto;
import com.yz.mall.auth.dto.RegisterDto;
import com.yz.mall.auth.vo.AuthUserInfoVo;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 内部开放接口: 系统管理-用户信息
 *
 * @author yunze
 * @date 2024/6/19 星期三 23:44
 */
public interface AuthSysUserService {

    /**
     * 手机号登录校验
     */
    AuthUserBaseInfoDto checkLogin(@Valid AuthSysUserCheckLoginDto dto);

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
    AuthUserInfoVo getUserInfoById(Long userId);

    /**
     * 新增用户
     *
     * @param dto 新增用户信息数据
     * @return 主键Id
     */
    Long add(@Valid RegisterDto dto);
}
