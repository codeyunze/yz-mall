package com.yz.auth.config;

import com.alibaba.fastjson.JSON;
import com.yz.common.vo.Result;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功处理逻辑
 */
@Configuration
public class CustomizeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //更新用户表上次登录时间、更新人、更新时间等字段
        /*User userDetails = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser sysUser = sysUserService.selectByName(userDetails.getUsername());
        sysUser.setLastLoginTime(new Date());
        sysUser.setUpdateTime(new Date());
        sysUser.setUpdateUser(sysUser.getId());
        sysUserService.update(sysUser);*/

        //此处还可以进行一些处理，比如登录成功之后可能需要返回给前台当前用户有哪些菜单权限，
        //进而前台动态的控制菜单的显示等，具体根据自己的业务需求进行扩展

        //返回json数据
        // Result result = Result.success(authentication.getAuthorities());
        //处理编码方式，防止中文乱码的情况
        // response.setContentType("text/json;charset=utf-8");
        //塞到HttpServletResponse中返回给前台
        // response.getWriter().write(JSON.toJSONString(result));
        // response.sendRedirect("http://www.baidu.com");
    }
}
