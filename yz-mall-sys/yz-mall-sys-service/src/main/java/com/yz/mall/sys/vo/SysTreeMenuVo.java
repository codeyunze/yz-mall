package com.yz.mall.sys.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yz.mall.sys.entity.SysMenu;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 树形结构的菜单信息
 *
 * @author yunze
 * @since 2024-12-05 12:42:02
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class SysTreeMenuVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单地址 {@link SysMenu#getPath()}
     */
    private String path;

    /**
     * 组件路径 {@link SysMenu#getComponent()}
     */
    private String component;

    /**
     * 路由名称 {@link SysMenu#getName()}
     */
    private String name;

    /**
     * 路由元信息
     */
    private SysTreeMenuMetaVo meta;

    /**
     * 当前菜单的子菜单
     */
    private List<SysTreeMenuVo> children;

}

