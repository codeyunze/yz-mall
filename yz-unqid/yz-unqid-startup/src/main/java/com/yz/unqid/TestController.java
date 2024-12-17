package com.yz.unqid;


import com.yz.tools.ApiController;
import com.yz.tools.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统-流水号表(SysUnqid)表控制层
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@RefreshScope
@RestController
@RequestMapping("sys/unqid/")
public class TestController extends ApiController {

    @Value("${message:test}")
    private String message;

    @Value("${abc:abc}")
    private String abc;

    @RequestMapping("test")
    public Result<String> test() {
        return Result.success(message);
    }

    @RequestMapping("abc")
    public Result<String> abc() {
        return success(abc);
    }

}

