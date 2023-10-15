package com.yz.websocket;

import com.yz.websocket.socket.service.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author : yunze
 * @date : 2023/9/19 17:35
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @RequestMapping(value = "get")
    public String get() {
        return "OK";
    }

    @Autowired
    private WebSocket webSocket;

    @RequestMapping(value = "/send")
    public void send(String uid, String msg) throws IOException {
        webSocket.sendMessage(uid, msg);
    }
}
