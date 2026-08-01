package com.yz.statemachine.order;

/**
 * 触发订单状态迁移的业务事件，由外部动作（支付成功、发货、确认收货、取消等）映射而来。
 *
 * @author yunze
 * @since 2026/04/08
 * @see OrderStateMachine
 */
public enum OrderEvent {
    /** 支付完成 */
    PAY,
    /** 商家发货 */
    SHIP,
    /** 确认收货 / 流程完结 */
    FINISH,
    /** 取消订单 */
    CANCEL
}
