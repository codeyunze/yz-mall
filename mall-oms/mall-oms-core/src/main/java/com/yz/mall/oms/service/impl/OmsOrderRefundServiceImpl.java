package com.yz.mall.oms.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.base.exception.DataNotExistException;
import com.yz.mall.oms.dto.OmsOrderRefundQueryDto;
import com.yz.mall.oms.dto.OmsRefundApplyDto;
import com.yz.mall.oms.dto.OmsRefundAuditDto;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.mall.oms.entity.OmsOrderRefund;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import com.yz.mall.oms.enums.OmsOrderStatusEnum;
import com.yz.mall.oms.enums.OmsRefundStatusEnum;
import com.yz.mall.oms.mapper.OmsOrderMapper;
import com.yz.mall.oms.mapper.OmsOrderRefundMapper;
import com.yz.mall.oms.service.OmsOrderRefundService;
import com.yz.mall.oms.service.OmsOrderRelationProductService;
import com.yz.mall.oms.vo.OmsOrderRefundVo;
import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.service.ExtendPmsStockService;
import com.yz.mall.serial.service.ExtendSerialService;
import com.yz.mall.sys.service.ExtendSysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 订单退款申请服务实现
 *
 * @author yunze
 * @since 2026-07-19
 */
@Service
public class OmsOrderRefundServiceImpl extends ServiceImpl<OmsOrderRefundMapper, OmsOrderRefund> implements OmsOrderRefundService {

    private final OmsOrderMapper omsOrderMapper;
    private final OmsOrderRelationProductService omsOrderRelationProductService;
    private final ExtendSerialService extendSerialService;
    private final ExtendSysUserService extendSysUserService;
    private final ExtendPmsStockService extendPmsStockService;

    public OmsOrderRefundServiceImpl(OmsOrderMapper omsOrderMapper
            , OmsOrderRelationProductService omsOrderRelationProductService
            , ExtendSerialService extendSerialService
            , ExtendSysUserService extendSysUserService
            , ExtendPmsStockService extendPmsStockService) {
        this.omsOrderMapper = omsOrderMapper;
        this.omsOrderRelationProductService = omsOrderRelationProductService;
        this.extendSerialService = extendSerialService;
        this.extendSysUserService = extendSysUserService;
        this.extendPmsStockService = extendPmsStockService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long apply(Long userId, OmsRefundApplyDto dto) {
        OmsOrder order = omsOrderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new DataNotExistException("订单不存在");
        }
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException("无权操作该订单");
        }
        if (!OmsOrderStatusEnum.PENDING_SHIPMENT.getStatus().equals(order.getOrderStatus())) {
            throw new BusinessException("仅待发货订单可申请退款");
        }
        if (order.getPayType() == null || order.getPayType() == 0 || order.getPayTime() == null) {
            throw new BusinessException("订单未支付，无法申请退款");
        }
        if (!StringUtils.hasText(dto.getReason())) {
            throw new BusinessException("退款原因不能为空");
        }

        Long pendingCount = baseMapper.selectCount(new LambdaQueryWrapper<OmsOrderRefund>()
                .eq(OmsOrderRefund::getOrderId, order.getId())
                .eq(OmsOrderRefund::getRefundStatus, OmsRefundStatusEnum.PENDING.getStatus()));
        if (pendingCount != null && pendingCount > 0) {
            throw new BusinessException("该订单已有待审核的退款申请，请勿重复提交");
        }

        String prefix = "TK" + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN).substring(2);
        String refundNo = extendSerialService.generateNumber(prefix, 6);

        OmsOrderRefund refund = new OmsOrderRefund();
        refund.setId(IdUtil.getSnowflakeNextId());
        refund.setRefundNo(refundNo);
        refund.setOrderId(order.getId());
        refund.setOrderCode(order.getOrderCode());
        refund.setUserId(userId);
        refund.setBusinessOrgId(order.getBusinessOrgId());
        refund.setRefundAmount(order.getPayAmount());
        refund.setReasonType(dto.getReasonType());
        refund.setReason(dto.getReason().trim());
        refund.setRefundStatus(OmsRefundStatusEnum.PENDING.getStatus());
        if (!this.save(refund)) {
            throw new BusinessException("退款申请提交失败");
        }

        int updated = omsOrderMapper.update(null, new LambdaUpdateWrapper<OmsOrder>()
                .eq(OmsOrder::getId, order.getId())
                .eq(OmsOrder::getOrderStatus, OmsOrderStatusEnum.PENDING_SHIPMENT.getStatus())
                .set(OmsOrder::getOrderStatus, OmsOrderStatusEnum.REFUNDING.getStatus())
                .set(OmsOrder::getUpdateId, userId)
                .set(OmsOrder::getUpdateTime, LocalDateTime.now()));
        if (updated <= 0) {
            throw new BusinessException("订单状态已变更，请刷新后重试");
        }
        return refund.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean audit(Long auditUserId, OmsRefundAuditDto dto) {
        OmsOrderRefund refund = baseMapper.selectById(dto.getRefundId());
        if (refund == null) {
            throw new DataNotExistException("退款单不存在");
        }
        if (!OmsRefundStatusEnum.PENDING.getStatus().equals(refund.getRefundStatus())) {
            throw new BusinessException("退款单已审核，请勿重复操作");
        }

        OmsOrder order = omsOrderMapper.selectById(refund.getOrderId());
        if (order == null) {
            throw new DataNotExistException("关联订单不存在");
        }
        if (!OmsOrderStatusEnum.REFUNDING.getStatus().equals(order.getOrderStatus())) {
            throw new BusinessException("订单状态不是退款中，无法审核");
        }

        boolean pass = Boolean.TRUE.equals(dto.getPass());
        if (!pass && !StringUtils.hasText(dto.getAuditRemark())) {
            throw new BusinessException("拒绝退款时请填写审核备注");
        }

        LocalDateTime now = LocalDateTime.now();
        Integer targetRefundStatus = pass ? OmsRefundStatusEnum.APPROVED.getStatus() : OmsRefundStatusEnum.REJECTED.getStatus();
        Integer targetOrderStatus = pass ? OmsOrderStatusEnum.REFUNDED.getStatus() : OmsOrderStatusEnum.PENDING_SHIPMENT.getStatus();

        int refundUpdated = baseMapper.update(null, new LambdaUpdateWrapper<OmsOrderRefund>()
                .eq(OmsOrderRefund::getId, refund.getId())
                .eq(OmsOrderRefund::getRefundStatus, OmsRefundStatusEnum.PENDING.getStatus())
                .set(OmsOrderRefund::getRefundStatus, targetRefundStatus)
                .set(OmsOrderRefund::getAuditUserId, auditUserId)
                .set(OmsOrderRefund::getAuditTime, now)
                .set(OmsOrderRefund::getAuditRemark, dto.getAuditRemark())
                .set(OmsOrderRefund::getUpdateTime, now));
        if (refundUpdated <= 0) {
            throw new BusinessException("退款单状态已变更，请刷新后重试");
        }

        int orderUpdated = omsOrderMapper.update(null, new LambdaUpdateWrapper<OmsOrder>()
                .eq(OmsOrder::getId, order.getId())
                .eq(OmsOrder::getOrderStatus, OmsOrderStatusEnum.REFUNDING.getStatus())
                .set(OmsOrder::getOrderStatus, targetOrderStatus)
                .set(OmsOrder::getUpdateId, auditUserId)
                .set(OmsOrder::getUpdateTime, now));
        if (orderUpdated <= 0) {
            throw new BusinessException("订单状态已变更，请刷新后重试");
        }

        if (pass) {
            extendSysUserService.recharge(order.getUserId(), refund.getRefundAmount());
            restoreStock(order.getId());
        }
        return true;
    }

    /**
     * 按订单商品行回补库存（V1 订单行仅有 productId，由库存服务解析真实 SKU）
     */
    private void restoreStock(Long orderId) {
        List<OmsOrderRelationProduct> products = omsOrderRelationProductService.list(
                new LambdaQueryWrapper<OmsOrderRelationProduct>().eq(OmsOrderRelationProduct::getOrderId, orderId));
        if (CollectionUtils.isEmpty(products)) {
            return;
        }
        for (OmsOrderRelationProduct product : products) {
            if (product.getProductId() == null || product.getProductQuantity() == null || product.getProductQuantity() <= 0) {
                continue;
            }
            ExtendPmsStockDto stockDto = new ExtendPmsStockDto();
            stockDto.setOrderId(orderId);
            stockDto.setProductId(product.getProductId());
            stockDto.setSkuId(product.getProductId());
            stockDto.setQuantity(product.getProductQuantity());
            stockDto.setRemark("订单退款回补库存，订单id=" + orderId);
            Boolean added = extendPmsStockService.add(stockDto);
            if (!Boolean.TRUE.equals(added)) {
                throw new BusinessException("回补库存失败，商品id=" + product.getProductId());
            }
        }
    }

    @Override
    public Page<OmsOrderRefundVo> page(PageFilter<OmsOrderRefundQueryDto> filter) {
        if (filter.getFilter() == null) {
            filter.setFilter(new OmsOrderRefundQueryDto());
        }
        return baseMapper.selectPageByFilter(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
    }
}
