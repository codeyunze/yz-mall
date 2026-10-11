package com.yz.mall.tw.access.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * EMQX HTTP 授权（ACL）请求
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmqxAclRequest {

    private String clientid;
    private String username;
    private String topic;
    /**
     * publish / subscribe
     */
    private String action;
    private String ipaddr;
}
