package com.yz.basefile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yunze
 * @version 1.0
 * @date 2023/4/3 12:53
 */
@Controller
@RequestMapping(value = "/maintain")
public class MaintainController {

    @RequestMapping("test")
    @ResponseBody
    public String test(){
        return "server response success!";
    }
}
