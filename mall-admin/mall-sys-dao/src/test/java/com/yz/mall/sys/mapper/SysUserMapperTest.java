package com.yz.mall.sys.mapper;

import com.yz.mall.sys.BaseMapperTest;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.vo.BaseUserVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yunze
 * @since 2025/7/9 23:38
 */
@Slf4j
// @ActiveProfiles("test")
class SysUserMapperTest extends BaseMapperTest {

    @Resource
    private SysUserMapper userMapper;

    @Test
    @DisplayName("插入一条测试数据并读取")
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
        int inserted = userMapper.insert(testUser);
        assertThat(inserted).isEqualTo(1);
        assertThat(testUser.getId()).isNotNull();

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
    @DisplayName("扣减余额测试")
    void deduct() {
        String phone = "15300000017";
        BaseUserVo userVo = userMapper.get(phone);
        assert userVo != null;

        Long userId = userVo.getId();
        BigDecimal balance = userVo.getBalance();

        userMapper.deduct(userId, new BigDecimal("10.00"));
        BaseUserVo after = userMapper.get(phone);
        Assertions.assertNotNull(after);
        Assertions.assertEquals(balance.subtract(new BigDecimal("10.00")), after.getBalance());

        log.info("用户余额应该为：{}，实际为：{}", balance.subtract(new BigDecimal("10.00")), after.getBalance());
    }

    @Test
    void recharge() {
    }

    @Test
    void selectPage() {
    }
}