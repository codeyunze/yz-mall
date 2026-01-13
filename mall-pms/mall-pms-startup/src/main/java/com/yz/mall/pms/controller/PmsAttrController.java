package com.yz.mall.pms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsAttrAddDto;
import com.yz.mall.pms.dto.PmsAttrQueryDto;
import com.yz.mall.pms.dto.PmsAttrUpdateDto;
import com.yz.mall.pms.entity.PmsAttr;
import com.yz.mall.pms.service.PmsAttrService;
import com.yz.mall.pms.vo.PmsAttrVo;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 商品规格属性表(PmsAttr)表控制层
 *
 * @author yunze
 * @since 2025-01-XX
 */
@RestController
@RequestMapping("pms/attr")
public class PmsAttrController extends ApiController {

    private final PmsAttrService service;

    public PmsAttrController(PmsAttrService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @SaCheckPermission("api:pms:attr:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid PmsAttrAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @SaCheckPermission("api:pms:attr:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid PmsAttrUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键 ID
     */
    @SaCheckPermission("api:pms:attr:edit")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:pms:attr:page")
    @PostMapping("page")
    public Result<ResultTable<PmsAttr>> page(@RequestBody @Valid PageFilter<PmsAttrQueryDto> filter) {
        Page<PmsAttr> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("api:pms:attr:page")
    @GetMapping("get/{id}")
    public Result<PmsAttrVo> get(@PathVariable Long id) {
        return success(this.service.detail(id));
    }

    /**
     * 根据关联ID查询属性列表
     */
    @SaCheckPermission("api:pms:attr:page")
    @GetMapping("list/{relatedId}")
    public Result<List<PmsAttrVo>> listByRelatedId(@PathVariable Long relatedId) {
        return success(this.service.listByRelatedId(relatedId));
    }

}
