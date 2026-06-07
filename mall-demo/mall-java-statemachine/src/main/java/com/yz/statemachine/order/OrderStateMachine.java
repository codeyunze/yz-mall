package com.yz.statemachine.order;

import java.util.EnumMap;
import java.util.Map;

/**
 * 轻量级订单状态机：用二维表（当前状态 → 事件 → 目标状态）描述合法迁移，不依赖 Spring。
 * <p>
 * 本示例未包含「已发货」后的取消等扩展路径，仅演示最小闭环与非法迁移校验。
 * <p>
 * <b>业务代码应写在哪里：</b>不要写在本类中。每个事件对应的支付、库存、物流等应实现
 * {@link OrderEventAction}，由 {@link OrderProcessService} 在合法迁移确定后统一调用。
 *
 * @author yunze
 * @since 2026/04/08
 * @see OrderEventAction
 * @see OrderProcessService
 */
public class OrderStateMachine {
    /**
     * 状态转移表：外层 key 为当前 {@link OrderStatus}，内层为「事件 → 下一状态」。
     */
    private final Map<OrderStatus, Map<OrderEvent, OrderStatus>> transitions = new EnumMap<>(OrderStatus.class);

    /**
     * 注册默认转移规则：
     * <ul>
     *   <li>CREATED + PAY → PAID；CREATED + CANCEL → CANCELED</li>
     *   <li>PAID + SHIP → SHIPPED；PAID + CANCEL → CANCELED</li>
     *   <li>SHIPPED + FINISH → FINISHED</li>
     * </ul>
     */
    public OrderStateMachine() {
        addTransition(OrderStatus.CREATED, OrderEvent.PAY, OrderStatus.PAID);
        addTransition(OrderStatus.CREATED, OrderEvent.CANCEL, OrderStatus.CANCELED);
        addTransition(OrderStatus.PAID, OrderEvent.SHIP, OrderStatus.SHIPPED);
        addTransition(OrderStatus.PAID, OrderEvent.CANCEL, OrderStatus.CANCELED);
        addTransition(OrderStatus.SHIPPED, OrderEvent.FINISH, OrderStatus.FINISHED);
    }

    /**
     * 根据当前状态与事件计算下一状态。
     *
     * @param currentStatus 当前订单状态
     * @param event         发生的事件
     * @return 迁移后的目标状态
     * @throws IllegalStateException 当当前状态下不允许该事件时（无定义转移）
     */
    public OrderStatus transit(OrderStatus currentStatus, OrderEvent event) {
        Map<OrderEvent, OrderStatus> statusMap = transitions.get(currentStatus);
        if (statusMap == null || !statusMap.containsKey(event)) {
            throw new IllegalStateException("不支持的状态流转: " + currentStatus + " --" + event + "--> ?");
        }
        return statusMap.get(event);
    }

    /**
     * 注册一条有向边：在状态 {@code from} 上发生 {@code event} 时进入 {@code to}。
     */
    private void addTransition(OrderStatus from, OrderEvent event, OrderStatus to) {
        transitions.computeIfAbsent(from, key -> new EnumMap<>(OrderEvent.class)).put(event, to);
    }
}
