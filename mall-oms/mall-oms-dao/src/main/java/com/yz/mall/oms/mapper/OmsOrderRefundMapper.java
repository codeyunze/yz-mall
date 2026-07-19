package com.yz.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.oms.dto.OmsOrderRefundQueryDto;
import com.yz.mall.oms.entity.OmsOrderRefund;
import com.yz.mall.oms.vo.OmsOrderRefundVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单退款申请表(oms_order_refund)数据库访问层
 *
 * @author yunze
 * @since 2026-07-19
 */
@Mapper
public interface OmsOrderRefundMapper extends BaseMapper<OmsOrderRefund> {

    /**
     * 分页查询退款单
     *
     * @param page   分页
     * @param filter 过滤条件
     * @return 分页数据
     */
    Page<OmsOrderRefundVo> selectPageByFilter(Page<Object> page, @Param("filter") OmsOrderRefundQueryDto filter);
}
