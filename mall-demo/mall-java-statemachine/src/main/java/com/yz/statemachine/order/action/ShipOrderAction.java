package com.yz.statemachine.order.action;

import com.yz.statemachine.order.OrderContext;
import com.yz.statemachine.order.OrderEventAction;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 发货事件业务：模拟创建物流单、写运单号、通知承运商。
 */
public class ShipOrderAction implements OrderEventAction {

    @Override
    public void execute(OrderContext order) {
        // 真实场景：WMS 出库、对接物流 API、回写运单与发货时间
        String tracking = "SF" + ThreadLocalRandom.current().nextInt(100000000, 999999999);
        order.setLogisticsTrackingNo(tracking);
        System.out.println("    [业务-SHIP] 已发货，运单号=" + tracking + "，订单=" + order.getOrderId()
                + "（模拟：出库扣实物库存、写入物流订阅）");
    }
}
