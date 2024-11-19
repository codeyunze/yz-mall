package com.yz.mall.security.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yunze
 * @date 2024/11/19 星期二 22:54
 */
public class RefreshTokenDto implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 刷新token
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;

    /**
     * 用户Id
     */
    @NotBlank(message = "用户标识不能为空")
    private String userId;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
