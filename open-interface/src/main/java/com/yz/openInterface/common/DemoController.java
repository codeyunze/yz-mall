package com.yz.openInterface.common;

import com.yz.common.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping(value = "/a")
    public Result a(){
        return Result.success(null, "service is normal!");
    }

}
