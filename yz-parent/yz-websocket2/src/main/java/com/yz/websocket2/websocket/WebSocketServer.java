package com.yz.websocket2.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ServerEndpoint("/wsclient/{ucCode}")
public class WebSocketServer {

    private static CopyOnWriteArrayList<Session> sessionList = new CopyOnWriteArrayList<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("ucCode") String ucCode) {
        // 建立连接时触发，可以根据ucCode进行处理
        System.out.println("新连接加入，ucCode：" + ucCode);
        sessionList.add(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        // 处理收到的消息
        System.out.println("收到消息：" + message);

        // 广播消息给所有连接的客户端
        for (Session s : sessionList) {
            try {
                s.getBasicRemote().sendText("收到消息：" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        // 关闭连接时触发
        System.out.println("连接关闭");
        sessionList.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // 发生错误时触发
        throwable.printStackTrace();
    }


}
