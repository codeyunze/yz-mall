package com.yz.mall.sys.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * 系统-菜单资源表(SysMenu)表更新数据模型类
 *
 * @author yunze
 * @since 2024-11-21 23:29:02
 */
@Data
public class SysMenuUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 上级菜单
     */
    private Long parentId;

    /**
     * 菜单类型0-菜单,1-iframe,2-外链接,3-按钮,4-接口
     */
    private Integer menuType;

    /**
     * 菜单名称
     */
    @Length(max = 50, message = "菜单名称超过50个字符")
    private String title;

    /**
     * 路由名称
     */
    @Length(max = 100, message = "路由名称超过100个字符")
    private String name;

    /**
     * 路由路径
     */
    @Length(max = 50, message = "路由路径超过50个字符")
    private String path;

    /**
     * 组件路径
     */
    @Length(max = 50, message = "组件路径超过50个字符")
    private String component;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 路由重定向
     */
    @Length(max = 50, message = "路由重定向超过50个字符")
    private String redirect;

    /**
     * 菜单图标
     */
    @Length(max = 100, message = "菜单图标超过100个字符")
    private String icon;

    /**
     * 菜单右侧额外图标
     */
    @Length(max = 100, message = "菜单右侧额外图标超过100个字符")
    private String extraIcon;

    /**
     * 进场动画
     */
    @Length(max = 10, message = "进场动画超过10个字符")
    private String enterTransition;

    /**
     * 离场动画
     */
    @Length(max = 10, message = "离场动画超过10个字符")
    private String leaveTransition;

    /**
     * 菜单激活
     */
    @Length(max = 100, message = "菜单激活超过100个字符")
    private String activePath;

    /**
     * 按钮权限标识
     */
    @Length(max = 50, message = "按钮权限标识超过50个字符")
    private String auths;

    /**
     * 链接地址(嵌入iframe链接地址)
     */
    @Length(max = 100, message = "链接地址超过100个字符")
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

