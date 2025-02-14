package com.roukaixin.cronvideos.pool;


import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;

import java.util.HashMap;
import java.util.Map;

public class Aria2WebSocketPool {

    private final Map<Long, WebSocketConnectionManager> webSocketConnectionManagerMap = HashMap.newHashMap(10);

    private final Map<WebSocketSession, Long> webSocketSessionMap = HashMap.newHashMap(10);


    public void put(Long key, WebSocketConnectionManager manager) {
        this.webSocketConnectionManagerMap.put(key, manager);
    }

    public void remove(Long key) {
        this.webSocketConnectionManagerMap.remove(key);
    }

    public WebSocketConnectionManager getOrDefault(Long key) {
        return this.webSocketConnectionManagerMap.getOrDefault(key, null);
    }

    public Long getAria2ServiceId(WebSocketSession session) {
        return webSocketSessionMap.get(session);
    }

    public void putSession(WebSocketSession session, Long id) {
        this.webSocketSessionMap.put(session, id);
    }

    public void removeSession(WebSocketSession session) {
        this.webSocketSessionMap.remove(session);
    }
}
