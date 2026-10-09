package com.yz.mall.tw.access.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * EMQX HTTP 认证请求（EMQX 5 Authenticator HTTP）
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmqxAuthRequest {

    private String clientid;
    private String username;
    private String password;
    private String peerhost;
    private String proto_name;
    private String mountpoint;
}
