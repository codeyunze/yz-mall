package com.yz.mall.sys.feign;

import com.yz.mall.sys.dto.InternalLoginInfoDto;
import com.yz.mall.sys.dto.InternalSysUserAddDto;
import com.yz.mall.sys.dto.InternalSysUserBalanceDto;
import com.yz.mall.sys.dto.InternalSysUserCheckLoginDto;
import com.yz.mall.sys.vo.InternalLoginInfoVo;
import com.yz.mall.web.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 内部暴露接口: 用户信息
 *
 * @author yunze
 * @date 2024/6/22 23:44
 */
@FeignClient(name = "yz-mall-sys", contextId = "internalSysUser", path = "internal/sys/user")
public interface InternalSysUserFeign {

    /**
     * 扣减余额
     */
    @PostMapping("deduct")
    Result<Boolean> deduct(@RequestBody @Valid InternalSysUserBalanceDto dto);

    /**
     * 账户充值
     */
    @PostMapping("recharge")
    Result<Boolean> recharge(@RequestBody @Valid InternalSysUserBalanceDto dto);

    /**
     * 登录校验
     */
    @PostMapping("checkLogin")
    Result<InternalLoginInfoDto> checkLogin(@RequestBody @Valid InternalSysUserCheckLoginDto dto);

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
    Result<String> add(@Valid @RequestBody InternalSysUserAddDto dto);

    /**
     * 获取用户信息
     *
     * @param id 用户Id
     * @return 用户信息
     */
    @GetMapping("getUserInfo/{id}")
    Result<InternalLoginInfoVo> getUserInfo(@PathVariable Long id);
}
