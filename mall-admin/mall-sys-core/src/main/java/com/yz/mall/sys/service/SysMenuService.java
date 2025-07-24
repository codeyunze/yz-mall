package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysMenuAddDto;
import com.yz.mall.sys.dto.SysMenuQueryDto;
import com.yz.mall.sys.dto.SysMenuUpdateDto;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.vo.SysMenuSlimVo;
import com.yz.mall.sys.vo.SysTreeMenuVo;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 系统-菜单资源表(SysMenu)表服务接口
 *
 * @author yunze
 * @since 2024-11-21 23:29:01
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysMenuAddDto dto);

    /**
     * 更新数据
     * <br> 会同时更新所有角色的按钮和接口权限缓存
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysMenuUpdateDto dto);

    /**
     * 列表查询
     *
     * @param filter 过滤条件
     * @return 列表数据
     */
    List<SysMenu> list(SysMenuQueryDto filter);

    /**
     * 查询所有菜单
     */
    List<SysMenuSlimVo> listSlim();

    /**
     * 将菜单列表信息加工处理为树形结构
     *
     * @param menus         菜单列表信息
     * @param parentId      父级菜单Id (默认传0L，标识从第一级菜单开始往下处理)
     * @param roleByMenuMap 菜单所关联的角色
     * @return 树形结构菜单信息
     */
    List<SysTreeMenuVo> menusInfoProcessor(List<SysMenu> menus, Long parentId, Map<Long, List<Long>> roleByMenuMap);
}

