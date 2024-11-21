package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-菜单资源表(SysMenu)表新增数据模型类
 *
 * @author yunze
 * @since 2024-11-21 23:29:02
 */
@Data
public class SysMenuAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 上级菜单
     */
    private Long parentId = 0L;

    /**
     * 菜单类型0-菜单,1-iframe,2-外链接,3-按钮,4-接口
     */
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    private String title;

    /**
     * 路由名称
     */
    @NotBlank(message = "路由名称不能为空")
    private String name;

    /**
     * 路由路径
     */
    @NotBlank(message = "路由路径不能为空")
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 排序
     */
    private Integer sort = 99;

    /**
     * 路由重定向
     */
    private String redirect;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单右侧额外图标
     */
    private String extraIcon;

    /**
     * 进场动画
     */
    private String enterTransition;

    /**
     * 离场动画
     */
    private String leaveTransition;

    /**
     * 菜单激活
     */
    private String activePath;

    /**
     * 按钮权限标识
     */
    private String auths;

    /**
     * 链接地址(嵌入iframe链接地址)
     */
    private String frameSrc;

    /**
     * 加载动画(内嵌的iframe页面是否开启首次加载动画)
     */
    private Integer frameLoading;

    /**
     * 缓存页面(是否缓存该路由页面)
     */
    private Integer keepAlive;

    /**
     * 标签页(当前菜单名称或自定义信息禁止添加到标签页)
     */
    private Integer hiddenTag;

    /**
     * 固定标签页(当前菜单名称是否固定显示在标签页且不可关闭)
     */
    private Integer fixedTag;

    /**
     * 是否显示该菜单
     */
    private Integer showLink;

    /**
     * 是否显示父级菜单
     */
    private Integer showParent;


}

