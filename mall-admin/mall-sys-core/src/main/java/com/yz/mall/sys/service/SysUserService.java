package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysUserAddDto;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.dto.SysUserResetPasswordDto;
import com.yz.mall.sys.dto.SysUserUpdateDto;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.vo.InternalLoginInfoVo;
import com.yz.mall.sys.vo.SysTreeMenuVo;
import com.yz.mall.sys.vo.SysUserVo;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

/**
 * 基础-用户(BaseUser)表服务接口
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysUserVo> page(long current, long size, SysUserQueryDto filter);

    /**
     * 获取请求用户可访问的菜单信息
     *
     * @param userId 用户Id {@link SysUser#getId()}
     * @return 用户可访问的菜单
     */
    List<SysTreeMenuVo> getUserMenus(Long userId);

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
    InternalLoginInfoVo getUserInfoById(Long userId);

    /**
     * 重置用户密码
     *
     * @param dto 用户Id和用户密码
     * @return 是否重置成功
     */
    boolean resetPassword(SysUserResetPasswordDto dto);

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysUserAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysUserUpdateDto dto);

    /**
     * 切换指定用户的状态
     *
     * @param id 用户Id
     * @return 是否操作成功
     */
    boolean updateUserStatusById(Long id);

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
}

