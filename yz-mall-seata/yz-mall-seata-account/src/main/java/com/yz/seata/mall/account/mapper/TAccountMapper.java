package com.yz.seata.mall.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.seata.mall.account.entity.TAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 账号信息(TAccount)表数据库访问层
 *
 * @author yunze
 * @since 2023-11-07 23:21:27
 */
@Mapper
public interface TAccountMapper extends BaseMapper<TAccount> {

    /**
     * 扣减余额
     *
     * @param accountId 账号id
     * @param amount    扣减金额
     * @return 影响数据量
     */
    @Update("update t_account set cash_balance = cash_balance - #{amountToDeduct} where id = #{accountId} and cash_balance > #{amountToDeduct}")
    Integer deduct(@Param("accountId") Long accountId, @Param("amountToDeduct") BigDecimal amountToDeduct);

}

