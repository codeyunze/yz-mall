package com.yz.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : yunze
 * @date : 2023/1/21 23:28
 */
@Controller
@RequestMapping(value = "/auth")
public class AuthController {

    @RequestMapping(value = "/redirect")
    public String redirect(String redirect, String code){
        return "redirect:" + redirect + "?code=" + code;
    }
}
