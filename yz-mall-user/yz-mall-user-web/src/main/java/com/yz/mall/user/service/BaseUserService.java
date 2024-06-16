package com.yz.mall.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.user.dto.BaseUserAddDto;
import com.yz.mall.user.dto.BaseUserQueryDto;
import com.yz.mall.user.dto.BaseUserUpdateDto;
import com.yz.mall.user.entity.BaseUser;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

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

}

