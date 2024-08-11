package com.yz.mall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.user.entity.BaseUser;
import com.yz.mall.user.vo.BaseUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 基础-用户(BaseUser)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Mapper
public interface BaseUserMapper extends BaseMapper<BaseUser> {

    /**
     * 获取用户信息
     *
     * @param account 登录账号（手机号|邮箱）
     * @return 用户信息
     */
    @Select("select id, phone, email, password, balance from base_user where phone = #{account} or email = #{account}")
    BaseUserVo get(@Param("account") String account);

    /**
     * 扣减账户金额
     *
     * @param userId 扣减用户
     * @param amount 扣减金额
     */
    @Update("update base_user set balance = balance - #{amount} where invalid = 0 and id = #{userId} and balance >= #{amount}")
    Integer deduct(@Param("userId") String userId, @Param("amount") BigDecimal amount);

    /**
     * 账户充值
     *
     * @param userId 充值用户
     * @param amount 充值金额
     */
    @Update("update base_user set balance = balance + #{amount} where invalid = 0 and id = #{userId}")
    Integer recharge(@Param("userId") String userId, @Param("amount") BigDecimal amount);
}

