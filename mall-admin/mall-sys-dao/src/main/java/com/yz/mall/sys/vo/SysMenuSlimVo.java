package com.yz.mall.sys.vo;

import com.yz.mall.sys.entity.SysMenu;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统-菜单资源表(SysMenu)表实体类
 *
 * @author yunze
 * @since 2024-11-21 23:29:01
 */
@Data
public class SysMenuSlimVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识 {@link SysMenu#getId()}
     */
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
    private String title;
}

