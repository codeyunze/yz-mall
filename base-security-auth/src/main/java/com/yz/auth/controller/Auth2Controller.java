package com.yz.auth.controller;

import com.gh.common.toolsclass.ResultData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : gaohan
 * @date : 2022/8/30 23:44
 */
@RestController
@RequestMapping(value = "/auth2")
public class Auth2Controller {

    @RequestMapping(value = "/test")
    public ResultData test(){
        return ResultData.success("auth2 access is successful");
    }
}
