package com.yz.mall.sys.vo;

import com.yz.mall.sys.entity.SysArea;
import lombok.Data;

import java.io.Serializable;

/**
 * 地区数据
 *
 * @author yunze
 * @since 2025/3/3 22:45
 */
@Data
public class AreaVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 上级地区编码
     */
    private String parentValue;

    /**
     * 地区名
     */
    private String label;

    /**
     * 地区编码
     */
    private String value;

    /**
     * 级别
     */
    private Integer level;


    public AreaVo(String parentId, SysArea sysArea) {
        this.parentValue = parentId;
        this.label = sysArea.getName();
        this.value = sysArea.getId();
        this.level = sysArea.getLevelType();
    }

    public AreaVo() {}
}
