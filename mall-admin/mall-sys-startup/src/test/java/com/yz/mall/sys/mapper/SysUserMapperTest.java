package com.yz.mall.sys.mapper;

import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.vo.BaseUserVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author yunze
 * @since 2025/7/9 23:38
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
class SysUserMapperTest {

    @Autowired
    private SysUserMapper userMapper;

    @Test
    void get() {
        // 准备测试数据
        SysUser testUser = new SysUser();
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setBalance(new BigDecimal("100.00"));
        testUser.setUsername("testUser");
        testUser.setStatus(1);
        testUser.setSex(1);
        testUser.setInvalid(0L);
        testUser.setCreateTime(LocalDateTime.now().plusDays(-2));
        userMapper.insert(testUser);

        // 测试手机号查询
        BaseUserVo userVoByPhone = userMapper.get("13800138000");
        Assertions.assertNotNull(userVoByPhone);
        Assertions.assertEquals("13800138000", userVoByPhone.getPhone());
        log.info("手机号查询测试结果：{}", userVoByPhone);

        // 测试邮箱查询
        BaseUserVo userVoByEmail = userMapper.get("test@example.com");
        Assertions.assertNotNull(userVoByEmail);
        Assertions.assertEquals("test@example.com", userVoByEmail.getEmail());
        log.info("邮箱查询测试结果：{}", userVoByEmail);

    }

    @Test
    void deduct() {
    }

    @Test
    void recharge() {
    }

    @Test
    void selectPage() {
    }
}