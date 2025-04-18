package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.vo.BaseUserVo;
import com.yz.mall.sys.vo.SysUserVo;
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
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 获取用户信息
     *
     * @param account 登录账号（手机号|邮箱）
     * @return 用户信息
     */
    @Select("select id, phone, email, password, balance from sys_user where phone = #{account} or email = #{account}")
    BaseUserVo get(@Param("account") String account);

    /**
     * 扣减账户金额
     *
     * @param userId 扣减用户
     * @param amount 扣减金额
     */
    @Update("update sys_user set balance = balance - #{amount} where invalid = 0 and id = #{userId} and balance >= #{amount}")
    Integer deduct(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 账户充值
     *
     * @param userId 充值用户
     * @param amount 充值金额
     */
    @Update("update sys_user set balance = balance + #{amount} where invalid = 0 and id = #{userId}")
    Integer recharge(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysUserVo> selectPage(Page<Object> page, @Param("filter") SysUserQueryDto filter);
}

