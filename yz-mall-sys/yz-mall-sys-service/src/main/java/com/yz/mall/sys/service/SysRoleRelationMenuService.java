package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysRoleRelationMenuAddDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuQueryDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuUpdateDto;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统-角色关联菜单表(SysRoleRelationMenu)表服务接口
 *
 * @author yunze
 * @since 2024-11-28 12:58:05
 */
public interface SysRoleRelationMenuService extends IService<SysRoleRelationMenu> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysRoleRelationMenuAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysRoleRelationMenuUpdateDto dto);

    /**
     * 列表查询
     * @param filter 过滤条件
     * @return 查询列表数据
     */
    List<SysRoleRelationMenu> list(SysRoleRelationMenuQueryDto filter);

}

