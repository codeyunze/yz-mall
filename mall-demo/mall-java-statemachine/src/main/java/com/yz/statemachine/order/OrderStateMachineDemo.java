package com.yz.statemachine.order;

/**
 * 演示入口：1）纯状态机校验；2）带业务动作的完整下单流程（{@link OrderProcessService}）。
 *
 * @author yunze
 * @since 2026/04/08
 */
public class OrderStateMachineDemo {

    /**
     * 先跑「仅状态迁移」示例，再跑「状态机 + 各事件业务实现」示例。
     *
     * @param args 未使用
     */
    public static void main(String[] args) {
        demoStateMachineOnly();
        System.out.println();
        System.out.println("========== 以下为：状态机 + 业务 Action（推荐落地形态）==========");
        demoProcessWithBusinessActions();
    }

    /**
     * 仅演示 {@link OrderStateMachine#transit(OrderStatus, OrderEvent)}：无业务副作用，非法迁移抛异常。
     */
    private static void demoStateMachineOnly() {
        OrderStateMachine stateMachine = new OrderStateMachine();
        OrderStatus status = OrderStatus.CREATED;
        System.out.println("【纯状态机】初始状态: " + status);

        status = printTransit(stateMachine, status, OrderEvent.PAY);
        status = printTransit(stateMachine, status, OrderEvent.SHIP);
        status = printTransit(stateMachine, status, OrderEvent.FINISH);

        try {
            printTransit(stateMachine, status, OrderEvent.CANCEL);
        } catch (IllegalStateException ex) {
            System.out.println("【纯状态机】非法流转校验: " + ex.getMessage());
        }
    }

    /**
     * 演示 {@link OrderProcessService#fire(OrderContext, OrderEvent)}：
     * 每次触发事件会先校验迁移，再执行 {@link OrderEventAction}，最后写回订单状态。
     */
    private static void demoProcessWithBusinessActions() {
        OrderProcessService process = new OrderProcessService();
        OrderContext order = new OrderContext(10001L, OrderStatus.CREATED);
        System.out.println("初始: " + order);

        fireAndPrint(process, order, OrderEvent.PAY);
        fireAndPrint(process, order, OrderEvent.SHIP);
        fireAndPrint(process, order, OrderEvent.FINISH);
        System.out.println("流程结束: " + order);

        OrderContext cancelDemo = new OrderContext(10002L, OrderStatus.CREATED);
        System.out.println();
        System.out.println("--- 取消分支示例（CREATED + CANCEL）---");
        fireAndPrint(process, cancelDemo, OrderEvent.CANCEL);
        System.out.println("取消后: " + cancelDemo);
    }

    private static void fireAndPrint(OrderProcessService process, OrderContext order, OrderEvent event) {
        System.out.println(">>> 触发事件: " + event + "，当前状态: " + order.getStatus());
        process.fire(order, event);
        System.out.println("    迁移后状态: " + order.getStatus());
    }

    private static OrderStatus printTransit(OrderStateMachine stateMachine, OrderStatus from, OrderEvent event) {
        OrderStatus to = stateMachine.transit(from, event);
        System.out.println("执行事件: " + event + "，状态变化: " + from + " -> " + to);
        return to;
    }
}
