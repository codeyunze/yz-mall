package com.yz.mall.so.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.so.dto.SeataOrderAddDto;
import com.yz.mall.so.dto.SeataOrderQueryDto;
import com.yz.mall.so.dto.SeataOrderUpdateDto;
import com.yz.mall.so.entity.SeataOrder;
import jakarta.validation.Valid;


/**
 * 分布式事务-订单表(SeataOrder)表服务接口
 *
 * @author yunze
 * @since 2025-11-24 22:40:03
 */
public interface SeataOrderService extends IService<SeataOrder> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SeataOrderAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SeataOrderUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SeataOrder> page(PageFilter<SeataOrderQueryDto> filter);

}

