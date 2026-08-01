package com.yz.statemachine.order;

import com.yz.statemachine.order.action.CancelOrderAction;
import com.yz.statemachine.order.action.FinishOrderAction;
import com.yz.statemachine.order.action.PayOrderAction;
import com.yz.statemachine.order.action.ShipOrderAction;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 订单流程编排：连接「状态机规则」与「事件业务实现」的枢纽。
 * <p>
 * <b>推荐执行顺序（与多数电商落地一致）：</b>
 * <ol>
 *   <li>用 {@link OrderStateMachine#transit(OrderStatus, OrderEvent)} 校验并解析目标状态（只做规则，不写业务）。</li>
 *   <li>调用 {@link OrderEventAction#execute(OrderContext)} 完成副作用；失败则抛异常，<b>不修改</b>订单状态。</li>
 *   <li>业务成功后，将上下文中的状态更新为步骤 1 得到的目标状态（demo 直接改内存；生产环境应在同一事务中写库）。</li>
 * </ol>
 * 若需「先落库再调第三方」，可改为：乐观锁更新状态 → 调支付；失败则补偿回滚状态（需额外设计）。
 */
public class OrderProcessService {

    private final OrderStateMachine stateMachine;
    private final Map<OrderEvent, OrderEventAction> actions;

    public OrderProcessService() {
        this(new OrderStateMachine(), defaultActions());
    }

    public OrderProcessService(OrderStateMachine stateMachine, Map<OrderEvent, OrderEventAction> actions) {
        this.stateMachine = Objects.requireNonNull(stateMachine);
        this.actions = Objects.requireNonNull(actions);
    }

    private static Map<OrderEvent, OrderEventAction> defaultActions() {
        Map<OrderEvent, OrderEventAction> map = new EnumMap<>(OrderEvent.class);
        map.put(OrderEvent.PAY, new PayOrderAction());
        map.put(OrderEvent.SHIP, new ShipOrderAction());
        map.put(OrderEvent.FINISH, new FinishOrderAction());
        map.put(OrderEvent.CANCEL, new CancelOrderAction());
        return map;
    }

    /**
     * 触发一次状态迁移并执行业务：合法则先跑业务，成功后再改状态。
     *
     * @param order 订单上下文
     * @param event 业务事件
     * @throws IllegalStateException 非法迁移（与状态机一致）
     * @throws RuntimeException      业务动作失败
     */
    public void fire(OrderContext order, OrderEvent event) {
        OrderStatus from = order.getStatus();
        OrderStatus to = stateMachine.transit(from, event);
        OrderEventAction action = actions.get(event);
        if (action == null) {
            throw new IllegalStateException("未注册事件对应的业务实现: " + event);
        }
        action.execute(order);
        order.setStatus(to);
    }

    public OrderStateMachine getStateMachine() {
        return stateMachine;
    }
}
