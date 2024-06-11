package com.yz.cases.mall.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yunze
 * @date 2024/6/10 15:25
 */
@RestController
@RequestMapping(value = "/db")
public class DataSourceController {

    @Resource
    private DynamicRoutingDataSource dataSource;

    @RequestMapping("createSource")
    public String createSource() {
        DruidDataSource db = new DruidDataSource();
        db.setUsername("root");
        db.setPassword("123456");
        db.setDriverClassName("com.mysql.cj.jdbc.Driver");
        db.setUrl("jdbc:mysql://127.0.0.1:3306/mall_product?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF8&useSSL=false&allowMultiQueries=true");
        dataSource.addDataSource("mall_product", db);
        return "success";
    }
}
