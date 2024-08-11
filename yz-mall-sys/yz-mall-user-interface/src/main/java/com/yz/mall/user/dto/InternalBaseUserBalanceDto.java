package com.yz.mall.user.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 基础-用户(BaseUser)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Data
public class InternalBaseUserBalanceDto implements Serializable {

    private static final long serialVesionUID = 1L;

    // TODO: 2024/6/19 星期三 yunze 用户标识调整为从token对应的请求者获取
    /**
     * 用户主键标识
     */
    @NotNull(message = "用户标识不能为空")
    private String userId;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    public InternalBaseUserBalanceDto() {
    }

    public InternalBaseUserBalanceDto(String userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }
}

