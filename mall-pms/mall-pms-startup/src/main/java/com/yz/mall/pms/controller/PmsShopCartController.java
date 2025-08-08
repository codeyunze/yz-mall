package com.yz.mall.pms.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsShopCartAddDto;
import com.yz.mall.pms.dto.PmsShopCartQueryDto;
import com.yz.mall.pms.dto.PmsShopCartUpdateDto;
import com.yz.mall.pms.service.PmsShopCartService;
import com.yz.mall.pms.vo.PmsShopCartVo;
import com.yz.mall.base.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 购物车数据表(PmsShopCart)表控制层
 *
 * @author yunze
 * @since 2025-01-24 10:08:17
 */
@RestController
@RequestMapping("pms/cart")
public class PmsShopCartController extends ApiController {

    /**
     * 服务对象
     */
    private final PmsShopCartService service;

    public PmsShopCartController(PmsShopCartService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @SaCheckPermission("api:pms:cart:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid PmsShopCartAddDto dto) {
        dto.setUserId(StpUtil.getLoginIdAsLong());
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid PmsShopCartUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     */
    @SaCheckPermission("api:pms:cart:edit")
    @DeleteMapping("delete")
    public Result<Boolean> delete(@RequestBody IdsDto dto) {
        long userId = StpUtil.getLoginIdAsLong();
        return success(this.service.removeByIds(dto.getIds(), userId));
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:pms:cart:edit")
    @PostMapping("page")
    public Result<ResultTable<PmsShopCartVo>> page(@RequestBody @Valid PageFilter<PmsShopCartQueryDto> filter) {
        filter.getFilter().setUserId(StpUtil.getLoginIdAsLong());
        Page<PmsShopCartVo> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }
}

