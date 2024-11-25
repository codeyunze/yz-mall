package com.yz.mall.sys.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysUserAddDto;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.dto.SysUserUpdateDto;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.service.SysUserService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.ResultTable;
import com.yz.tools.enums.CodeEnum;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 基础-用户(BaseUser)表控制层
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@RestController
@RequestMapping("sys/user")
public class SysUserController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysUserService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid SysUserAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysUserUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 切换角色状态
     *
     * @param id 角色Id
     * @return 是否切换成功
     */
    @PostMapping("switch/{id}")
    public Result<Boolean> switchRole(@PathVariable Long id) {
        boolean updated = service.updateUserStatusById(id);
        return updated ? success(true) : error(CodeEnum.BUSINESS_ERROR);
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<ResultTable<SysUser>> page(@RequestBody @Valid PageFilter<SysUserQueryDto> filter) {
        Page<SysUser> page = this.service.page(filter);
        return new Result<>(CodeEnum.SUCCESS.get(), new ResultTable<>(page.getRecords(), page.getTotal()), "查询成功");
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysUser> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

