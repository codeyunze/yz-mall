package com.yz.openinterface;

import com.yz.common.vo.Result;
import com.yz.openinterface.jms.producer.DirectProducer;
import com.yz.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yunze
 */
@RestController
@RequestMapping("/demo")
@Slf4j
public class TestController {

    @Autowired(required = false)
    private RedisUtil redisUtil;

    @RequestMapping(value = "/a/{msg}")
    public Result a(@PathVariable(value = "msg") String msg) {
        redisUtil.insertOrUpdate("3xj-" + msg, msg);
        return Result.success(null, "service is normal!");
    }

    @RequestMapping(value = "/b/{msg}")
    public Result b(@PathVariable(value = "msg") String msg, @RequestHeader("X-Request-color") String color, HttpServletRequest request) {
        Object o = redisUtil.get("3xj-" + msg);
        log.info(color);
        log.info(request.getHeader("X-Request-color"));
        return Result.success(o);
    }

    @Autowired
    private DirectProducer producer;

    @RequestMapping(value = "/c")
    public Result c() {
        for (int i = 0; i < 100; i++) {
            producer.sendMessage("order-" + i);
        }
        return Result.success();
    }

}
