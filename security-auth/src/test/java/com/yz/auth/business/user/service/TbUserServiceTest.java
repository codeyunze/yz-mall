package com.yz.auth.business.user.service;

import com.yz.auth.business.user.entity.TbUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
class TbUserServiceTest {

    @Autowired
    private TbUserService userService;

    @Test
    void getByUsername() {
        String username = "fox";
        TbUser user = userService.getByUsername(username);
        Assert.assertNotNull(user.getId());
        log.info(user.toString());
    }
}