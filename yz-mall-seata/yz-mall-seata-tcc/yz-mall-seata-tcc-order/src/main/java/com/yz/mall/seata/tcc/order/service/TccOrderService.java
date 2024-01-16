package com.yz.mall.seata.tcc.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.seata.tcc.order.dto.TccOrderDto;
import com.yz.mall.seata.tcc.order.entity.TccOrder;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * 订单信息(TOrder)表服务接口
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@LocalTCC
public interface TccOrderService extends IService<TccOrder> {

    /**
     * 准备保存订单信息
     *
     * @param order   订单信息
     * @param orderId 订单主键ID
     * @return 订单详细信息
     */
    @TwoPhaseBusinessAction(name = "prepareSaveOrder", commitMethod = "commit", rollbackMethod = "rollback", useTCCFence = true)
    TccOrder prepareSaveOrder(TccOrderDto order,
                              @BusinessActionContextParameter(paramName = "orderId") Long orderId);

    /**
     * 提交事务，二阶段确认方法可以另命名，但要保证与commitMethod一致
     * context可以传递try方法的参数
     *
     * @param actionContext 上下文数据
     * @return 是否操作成功
     */
    boolean commit(BusinessActionContext actionContext);

    /**
     * 回滚事务，二阶段取消方法可以另命名，但要保证与rollbackMethod一致
     *
     * @param actionContext 上下文数据
     * @return 是否操作成功
     */
    boolean rollback(BusinessActionContext actionContext);
}

