package com.yz.mall.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yunze
 * @date 2024/11/19 星期二 22:54
 */
@Data
public class RefreshTokenDto implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 刷新token
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}
