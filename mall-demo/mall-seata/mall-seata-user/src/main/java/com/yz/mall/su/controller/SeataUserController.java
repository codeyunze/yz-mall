package com.yz.mall.su.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.su.dto.SeataUserAddDto;
import com.yz.mall.su.dto.SeataUserQueryDto;
import com.yz.mall.su.dto.SeataUserUpdateDto;
import com.yz.mall.su.entity.SeataUser;
import com.yz.mall.su.service.SeataUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


/**
 * 分布式事务-用户表(SeataUser)表控制层
 *
 * @author yunze
 * @since 2024-06-18 00:00:00
 */
@RestController
@RequestMapping("seataUser")
public class SeataUserController extends ApiController {

    /**
     * 服务对象
     */
    private final SeataUserService service;

    public SeataUserController(SeataUserService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SeataUserAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SeataUserUpdateDto dto) {
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
    public Result<ResultTable<SeataUser>> page(@RequestBody @Valid PageFilter<SeataUserQueryDto> filter) {
        Page<SeataUser> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SeataUser> get(@PathVariable String id) {
        return success(this.service.getById(id));
    }

    /**
     * 扣减用户余额
     * 分布式事务接口
     */
    @PostMapping("decreaseBalance")
    public Result<Boolean> decreaseBalance(@RequestParam Long userId, @RequestParam Long amount) {
        return success(this.service.decreaseBalance(userId, amount));
    }
}