package com.yz.mall.sys.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yz.mall.sys.entity.SysMenu;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户菜单的元数据
 *
 * @author yunze
 * @date 2024/12/5 17:26
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class SysTreeMenuMetaVo implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 菜单图标 {@link SysMenu#getIcon()}
     */
    private String icon;

    /**
     * 菜单名称 {@link SysMenu#getTitle()}
     */
    private String title;

    /**
     * 菜单排序 {@link SysMenu#getSort()}
     */
    private Integer rank;

    /**
     * 链接地址(嵌入iframe链接地址) {@link SysMenu#getFrameSrc()}
     */
    private String frameSrc;

    /**
     * 缓存页面(是否缓存该路由页面) {@link SysMenu#getKeepAlive()}
     */
    private Boolean keepAlive;

    /**
     * 是否显示该菜单 {@link SysMenu#getShowLink()}
     */
    private Boolean showLink;

    /**
     * 菜单激活 {@link SysMenu#getActivePath()}
     */
    private String activePath;

    /**
     * 菜单名称右侧的额外图标 {@link SysMenu#getExtraIcon()}
     */
    private String extraIcon;

    /**
     * 是否显示父级菜单
     */
    private Boolean showParent;

    /**
     * 内嵌的iframe页面是否开启首次加载动画
     */
    private Boolean frameLoading;

    /**
     * 当前菜单名称或自定义信息禁止添加到标签页
     */
    private Boolean hiddenTag;

    /**
     * 设置可访问当前路由菜单的角色
     */
    private List<Long> roles;
    
}
