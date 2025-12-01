package com.yz.mall.sys.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.sys.dto.SysDictionaryAddDto;
import com.yz.mall.sys.dto.SysDictionaryQueryDto;
import com.yz.mall.sys.dto.SysDictionaryUpdateDto;
import com.yz.mall.sys.entity.SysDictionary;
import com.yz.mall.sys.service.SysDictionaryService;
import com.yz.mall.sys.vo.ExtendSysDictionaryVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


/**
 * 系统-数据字典管理
 *
 * @author yunze
 * @since 2025-11-30 09:53:52
 */
@RestController
@RequestMapping("sys/dictionary")
public class SysDictionaryController extends ApiController {

    /**
     * 服务对象
     */
    private final SysDictionaryService service;

    public SysDictionaryController(SysDictionaryService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysDictionaryAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysDictionaryUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键 ID
     */
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<ResultTable<SysDictionary>> page(@RequestBody @Valid PageFilter<SysDictionaryQueryDto> filter) {
        Page<SysDictionary> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysDictionary> get(@PathVariable String id) {
        return success(this.service.getById(id));
    }

    /**
     * 数据字典查询
     */
    @GetMapping("getByKey/{key}")
    public Result<ExtendSysDictionaryVo> getByKey(@PathVariable String key) {
        return success(this.service.getByKey(key));
    }
}

