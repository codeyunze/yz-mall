package com.yz.mall.oms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.oms.dto.OmsOrderProductRelationAddDto;
import com.yz.mall.oms.dto.OmsOrderProductRelationQueryDto;
import com.yz.mall.oms.dto.OmsOrderProductRelationUpdateDto;
import com.yz.mall.oms.entity.OmsOrderProductRelation;
import com.yz.mall.oms.service.OmsOrderProductRelationService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 订单商品关联表(OmsOrderProductRelation)表控制层
 *
 * @author yunze
 * @since 2024-06-18 12:51:39
 */
@RestController
@RequestMapping("omsOrderProductRelation")
public class OmsOrderProductRelationController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private OmsOrderProductRelationService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid OmsOrderProductRelationAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid OmsOrderProductRelationUpdateDto dto) {
        return success(this.service.update(dto));
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
    public Result<List<OmsOrderProductRelation>> page(@RequestBody @Valid PageFilter<OmsOrderProductRelationQueryDto> filter) {
        Page<OmsOrderProductRelation> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<OmsOrderProductRelation> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

