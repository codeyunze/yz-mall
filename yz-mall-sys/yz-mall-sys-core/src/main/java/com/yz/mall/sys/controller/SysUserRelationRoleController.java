package com.yz.mall.sys.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysUserRelationRoleAddDto;
import com.yz.mall.sys.dto.SysUserRelationRoleQueryDto;
import com.yz.mall.sys.dto.SysUserRelationRoleUpdateDto;
import com.yz.mall.sys.entity.SysUserRelationRole;
import com.yz.mall.sys.service.SysUserRelationRoleService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 系统-关联角色数据表(SysUserRelationRole)表控制层
 *
 * @author yunze
 * @since 2024-11-17 19:55:59
 */
@RestController
@RequestMapping("sys/user/role")
public class SysUserRelationRoleController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysUserRelationRoleService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysUserRelationRoleAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysUserRelationRoleUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<ResultTable<SysUserRelationRole>> page(@RequestBody @Valid PageFilter<SysUserRelationRoleQueryDto> filter) {
        Page<SysUserRelationRole> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysUserRelationRole> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }


    /**
     * 获取请求用户所拥有的角色Id
     *
     * @return 请求用户所拥有的角色Id
     */
    @GetMapping("getRoleId")
    public Result<List<Long>> getRole() {
        long userId = StpUtil.getLoginIdAsLong();
        List<Long> roleIdsByRelationId = service.getRoleIdsByRelationId(userId);
        return success(roleIdsByRelationId);
    }
}

