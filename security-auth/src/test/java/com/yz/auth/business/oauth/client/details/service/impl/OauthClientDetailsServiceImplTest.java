package com.yz.auth.business.oauth.client.details.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.auth.business.oauth.client.details.entity.OauthClientDetails;
import com.yz.auth.business.oauth.client.details.service.OauthClientDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
class OauthClientDetailsServiceImplTest {

    @Autowired
    private OauthClientDetailsService oauthClientDetailsService;

    private ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void getClients() throws JsonProcessingException {
        List<OauthClientDetails> clients = oauthClientDetailsService.getClients();

        String str = MAPPER.writeValueAsString(clients);

        log.info("===> result: {}", str);
    }
}