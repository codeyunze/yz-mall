package com.yz.mall.sys.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方开放客户端(SysOpenClient)表查询过滤条件数据模型类
 *
 * @author yunze
 */
@Data
public class SysOpenClientQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端标识
     */
    private String clientId;

    /**
     * 应用名称（模糊）
     */
    private String clientName;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;
}
