package com.yz.statemachine.order;

/**
 * 演示用订单生命周期状态（与 {@link OrderStateMachine} 中允许的迁移一致）。
 * <p>
 * 典型路径：已创建 → 已支付 → 已发货 → 已完成；在「已创建 / 已支付」阶段可取消。
 *
 * @author yunze
 * @since 2026/04/08
 */
public enum OrderStatus {
    /** 订单已创建，待支付 */
    CREATED,
    /** 已支付，待发货 */
    PAID,
    /** 已发货，待确认完成 */
    SHIPPED,
    /** 订单正常结束 */
    FINISHED,
    /** 订单已取消 */
    CANCELED
}
