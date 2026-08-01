package com.yz.statemachine.order.action;

import com.yz.statemachine.order.OrderContext;
import com.yz.statemachine.order.OrderEventAction;

import java.util.UUID;

/**
 * 支付事件业务：模拟拉起支付、扣款成功、预占库存、写支付流水。
 */
public class PayOrderAction implements OrderEventAction {

    @Override
    public void execute(OrderContext order) {
        // 真实场景：调支付网关、幂等校验、与订单金额对账等
        String txnId = "PAY-" + UUID.randomUUID().toString().substring(0, 8);
        order.setPayTransactionId(txnId);
        System.out.println("    [业务-PAY] 支付成功，流水号=" + txnId + "，订单=" + order.getOrderId()
                + "（模拟：预占库存 SKU、写入 oms_payment 流水）");
    }
}
