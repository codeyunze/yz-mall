package com.yz.statemachine.order.action;

import com.yz.statemachine.order.OrderContext;
import com.yz.statemachine.order.OrderEventAction;

/**
 * 取消事件业务：模拟释放库存、未支付则关闭单、已支付则走退款流程。
 */
public class CancelOrderAction implements OrderEventAction {

    @Override
    public void execute(OrderContext order) {
        // 真实场景：按当前是否已支付分支：退款 API、释放预占库存、关单原因落库
        System.out.println("    [业务-CANCEL] 取消订单，订单=" + order.getOrderId()
                + "（模拟：释放预占库存；若已付则创建退款单并异步回调）");
    }
}
