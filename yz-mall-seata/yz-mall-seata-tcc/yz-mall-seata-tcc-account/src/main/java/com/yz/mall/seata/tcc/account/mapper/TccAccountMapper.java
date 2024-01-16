package com.yz.mall.seata.tcc.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.seata.tcc.account.entity.TccAccount;
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
public interface TccAccountMapper extends BaseMapper<TccAccount> {

    /**
     * 冻结余额
     *
     * @param accountId    账号id
     * @param freezeAmount 冻结金额
     * @return 影响数据量
     * @apiNote Try: 账户余额-支出余额，冻结余额+支出余额
     */
    @Update("update tcc_account set cash_balance = cash_balance - #{freezeAmount}, freeze_money = freeze_money + #{freezeAmount} where id = #{accountId} and cash_balance > #{freezeAmount}")
    Integer freezeBalance(@Param("accountId") Long accountId, @Param("freezeAmount") BigDecimal freezeAmount);

    /**
     * 扣减冻结金额
     *
     * @param accountId    账号id
     * @param deductAmount 冻结金额
     * @return 影响数据量
     * @apiNote Confirm: 冻结余额-支出余额 （真正的扣减余额）
     */
    @Update("update tcc_account set freeze_money = freeze_money - #{deductAmount} where id = #{accountId}")
    Integer deductFreezeBalance(@Param("accountId") Long accountId, @Param("deductAmount") BigDecimal deductAmount);

    /**
     * 解冻金额
     *
     * @param accountId      账号id
     * @param unfreezeAmount 解冻金额
     * @return 影响数据量
     * @apiNote Cancel: 冻结余额-支出余额，账户余额+支出余额
     */
    @Update("update tcc_account set freeze_money = freeze_money - #{unfreezeAmount}, cash_balance = cash_balance + #{unfreezeAmount} where id = #{accountId}")
    Integer unfreezeBalance(@Param("accountId") Long accountId, @Param("unfreezeAmount") BigDecimal unfreezeAmount);
}

