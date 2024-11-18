package com.yz.cases.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.cases.mall.dto.BaseUserAddDto;
import com.yz.cases.mall.dto.BaseUserQueryDto;
import com.yz.cases.mall.dto.BaseUserUpdateDto;
import com.yz.cases.mall.entity.BaseUser;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

/**
 * 基础-用户(BaseUser)表服务接口
 *
 * @author yunze
 * @since 2024-06-11 23:16:13
 */
public interface BaseUserService extends IService<BaseUser> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(BaseUserAddDto dto);

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

