package com.yz.mall.pms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsCategoryAddDto;
import com.yz.mall.pms.dto.PmsCategoryQueryDto;
import com.yz.mall.pms.dto.PmsCategoryUpdateDto;
import com.yz.mall.pms.entity.PmsCategory;
import com.yz.mall.pms.service.PmsCategoryService;
import com.yz.mall.pms.vo.PmsCategoryVo;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 商品分类表(PmsCategory)表控制层
 *
 * @author yunze
 * @since 2025-01-XX
 */
@RestController
@RequestMapping("pms/category")
public class PmsCategoryController extends ApiController {

    private final PmsCategoryService service;

    public PmsCategoryController(PmsCategoryService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @SaCheckPermission("api:pms:category:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid PmsCategoryAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @SaCheckPermission("api:pms:category:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid PmsCategoryUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键 ID
     */
    @SaCheckPermission("api:pms:category:edit")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:pms:category:page")
    @PostMapping("page")
    public Result<ResultTable<PmsCategory>> page(@RequestBody @Valid PageFilter<PmsCategoryQueryDto> filter) {
        Page<PmsCategory> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("api:pms:category:page")
    @GetMapping("get/{id}")
    public Result<PmsCategoryVo> detail(@PathVariable Long id) {
        return success(this.service.detail(id));
    }

    /**
     * 查询所有分类（树形结构）
     */
    @SaCheckPermission("api:pms:category:page")
    @GetMapping("tree")
    public Result<List<PmsCategoryVo>> tree() {
        return success(this.service.tree());
    }

    /**
     * 根据父分类ID查询子分类列表
     *
     * @param parentId 父分类ID，0表示查询顶级分类
     */
    @SaCheckPermission("api:pms:category:page")
    @GetMapping("list")
    public Result<List<PmsCategoryVo>> listByParentId(@RequestParam(required = false, defaultValue = "0") Long parentId) {
        return success(this.service.listByParentId(parentId));
    }
}

