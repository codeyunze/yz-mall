package com.yz.mall.sys.dto;

import com.yz.mall.sys.entity.SysMenu;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统-菜单资源表(SysMenu)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-11-21 23:29:02
 */
@Data
public class SysMenuQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 菜单主键Id列表 {@link SysMenu#getId()}
     */
    private List<Long> menuIds;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

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
     * 按钮权限标识
     */
    private String auths;



}

