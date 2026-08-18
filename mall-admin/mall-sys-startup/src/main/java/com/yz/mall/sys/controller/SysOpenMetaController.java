package com.yz.mall.sys.controller;

// import cn.dev33.satoken.annotation.SaCheckPermission;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.sys.service.SysOpenClientService;
import com.yz.mall.sys.vo.SysOpenPermissionOptionVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第三方开放平台元数据（服务端公钥、可授权权限码清单）
 *
 * @author yunze
 */
@RestController
@RequestMapping("sys/open")
public class SysOpenMetaController extends ApiController {

    @Resource
    private SysOpenClientService service;

    /**
     * 下载平台服务端公钥，供第三方加密 SM4 会话密钥
     */
    // @SaCheckPermission("api:sys:open:server-public-key")
    @GetMapping("server-public-key")
    public Result<Map<String, String>> serverPublicKey() {
        Map<String, String> data = new HashMap<>(2);
        data.put("serverPublicKey", service.getServerPublicKey());
        return success(data);
    }

    /**
     * 系统预置可授权开放 API 清单
     */
    // @SaCheckPermission("api:sys:open:client:auth")
    @GetMapping("permission/options")
    public Result<List<SysOpenPermissionOptionVo>> permissionOptions() {
        return success(service.listPermissionOptions());
    }
}
