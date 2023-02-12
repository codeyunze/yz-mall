package com.yz.openinterface.business.shop.order.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @ClassName ShopOrderControllerTest
 * @Description TODO
 * @Author yunze
 * @Date 2023/2/12 22:14
 * @Version 1.0
 */
// 让JUnit运行Spring的测试环境,获得Spring环境的上下文的支持
@SpringBootTest
// 让JUnit运行Spring的测试环境,获得Spring环境的上下文的支持
@RunWith(SpringRunner.class)
// 用于自动配置MockMvc,配置后MockMvc类可以直接注入,相当于new MockMvc
@AutoConfigureMockMvc
class ShopOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void selectAll() throws Exception {
        System.out.println("---------------【selectAll】start---------------");
        String json = "{\n" +
                "    \"current\": 1,\n" +
                "    \"size\": 100,\n" +
                "    \"filter\": {\n" +
                "        \"orderNumber\": 29,\n" +
                "        \"productNumber\": 33\n" +
                "    }\n" +
                "\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/shopOrder/list")
                        .content(json.getBytes()) // 传json参数
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer ********-****-****-****-************")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
        System.out.println("---------------end---------------");
    }

    @Test
    void selectOne() {
        System.out.println("---------------【selectOne】start---------------");
        System.out.println("---------------end---------------");
    }
}