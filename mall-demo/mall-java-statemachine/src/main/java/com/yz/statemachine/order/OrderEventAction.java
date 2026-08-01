package com.yz.statemachine.order;

/**
 * 某一 {@link OrderEvent} 对应的业务副作用：支付、发货、完结、取消等应各自实现本接口。
 * <p>
 * <b>放置位置约定（与状态机的关系）：</b>
 * <ul>
 *   <li>{@link OrderStateMachine} 只维护「状态 × 事件 → 下一状态」，不包含业务代码。</li>
 *   <li>本接口的实现类放在业务层（本 demo 为 {@code com.yz.statemachine.order.action} 包），
 *       由 {@link OrderProcessService} 在合法迁移确定后调用。</li>
 *   <li>若使用 Spring，可将各 Action 声明为 Bean，在 ProcessService 中按事件注入或从 Map 获取。</li>
 * </ul>
 *
 * @author yunze
 * @since 2026/04/09
 */
@FunctionalInterface
public interface OrderEventAction {

    /**
     * 执行该事件对应的业务逻辑（调支付、写库存、发物流等）。成功返回后，由编排层更新订单状态。
     *
     * @param order 当前订单上下文（只读/有限写入由实现约定；demo 中由 action 写入扩展字段）
     * @throws RuntimeException 业务失败时应抛出，编排层不应更新状态（见 {@link OrderProcessService}）
     */
    void execute(OrderContext order);
}
