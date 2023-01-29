package com.yz.auth.business.baseUser.controller;


import com.yz.common.vo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 基础-用户表 前端控制器
 * </p>
 *
 * @author gaohan
 * @since 2022-09-13
 */
@RestController
@RequestMapping("/user")
public class BaseUserController {

    @GetMapping(value = "/getInfo")
    public Result getInfo(Authentication authentication) {
        return new Result(HttpStatus.OK.value(), authentication, HttpStatus.OK.getReasonPhrase());
    }
}

