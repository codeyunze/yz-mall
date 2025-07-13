package com.yz.mall.auth.vo;

import lombok.Data;

/**
 * 组织信息
 *
 * @author yunze
 * @since 2025/5/18 21:42
 */
@Data
public class OrgInfo {

    /**
     * 组织Id
     */
    private Long orgId;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织路径
     */
    private String orgPathId;
}
