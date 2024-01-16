package com.yz.mall.seata.at.order.config;

import com.yz.mall.seata.at.order.feign.TStorageFeignService;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author yunze
 * @date 2023/11/7 0007 0:02
 */
@Slf4j
@Component
public class FallbackAccountFeignServiceFactory implements FallbackFactory<TStorageFeignService> {
    @Override
    public TStorageFeignService create(Throwable throwable) {

        return new TStorageFeignService() {
            @Override
            public Result<Boolean> deduct(Long productId, Integer num) {
                log.info("账户服务异常降级了");
                // 解决 feign整合sentinel降级导致Seata失效的处理  此方案不可取
                //
//                if(!StringUtils.isEmpty(RootContext.getXID())){
//                    //通过xid获取GlobalTransaction调用rollback回滚
                //  可以让库存服务回滚  能解决问题吗？  绝对不能用
//                    GlobalTransactionContext.reload(RootContext.getXID()).rollback();
//                }
                return new Result<>(CodeEnum.SUCCESS.get(), false, "服务降级");
            }
        };
    }
}
