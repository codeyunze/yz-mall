package com.yz.cases.mall.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.cases.mall.dto.BaseUserAddDto;
import com.yz.cases.mall.dto.BaseUserQueryDto;
import com.yz.cases.mall.dto.BaseUserUpdateDto;
import com.yz.cases.mall.entity.BaseUser;
import com.yz.cases.mall.service.BaseUserService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 基础-用户(BaseUser)表控制层
 *
 * @author yunze
 * @since 2024-06-11 23:16:13
 */
@RestController
@RequestMapping("base/user")
public class BaseUserController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private BaseUserService service;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result<Long> insert(@RequestBody @Valid BaseUserAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody @Valid BaseUserUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<List<BaseUser>> page(@RequestBody @Valid PageFilter<BaseUserQueryDto> filter) {
        Page<BaseUser> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<BaseUser> page(@PathVariable Long id) {
        return success(this.service.getById(id));
    }

}

