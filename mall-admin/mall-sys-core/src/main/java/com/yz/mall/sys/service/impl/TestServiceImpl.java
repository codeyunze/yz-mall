package com.yz.mall.sys.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.entity.Test;
import com.yz.mall.sys.mapper.TestMapper;
import com.yz.mall.sys.service.TestService;
import org.springframework.stereotype.Service;

/**
 * (Test)表服务实现类
 *
 * @author yunze
 * @since 2026-04-20 19:23:22
 */
@DS("#session.tenantCode")
@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {

}

