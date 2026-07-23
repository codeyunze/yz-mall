package com.yz.mall.sys.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 基础-用户(BaseUser)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Data
public class ExtendSysUserBalanceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    // TODO: 2024/6/19 星期三 yunze 用户标识调整为从token对应的请求者获取
    /**
     * 用户主键标识
     */
    @NotNull(message = "用户标识不能为空")
    private Long userId;

    /**
     * 金额（单位：分）
     */
    @NotNull(message = "金额不能为空")
    private Long amount;

    public ExtendSysUserBalanceDto() {
    }

    public ExtendSysUserBalanceDto(Long userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }
}

