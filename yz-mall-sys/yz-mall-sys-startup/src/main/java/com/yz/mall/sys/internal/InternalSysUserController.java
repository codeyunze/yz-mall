package com.yz.mall.sys.internal;


import cn.dev33.satoken.annotation.SaIgnore;
import com.yz.mall.sys.dto.InternalLoginInfoDto;
import com.yz.mall.sys.dto.InternalSysUserAddDto;
import com.yz.mall.sys.dto.InternalSysUserBalanceDto;
import com.yz.mall.sys.dto.InternalSysUserCheckLoginDto;
import com.yz.mall.sys.service.InternalSysUserService;
import com.yz.mall.sys.vo.InternalLoginInfoVo;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 内部暴露接口：用户信息
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@RestController
@RequestMapping("internal/sys/user")
public class InternalSysUserController extends ApiController {

    private final InternalSysUserService service;

    public InternalSysUserController(InternalSysUserService service) {
        this.service = service;
    }

    /**
     * 扣减余额
     */
    @PostMapping("deduct")
    public Result<Boolean> deduct(@RequestBody @Valid InternalSysUserBalanceDto dto) {
        service.deduct(dto.getUserId(), dto.getAmount());
        return success(true);
    }

    /**
     * 账户充值
     */
    @PostMapping("recharge")
    public Result<Boolean> recharge(@RequestBody @Valid InternalSysUserBalanceDto dto) {
        service.recharge(dto.getUserId(), dto.getAmount());
        return success(true);
    }

    /**
     * 登录校验
     */
    @SaIgnore
    @PostMapping("checkLogin")
    public Result<InternalLoginInfoDto> checkLogin(@RequestBody @Valid InternalSysUserCheckLoginDto dto) {
        InternalLoginInfoDto loginInfo = service.checkLogin(dto);
        return success(loginInfo);
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
    public Result<String> add(@Valid @RequestBody InternalSysUserAddDto dto) {
        return success(service.add(dto));
    }

    /**
     * 获取用户信息
     *
     * @param id 用户Id
     * @return 用户信息
     */
    @GetMapping("getUserInfo/{id}")
    public Result<InternalLoginInfoVo> getUserInfo(@PathVariable Long id) {
        return success(this.service.getUserInfoById(id));
    }
}

