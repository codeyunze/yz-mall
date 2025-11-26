package com.yz.mall.ss.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.ss.dto.SeataStockAddDto;
import com.yz.mall.ss.dto.SeataStockQueryDto;
import com.yz.mall.ss.dto.SeataStockUpdateDto;
import com.yz.mall.ss.entity.SeataStock;
import jakarta.validation.Valid;


/**
 * 分布式事务-库存表(SeataStock)表服务接口
 *
 * @author yunze
 * @since 2025-11-26 15:29:48
 */
public interface SeataStockService extends IService<SeataStock> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SeataStockAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SeataStockUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SeataStock> page(PageFilter<SeataStockQueryDto> filter);

}

