package com.yz.mall.sys.controller;

// import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.sys.dto.SysOpenClientAddDto;
import com.yz.mall.sys.dto.SysOpenClientAuthDto;
import com.yz.mall.sys.dto.SysOpenClientKeyUploadDto;
import com.yz.mall.sys.dto.SysOpenClientQueryDto;
import com.yz.mall.sys.dto.SysOpenClientUpdateDto;
import com.yz.mall.sys.entity.SysOpenClient;
import com.yz.mall.sys.entity.SysOpenClientAuth;
import com.yz.mall.sys.entity.SysOpenClientKey;
import com.yz.mall.sys.service.SysOpenClientService;
import com.yz.mall.sys.vo.SysOpenClientCreateVo;
import com.yz.mall.sys.vo.SysOpenClientDetailVo;
// import com.yz.mall.web.annotation.RepeatSubmit;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统-第三方开放客户端管理
 *
 * @author yunze
 */
@RestController
@RequestMapping("sys/open/client")
public class SysOpenClientController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysOpenClientService service;

    /**
     * 分页查询
     */
    // @SaCheckPermission("api:sys:open:client:page")
    @PostMapping("page")
    public Result<ResultTable<SysOpenClient>> page(@RequestBody @Valid PageFilter<SysOpenClientQueryDto> filter) {
        Page<SysOpenClient> page = service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询（含当前公钥摘要、授权列表）
     */
    // @SaCheckPermission("api:sys:open:client:detail")
    @GetMapping("get/{id}")
    public Result<SysOpenClientDetailVo> get(@PathVariable Long id) {
        return success(service.detail(id));
    }

    /**
     * 新增客户端
     */
    // @RepeatSubmit
    // @SaCheckPermission("api:sys:open:client:add")
    @PostMapping("add")
    public Result<SysOpenClientCreateVo> add(@RequestBody @Valid SysOpenClientAddDto dto) {
        return success(service.save(dto));
    }

    /**
     * 编辑客户端
     */
    // @RepeatSubmit
    // @SaCheckPermission("api:sys:open:client:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysOpenClientUpdateDto dto) {
        return success(service.update(dto));
    }

    /**
     * 切换客户端启停状态
     */
    // @RepeatSubmit
    // @SaCheckPermission("api:sys:open:client:status")
    @PostMapping("switch/{id}")
    public Result<Boolean> switchStatus(@PathVariable Long id) {
        return success(service.switchStatus(id));
    }

    /**
     * 删除客户端
     */
    // @RepeatSubmit
    // @SaCheckPermission("api:sys:open:client:delete")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(service.removeClient(id));
    }

    /**
     * 上传客户端公钥
     */
    // @RepeatSubmit
    // @SaCheckPermission("api:sys:open:client:key")
    @PostMapping("key/upload")
    public Result<Boolean> uploadKey(@RequestBody @Valid SysOpenClientKeyUploadDto dto) {
        return success(service.uploadKey(dto));
    }

    /**
     * 平台生成 SM2 密钥对（私钥仅此次返回）
     */
    // @RepeatSubmit
    // @SaCheckPermission("api:sys:open:client:key")
    @GetMapping("key/generate")
    public Result<Map<String, String>> generateKey(@RequestParam String clientId) {
        return success(service.generateKey(clientId));
    }

    /**
     * 查询客户端当前生效公钥
     */
    // @SaCheckPermission("api:sys:open:client:detail")
    @GetMapping("key/current/{clientId}")
    public Result<SysOpenClientKey> getCurrentKey(@PathVariable String clientId) {
        return success(service.getCurrentKey(clientId));
    }

    /**
     * 批量授予权限
     */
    // @RepeatSubmit
    // @SaCheckPermission("api:sys:open:client:auth")
    @PostMapping("auth/grant")
    public Result<Boolean> grantAuth(@RequestBody @Valid SysOpenClientAuthDto dto) {
        return success(service.grantAuth(dto));
    }

    /**
     * 撤销权限
     */
    // @RepeatSubmit
    // @SaCheckPermission("api:sys:open:client:auth")
    @PostMapping("auth/revoke")
    public Result<Boolean> revokeAuth(@RequestBody @Valid SysOpenClientAuthDto dto) {
        return success(service.revokeAuth(dto));
    }

    /**
     * 查询客户端有效授权列表
     */
    // @SaCheckPermission("api:sys:open:client:detail")
    @GetMapping("auth/list/{clientId}")
    public Result<List<SysOpenClientAuth>> listAuth(@PathVariable String clientId) {
        return success(service.listAuth(clientId));
    }
}
