package com.yz.mall.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.user.dto.BaseUserAddDto;
import com.yz.mall.user.dto.BaseUserQueryDto;
import com.yz.mall.user.dto.BaseUserUpdateDto;
import com.yz.mall.user.entity.BaseUser;
import com.yz.mall.user.vo.BaseUserVo;
import com.yz.tools.PageFilter;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * 基础-用户(BaseUser)表服务接口
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
public interface BaseUserService extends IService<BaseUser> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    String save(BaseUserAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid BaseUserUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<BaseUser> page(PageFilter<BaseUserQueryDto> filter);

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


}

