package com.yz.mall.oms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.oms.dto.OmsOrderItemDto;
import com.yz.mall.oms.dto.OmsOrderRelationProductAddDto;
import com.yz.mall.oms.dto.OmsOrderProductRelationQueryDto;
import com.yz.mall.oms.dto.OmsOrderRelationProductUpdateDto;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import com.yz.tools.PageFilter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 订单商品关联表(OmsOrderProductRelation)表服务接口
 *
 * @author yunze
 * @since 2024-06-18 12:51:39
 */
public interface OmsOrderProductRelationService extends IService<OmsOrderRelationProduct> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(OmsOrderRelationProductAddDto dto);

    /**
     * 批量新增数据
     *
     * @param orderId 订单Id
     * @param array 新增基础数据
     * @return 是否新增成功
     */
    boolean saveBatch(@NotBlank(message = "订单Id不能为空") Long orderId, @Valid List<OmsOrderItemDto> array);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid OmsOrderRelationProductUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<OmsOrderRelationProduct> page(PageFilter<OmsOrderProductRelationQueryDto> filter);

}

