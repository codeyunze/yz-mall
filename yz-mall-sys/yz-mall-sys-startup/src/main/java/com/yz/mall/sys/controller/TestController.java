package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaIgnore;
import com.yz.mall.web.annotation.RepeatSubmit;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统-菜单资源表(SysMenu)表控制层
 *
 * @author yunze
 * @since 2024-11-21 23:29:01
 */
@RestController
@RequestMapping("/sys/test")
public class TestController extends ApiController {

    @Value("${test}")
    private String test;

    /**
     * 详情查询
     */
    @RepeatSubmit
    @SaIgnore
    @RequestMapping("get")
    public Result<String> page() {
        return success(test);
    }

}

