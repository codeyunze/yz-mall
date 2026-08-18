package com.yz.mall.sys.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 第三方客户端授权操作数据模型类
 *
 * @author yunze
 */
@Data
public class SysOpenClientAuthDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端标识
     */
    @NotBlank(message = "clientId不能为空")
    private String clientId;

    /**
     * 权限码集合
     */
    @NotEmpty(message = "权限码不能为空")
    private List<String> permissionCodes;

    /**
     * 备注
     */
    private String remark;
}
