package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysMenuAddDto;
import com.yz.mall.sys.dto.SysMenuQueryDto;
import com.yz.mall.sys.dto.SysMenuUpdateDto;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.vo.SysMenuSlimVo;

import javax.validation.Valid;
import java.util.List;

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
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysMenuUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    List<SysMenu> list(SysMenuQueryDto filter);

    /**
     * 查询请求者所拥有访问权限的菜单
     * @param roleIds 请求则所拥有的所有角色Id
     */
    // List<SysMenuSlimVo> listSlim(List<Long> roleIds);

    /**
     * 查询所有菜单
     */
    List<SysMenuSlimVo> listSlim();
}

