package com.yz.mall.seata.tcc.storage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.seata.tcc.storage.entity.TccStorage;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * 库存信息(TStock)表服务接口
 *
 * @author yunze
 * @since 2023-11-05 15:59:36
 */
@LocalTCC
public interface TccStorageService extends IService<TccStorage> {

    /**
     * 扣减库存数量
     *
     * @param accountId 账号id
     * @param deductNum 扣减库存数量
     * @return 是否扣减成功
     */
    @TwoPhaseBusinessAction(name = "deduct", commitMethod = "commit", rollbackMethod = "rollback", useTCCFence = true)
    boolean deduct(@BusinessActionContextParameter(paramName = "accountId") Long accountId,
                   @BusinessActionContextParameter(paramName = "deductNum") Integer deductNum);


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

