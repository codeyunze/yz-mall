package com.yz.statemachine.order;

import java.util.Objects;

/**
 * 演示用订单聚合上下文：承载当前状态及本 demo 中各事件写入的「业务结果」字段。
 * <p>
 * 真实项目中对应：订单实体 + 支付单、物流单、库存流水等，通常与状态一起持久化。
 */
public class OrderContext {

    private final long orderId;
    private OrderStatus status;

    /** 支付成功后写入的第三方流水号（demo） */
    private String payTransactionId;
    /** 发货后写入的物流单号（demo） */
    private String logisticsTrackingNo;
    /** 完成后累计的积分（demo） */
    private int memberPointsDelta;

    public OrderContext(long orderId, OrderStatus initialStatus) {
        this.orderId = orderId;
        this.status = Objects.requireNonNull(initialStatus);
    }

    public long getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    /** 由 {@link OrderProcessService} 在业务成功后调用，更新持久化状态 */
    public void setStatus(OrderStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    public String getPayTransactionId() {
        return payTransactionId;
    }

    public void setPayTransactionId(String payTransactionId) {
        this.payTransactionId = payTransactionId;
    }

    public String getLogisticsTrackingNo() {
        return logisticsTrackingNo;
    }

    public void setLogisticsTrackingNo(String logisticsTrackingNo) {
        this.logisticsTrackingNo = logisticsTrackingNo;
    }

    public int getMemberPointsDelta() {
        return memberPointsDelta;
    }

    public void addMemberPoints(int delta) {
        this.memberPointsDelta += delta;
    }

    @Override
    public String toString() {
        return "OrderContext{"
                + "orderId=" + orderId
                + ", status=" + status
                + ", payTransactionId='" + payTransactionId + '\''
                + ", logisticsTrackingNo='" + logisticsTrackingNo + '\''
                + ", memberPointsDelta=" + memberPointsDelta
                + '}';
    }
}
