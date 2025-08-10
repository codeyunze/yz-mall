package com.yz.mall.sys.feign;

import com.yz.mall.base.Result;
import com.yz.mall.sys.dto.ExtendLoginInfoDto;
import com.yz.mall.sys.dto.ExtendSysUserAddDto;
import com.yz.mall.sys.dto.ExtendSysUserBalanceDto;
import com.yz.mall.sys.dto.ExtendSysUserCheckLoginDto;
import com.yz.mall.sys.vo.ExtendLoginInfoVo;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 内部暴露接口: 用户信息
 *
 * @author yunze
 * @date 2024/6/22 23:44
 */
@FeignClient(name = "yz-mall-sys", contextId = "extendSysUser", path = "extend/sys/user")
public interface ExtendSysUserFeign {

    /**
     * 扣减余额
     */
    @PostMapping("deduct")
    Result<Boolean> deduct(@RequestBody @Valid ExtendSysUserBalanceDto dto);

    /**
     * 账户充值
     */
    @PostMapping("recharge")
    Result<Boolean> recharge(@RequestBody @Valid ExtendSysUserBalanceDto dto);

    /**
     * 登录校验
     */
    @PostMapping("checkLogin")
    Result<ExtendLoginInfoDto> checkLogin(@RequestBody @Valid ExtendSysUserCheckLoginDto dto);

    /**
     * 获取指定用户所拥有的角色
     *
     * @param userId 用户Id
     * @return 用户所拥有的角色
     */
    @GetMapping("getUserRoles/{userId}")
    Result<List<Long>> getUserRoles(@PathVariable Long userId);

    /**
     * 新增用户
     *
     * @param dto 新增用户信息数据
     * @return 主键Id
     */
    @PostMapping("add")
    Result<Long> add(@Valid @RequestBody ExtendSysUserAddDto dto);

    /**
     * 获取用户信息
     *
     * @param id 用户Id
     * @return 用户信息
     */
    @GetMapping("getUserInfo/{id}")
    Result<ExtendLoginInfoVo> getUserInfo(@PathVariable Long id);
}
