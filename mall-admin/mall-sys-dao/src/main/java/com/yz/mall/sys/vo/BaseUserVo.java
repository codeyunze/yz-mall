package com.yz.mall.sys.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 基础-用户(BaseUser)表实体类
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@Data
public class BaseUserVo extends Model<BaseUserVo> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 账户余额
     */
    private BigDecimal balance;
}

