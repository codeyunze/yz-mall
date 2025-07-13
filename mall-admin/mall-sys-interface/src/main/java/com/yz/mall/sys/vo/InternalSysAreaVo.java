package com.yz.mall.sys.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 地区
 *
 * @author yunze
 * @since 2025-03-03 22:38:55
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class InternalSysAreaVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 行政地区Id（地区编码）
     */
    private String id;

    /**
     * 行政地区名称
     */
    private String name;

    /**
     * 上级行政地区编码
     */
    private String parentId;

    /**
     * 行政地区等级
     */
    private Integer levelType;

    /**
     * 详细行政地区
     */
    private String parentName;

}

