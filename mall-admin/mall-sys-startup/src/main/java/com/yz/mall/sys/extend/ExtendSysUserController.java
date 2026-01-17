package com.yz.mall.sys.extend;


import cn.dev33.satoken.annotation.SaIgnore;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.IdsDto;
import com.yz.mall.base.Result;
import com.yz.mall.sys.dto.ExtendSysUserAddDto;
import com.yz.mall.sys.dto.ExtendSysUserBalanceDto;
import com.yz.mall.sys.service.ExtendSysUserService;
import com.yz.mall.sys.vo.ExtendLoginInfoVo;
import com.yz.mall.sys.vo.ExtendSysUserSlimVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 内部暴露接口：用户信息
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@RestController
@RequestMapping("extend/sys/user")
public class ExtendSysUserController extends ApiController {

    private final ExtendSysUserService service;

    public ExtendSysUserController(ExtendSysUserService service) {
        this.service = service;
    }

    /**
     * 扣减余额
     */
    @PostMapping("deduct")
    public Result<Boolean> deduct(@RequestBody @Valid ExtendSysUserBalanceDto dto) {
        service.deduct(dto.getUserId(), dto.getAmount());
        return success(true);
    }

    /**
     * 账户充值
     */
    @PostMapping("recharge")
    public Result<Boolean> recharge(@RequestBody @Valid ExtendSysUserBalanceDto dto) {
        service.recharge(dto.getUserId(), dto.getAmount());
        return success(true);
    }

    /**
     * 获取指定用户所拥有的角色
     *
     * @param userId 用户Id
     * @return 用户所拥有的角色
     */
    @GetMapping("getUserRoles/{userId}")
    public Result<List<Long>> getUserRoles(@PathVariable Long userId) {
        return success(service.getUserRoles(userId));
    }

    /**
     * 新增用户
     *
     * @param dto 新增用户信息数据
     * @return 主键Id
     */
    @SaIgnore
    @PostMapping("add")
    public Result<Long> add(@Valid @RequestBody ExtendSysUserAddDto dto) {
        return success(service.add(dto));
    }

    /**
     * 获取用户信息
     *
     * @param id 用户Id
     * @return 用户信息
     */
    @GetMapping("getUserInfo/{id}")
    public Result<ExtendLoginInfoVo> getUserInfo(@PathVariable Long id) {
        return success(this.service.getUserInfoById(id));
    }

    /**
     * 获取用户基础信息
     *
     * @param idsDto 用户 Id
     * @return 用户信息
     */
    @PostMapping("getUserSlimByIds")
    public Result<Map<Long, ExtendSysUserSlimVo>> getUserSlimByIds(@RequestBody IdsDto<Long> idsDto) {
        return success(service.getUserSlimByIds(idsDto));
    }
}

