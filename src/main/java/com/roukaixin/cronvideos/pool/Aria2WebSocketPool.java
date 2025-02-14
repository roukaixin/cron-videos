package com.roukaixin.cronvideos.pool;


import org.springframework.web.socket.client.WebSocketConnectionManager;

import java.util.HashMap;
import java.util.Map;

public class Aria2WebSocketPool {

    private final Map<Long, WebSocketConnectionManager> webSocketConnectionManagerMap = HashMap.newHashMap(10);


    public void put(Long key, WebSocketConnectionManager manager) {
        this.webSocketConnectionManagerMap.put(key, manager);
    }

    public void remove(Long key) {
        webSocketConnectionManagerMap.remove(key);
    }

    public WebSocketConnectionManager getOrDefault(Long key) {
        return webSocketConnectionManagerMap.getOrDefault(key, null);
    }

}
