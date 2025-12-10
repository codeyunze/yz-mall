package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.sys.dto.SysDictionaryAddDto;
import com.yz.mall.sys.dto.SysDictionaryQueryDto;
import com.yz.mall.sys.dto.SysDictionaryUpdateDto;
import com.yz.mall.sys.entity.SysDictionary;
import com.yz.mall.sys.service.SysDictionaryService;
import com.yz.mall.sys.vo.ExtendSysDictionaryVo;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
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
    @SaCheckPermission("api:system:dictionary:update")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysDictionaryAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @SaCheckPermission("api:system:dictionary:update")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysDictionaryUpdateDto dto) {
        boolean updated = this.service.update(dto);
        return updated ? success(true) : Result.error(false, "数据字典更新失败");
    }

    /**
     * 删除
     *
     * @param id 删除数据主键 ID
     */
    @SaCheckPermission("api:system:dictionary:update")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<ResultTable<ExtendSysDictionaryVo>> page(@RequestBody @Valid PageFilter<SysDictionaryQueryDto> filter) {
        Page<ExtendSysDictionaryVo> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<ExtendSysDictionaryVo> get(@PathVariable String id) {
        SysDictionary bo = this.service.getById(id);
        ExtendSysDictionaryVo vo = new  ExtendSysDictionaryVo();
        BeanUtils.copyProperties(bo, vo);
        return success(vo);
    }

    /**
     * 数据字典查询
     */
    @GetMapping("getByKey/{key}")
    public Result<ExtendSysDictionaryVo> getByKey(@PathVariable String key) {
        return success(this.service.getByKey(key));
    }
}

