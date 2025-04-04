package com.roukaixin.cronvideos.pool;


import com.roukaixin.cronvideos.client.DownloaderClient;

import java.util.HashMap;
import java.util.Map;

public class Aria2WebSocketPool {

    private final Map<Long, DownloaderClient> aria2ClientMap = HashMap.newHashMap(10);


    public void put(Long key, DownloaderClient aria2Client) {
        this.aria2ClientMap.put(key, aria2Client);
    }

    public void remove(Long key) {
        this.aria2ClientMap.remove(key);
    }

    public DownloaderClient get(Long key) {
        return this.aria2ClientMap.getOrDefault(key, null);
    }

}
