package com.yz.mall.seata.tcc.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.seata.tcc.account.entity.TccAccount;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 账号信息(TccAccount)表服务接口
 *
 * @author yunze
 * @since 2023-11-07 23:21:27
 */
@LocalTCC
public interface TccAccountService extends IService<TccAccount> {

    /**
     * 扣减余额
     *
     * @param accountId      账号id
     * @param amountToDeduct 扣减金额
     * @return 是否扣减成功
     */
    @TwoPhaseBusinessAction(name = "deduct", commitMethod = "commit", rollbackMethod = "rollback", useTCCFence = true)
    boolean deduct(@BusinessActionContextParameter(paramName = "accountId") Long accountId,
                   @BusinessActionContextParameter(paramName = "amountToDeduct") BigDecimal amountToDeduct);

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

