package com.yz.mall.sys.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.mapper.SysUserMapper;
import com.yz.mall.sys.service.impl.SysUserServiceImpl;
import com.yz.mall.sys.vo.SysUserVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author yunze
 * @since 2025/7/10 23:35
 */
@ExtendWith(MockitoExtension.class) // 启用 Mockito
class SysUserServiceTest {

    @Mock
    private SysUserMapper userMapper; // 模拟依赖的 Mapper

    @InjectMocks
    private SysUserServiceImpl userService; // 注入被测试的 Service

    @Test
    @DisplayName("用户分页查询测试")
    void page() {
        // 1. 准备 Mock 数据
        int current = 1;
        int size = 10;

        Page<SysUserVo> voPage = new Page<>();
        voPage.setTotal(21);

        List<SysUserVo> mockUsers = new ArrayList<>();
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        for (int i = 0; i < 21; i++) {
            SysUserVo mockUser = new SysUserVo();
            mockUser.setId(1000L + i);
            mockUser.setEmail("test" + i + "@example.com");
            mockUser.setBalance(new BigDecimal("10" + i + ".00"));
            mockUser.setUsername("mockUser" + i);
            mockUser.setStatus(1);
            mockUser.setSex(i % 2 == 0 ? 1 : 0);
            mockUser.setCreateTime(fixedTime);
            mockUsers.add(mockUser);
        }
        int fromIndex = (current - 1) * size;
        int toIndex = Math.min(fromIndex + size, 21);
        voPage.setRecords(mockUsers.subList(fromIndex, toIndex));

        // 2. 定义 Mock 行为
        SysUserQueryDto queryDto = new SysUserQueryDto();

        when(userMapper.selectPage(any(), eq(queryDto))).thenReturn(voPage);

        // 3. 调用被测试方法
        Page<SysUserVo> result = userService.page(1L, 10L, queryDto);

        // 4. 验证结果
        assertEquals(21, result.getTotal());
        assertEquals("test2@example.com", result.getRecords().get(2).getEmail());
        // verify(userMapper, times(1)).selectPage(page, queryDto); // 验证调用次数
    }
}