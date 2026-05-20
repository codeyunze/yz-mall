package com.yz.mall.sys.controller;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.sys.entity.Test;
import com.yz.mall.sys.service.TestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;


/**
 * (Test)表控制层
 *
 * @author yunze
 * @since 2026-04-20 19:23:22
 */
@RestController
@RequestMapping("sys/test")
public class TestController extends ApiController {

    /**
     * 服务对象
     */
    private final TestService service;

    public TestController(TestService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @RequestMapping("add")
    public Result<Boolean> insert(@RequestParam("id") Integer id
            , @RequestParam("tenantCode") String tenantCode
            , HttpServletRequest request) {
        request.getSession().setAttribute("tenantCode", tenantCode);

        Test test = new Test();
        test.setId(id);
        return success(this.service.save(test));
    }


    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<Test> page(@PathVariable Integer id) {
        return success(this.service.getById(id));
    }

}

