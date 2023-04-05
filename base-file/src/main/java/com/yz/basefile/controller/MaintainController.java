package com.yz.basefile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 服务测试
 * @author yunze
 * @version 1.0
 * @date 2023/4/3 12:53
 */
@Controller
@RequestMapping(value = "/maintain")
public class MaintainController {

    /**
     * 服务响应情况测试
     * @return
     */
    @RequestMapping("test")
    @ResponseBody
    public String test(){
        return "server response success!";
    }
}
