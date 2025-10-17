package com.elave.selfservicesupermarketdemo1.websocket;


import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@CrossOrigin(origins = "*")
@ServerEndpoint(value = "/websocket/{id}")
@Slf4j
public class WebSocketServer {
    /**
     * 记录当前在线连接数
     */
    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 存放所有在线的客户端
     */
    private static final Map<String, Session> onlineClients = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session , @PathParam("id") String id) throws IOException{
        onlineCount.incrementAndGet();
        onlineClients.put(id, session);
        log.info("有新连接加入：{}，当前在线客户端数为：{}", id, onlineCount.get());
        session.getBasicRemote().sendText("客户端id为：" + session.getId());
    }


    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("收到客户端消息：" + message);
        session.getBasicRemote().sendText("服务器收到消息：" + message);
    }

    @OnClose
    public void onClose(Session session) {
        onlineCount.decrementAndGet(); // 在线数减1
        onlineClients.remove(session.getId());
        log.info("有一连接关闭：{}，当前在线客户端数为：{}", session.getId(), onlineCount.get());

    }

    @OnError
    public void onError(Throwable t) {
        log.error("WebSocket 连接出错{}", t.getMessage());
    }
    /**
     * 群发消息
     *
     * @param message 消息内容
     */
    public static void sendAllMessage(String message) {
        //log.info("开始给在线的客户端{}群发消息{}",onlineClients,message);
        for (Map.Entry<String, Session> sessionEntry : onlineClients.entrySet()) {
            Session toSession = sessionEntry.getValue();
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getAsyncRemote().sendText(message);
        }
    }

    public static void  sendMessage(String id,String message){
        Session session = onlineClients.get(id);
        log.info("服务端给客户端[{}]发送消息{}", id, message);
        session.getAsyncRemote().sendText(message);
    }


}
