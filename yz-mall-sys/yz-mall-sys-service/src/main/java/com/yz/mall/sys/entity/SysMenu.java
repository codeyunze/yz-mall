package com.yz.mall.sys.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统-菜单资源表(SysMenu)表实体类
 *
 * @author yunze
 * @since 2024-11-21 23:29:01
 */
@Data
public class SysMenu extends Model<SysMenu> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 数据是否有效：0数据有效
     */
    @TableLogic(value = "0", delval = "current_timestamp")
    private Long invalid;

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
    private String title;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 排序
     */
    private Integer sort;

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
     * 加载动画(内嵌的iframe页面是否开启首次加载动画) {@link com.yz.mall.sys.enums.EnableEnum}
     */
    private Integer frameLoading;

    /**
     * 缓存页面(是否缓存该路由页面) {@link com.yz.mall.sys.enums.EnableEnum}
     */
    private Integer keepAlive;

    /**
     * 标签页(当前菜单名称或自定义信息禁止添加到标签页) {@link com.yz.mall.sys.enums.EnableEnum}
     */
    private Integer hiddenTag;

    /**
     * 固定标签页(当前菜单名称是否固定显示在标签页且不可关闭) {@link com.yz.mall.sys.enums.EnableEnum}
     */
    private Integer fixedTag;

    /**
     * 是否显示该菜单 {@link com.yz.mall.sys.enums.EnableEnum}
     */
    private Integer showLink;

    /**
     * 是否显示父级菜单 {@link com.yz.mall.sys.enums.EnableEnum}
     */
    private Integer showParent;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}

