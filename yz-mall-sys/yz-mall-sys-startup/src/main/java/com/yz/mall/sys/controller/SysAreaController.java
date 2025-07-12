package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysAreaAddDto;
import com.yz.mall.sys.dto.SysAreaQueryDto;
import com.yz.mall.sys.dto.SysAreaUpdateDto;
import com.yz.mall.sys.entity.SysArea;
import com.yz.mall.sys.service.SysAreaService;
import com.yz.mall.sys.vo.AreaVo;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * (SysArea)表控制层
 *
 * @author yunze
 * @since 2025-03-03 22:38:55
 */
@RestController
@RequestMapping("sys/area")
public class SysAreaController extends ApiController {

    /**
     * 服务对象
     */
    private final SysAreaService service;

    public SysAreaController(SysAreaService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid SysAreaAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysAreaUpdateDto dto) {
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
    public Result<ResultTable<SysArea>> page(@RequestBody @Valid PageFilter<SysAreaQueryDto> filter) {
        Page<SysArea> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 查询指定行政地区所下辖的行政区域
     *
     * @param parentId 指定要查询的行政地区编码
     */
    @SaIgnore
    @GetMapping("getRegionByParent/{parentId}")
    public Result<List<AreaVo>> getRegionByParent(@PathVariable String parentId) {
        return success(this.service.getByParentId(parentId));
    }

    /**
     * 查询指定行政区域的信息
     */
    @SaIgnore
    @GetMapping("get/{id}")
    public Result<SysArea> get(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

