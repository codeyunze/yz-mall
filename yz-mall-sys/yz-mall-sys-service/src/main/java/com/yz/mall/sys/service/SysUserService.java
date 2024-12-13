package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.*;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.vo.BaseUserVo;
import com.yz.mall.sys.vo.SysTreeMenuVo;
import com.yz.mall.sys.vo.SysUserVo;
import com.yz.tools.PageFilter;

import javax.validation.Valid;
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
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    String save(SysUserAddDto dto);

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
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysUserVo> page(PageFilter<SysUserQueryDto> filter);

    /**
     * 获取用户信息
     *
     * @param account 登录账号（手机号|邮箱）
     * @return 用户信息
     */
    BaseUserVo get(String account);

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

    /**
     * 获取请求用户可访问的菜单信息
     *
     * @param userId 用户Id {@link SysUser#getId()}
     * @return 用户可访问的菜单
     */
    List<SysTreeMenuVo> getUserMenus(Long userId);
}

