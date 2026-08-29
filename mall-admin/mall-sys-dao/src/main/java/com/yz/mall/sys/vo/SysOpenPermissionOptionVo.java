package com.yz.mall.sys.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 可授权开放 API 权限码选项
 *
 * @author yunze
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysOpenPermissionOptionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限码
     */
    private String value;

    /**
     * 展示名称
     */
    private String label;
}
