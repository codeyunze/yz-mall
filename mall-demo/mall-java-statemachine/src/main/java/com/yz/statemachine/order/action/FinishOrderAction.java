package com.yz.statemachine.order.action;

import com.yz.statemachine.order.OrderContext;
import com.yz.statemachine.order.OrderEventAction;

/**
 * 确认收货/完结事件业务：模拟积分、结算分账等收尾逻辑。
 */
public class FinishOrderAction implements OrderEventAction {

    @Override
    public void execute(OrderContext order) {
        int points = 50;
        order.addMemberPoints(points);
        System.out.println("    [业务-FINISH] 订单完结，发放积分 +" + points + "，订单=" + order.getOrderId()
                + "（模拟：解冻营销预算、结算佣金、触发评价提醒）");
    }
}
