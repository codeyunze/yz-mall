package com.yz.mall.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.OverallExceptionHandle;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.SysUserVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yunze
 * @since 2025/7/11 22:45
 */
@ExtendWith(MockitoExtension.class)
class SysUserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SysUserService userService;

    @InjectMocks
    private SysUserController userController; // 被测试的Controller

    @BeforeEach
    public void setup() {
        // 初始化MockMvc，只配置当前Controller
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new OverallExceptionHandle()) // 可选：添加异常处理器
                .build();
    }

    @Test
    @DisplayName("用户分页查询接口")
    void page() throws Exception {
        // 1. 准备Mock数据
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

        // 2. 定义Mock行为
        when(userService.page(1L, 10L, new SysUserQueryDto())).thenReturn(voPage);

        PageFilter<SysUserQueryDto> filter = new PageFilter<>();
        filter.setCurrent(1L);
        filter.setSize(10L);
        SysUserQueryDto queryDto = new SysUserQueryDto();
        filter.setFilter(queryDto);

        String params = JacksonUtil.getObjectMapper().writeValueAsString(filter);

        // 3. 发起请求并验证
        mockMvc.perform(post("/sys/user/page")
                        .contentType(MediaType.APPLICATION_JSON)  // 在perform()内部
                        .content(params))
                .andExpect(jsonPath("$.code").value(CodeEnum.SUCCESS.get()))
                .andExpect(jsonPath("$.data.total").value(21))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("用户分页查询接口-未授权")
    public void page_Unauthorized() throws Exception {
        PageFilter<SysUserQueryDto> filter = new PageFilter<>();
        filter.setCurrent(1L);
        filter.setSize(10L);
        SysUserQueryDto queryDto = new SysUserQueryDto();
        filter.setFilter(queryDto);

        String params = JacksonUtil.getObjectMapper().writeValueAsString(filter);

        // 未授权访问
        mockMvc.perform(post("/sys/user/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params))
                .andExpect(status().isUnauthorized());
    }
}