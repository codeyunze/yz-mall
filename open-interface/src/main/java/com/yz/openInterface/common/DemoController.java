package com.yz.openInterface.common;

import com.yz.common.vo.Result;
import com.yz.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunze
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired(required = false)
    private RedisUtil redisUtil;

    @RequestMapping(value = "/a/{msg}")
    public Result a(@PathVariable(value = "msg") String msg) {
        redisUtil.insertOrUpdate("3xj-" + msg, msg);
        return Result.success(null, "service is normal!");
    }

    @RequestMapping(value = "/b/{msg}")
    public Result b(@PathVariable(value = "msg") String msg) {
        Object o = redisUtil.get("3xj-" + msg);
        return Result.success(o);
    }

}
