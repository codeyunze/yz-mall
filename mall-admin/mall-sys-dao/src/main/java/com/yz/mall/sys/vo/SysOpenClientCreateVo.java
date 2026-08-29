package com.yz.mall.sys.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 新建第三方客户端结果（回显 clientId）
 *
 * @author yunze
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysOpenClientCreateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 客户端标识，对外唯一
     */
    private String clientId;
}
