package com.yz.mall.oms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.oms.dto.OmsOrderRefundQueryDto;
import com.yz.mall.oms.dto.OmsRefundApplyDto;
import com.yz.mall.oms.dto.OmsRefundAuditDto;
import com.yz.mall.oms.entity.OmsOrderRefund;
import com.yz.mall.oms.vo.OmsOrderRefundVo;

/**
 * 订单退款申请服务
 *
 * @author yunze
 * @since 2026-07-19
 */
public interface OmsOrderRefundService extends IService<OmsOrderRefund> {

    /**
     * 用户申请退款（仅待发货订单）
     *
     * @param userId 申请人
     * @param dto    申请入参
     * @return 退款单id
     */
    Long apply(Long userId, OmsRefundApplyDto dto);

    /**
     * 审核退款（通过则退余额并回补库存）
     *
     * @param auditUserId 审核人
     * @param dto         审核入参
     * @return 是否成功
     */
    boolean audit(Long auditUserId, OmsRefundAuditDto dto);

    /**
     * 分页查询退款单
     *
     * @param filter 分页过滤
     * @return 分页结果
     */
    Page<OmsOrderRefundVo> page(PageFilter<OmsOrderRefundQueryDto> filter);
}
