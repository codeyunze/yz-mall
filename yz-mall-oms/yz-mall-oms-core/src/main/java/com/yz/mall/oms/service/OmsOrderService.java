package com.yz.mall.oms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.oms.dto.OmsOrderAddDto;
import com.yz.mall.oms.dto.OmsOrderGenerateDto;
import com.yz.mall.oms.dto.OmsOrderQueryDto;
import com.yz.mall.oms.dto.OmsOrderUpdateDto;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

/**
 * 订单信息表(OmsOrder)表服务接口
 *
 * @author yunze
 * @since 2024-06-18 12:49:55
 */
public interface OmsOrderService extends IService<OmsOrder> {

    /**
     * 生成订单
     * @param dto 订单与商品信息
     * @return 订单Id
     */
    Long generateOrder(OmsOrderGenerateDto dto);

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(OmsOrderAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid OmsOrderUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<OmsOrder> page(PageFilter<OmsOrderQueryDto> filter);

}

