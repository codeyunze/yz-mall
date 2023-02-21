package com.yz.auth.business;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestController
 * @Description TODO
 * @Author yunze
 * @Date 2023/2/13 23:44
 * @Version 1.0
 */
@Controller
@RequestMapping
public class TestController {

    @RequestMapping(value = "/demo")
    @ResponseBody
    public String demo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {

        }
        return "spring security demo";
    }

    @RequestMapping("/main")
    public String main() {
        return "redirect:/main.html";
    }

    @RequestMapping("/toerror")
    public String error() {
        return "redirect:/error.html";
    }
}
