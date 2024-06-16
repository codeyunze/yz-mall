package com.yz.cases.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.cases.mall.dto.SysDatasourceAddDto;
import com.yz.cases.mall.dto.SysDatasourceQueryDto;
import com.yz.cases.mall.dto.SysDatasourceUpdateDto;
import com.yz.cases.mall.entity.SysDatasource;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

/**
 * 系统-数据源信息(SysDatasource)表服务接口
 *
 * @author yunze
 * @since 2024-06-12 11:02:09
 */
public interface SysDatasourceService extends IService<SysDatasource> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysDatasourceAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysDatasourceUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysDatasource> page(PageFilter<SysDatasourceQueryDto> filter);

}

