package com.yz.mall.tw.access.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * EMQX HTTP 认证/授权响应
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmqxAuthResponse {

    /**
     * allow / deny
     */
    private String result;
    /**
     * 是否超级用户
     */
    private Boolean is_superuser;

    public static EmqxAuthResponse allow() {
        EmqxAuthResponse r = new EmqxAuthResponse();
        r.setResult("allow");
        r.setIs_superuser(false);
        return r;
    }

    public static EmqxAuthResponse deny() {
        EmqxAuthResponse r = new EmqxAuthResponse();
        r.setResult("deny");
        return r;
    }
}
