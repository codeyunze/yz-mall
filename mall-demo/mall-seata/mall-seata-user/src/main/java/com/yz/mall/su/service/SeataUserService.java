package com.yz.mall.su.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.su.dto.SeataUserAddDto;
import com.yz.mall.su.dto.SeataUserQueryDto;
import com.yz.mall.su.dto.SeataUserUpdateDto;
import com.yz.mall.su.entity.SeataUser;
import jakarta.validation.Valid;


/**
 * 分布式事务-用户表(SeataUser)表服务接口
 *
 * @author yunze
 * @since 2024-06-18 00:00:00
 */
public interface SeataUserService extends IService<SeataUser> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SeataUserAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SeataUserUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SeataUser> page(PageFilter<SeataUserQueryDto> filter);

    /**
     * 扣减用户余额
     * @param userId 用户ID
     * @param amount 扣减金额（分）
     * @return 是否扣减成功
     */
    boolean decreaseBalance(Long userId, Long amount);
}