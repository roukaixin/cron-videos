package com.roukaixin.cronvideos.websocker;


import com.roukaixin.cronvideos.handler.Aria2Handler;
import com.roukaixin.cronvideos.mapper.Aria2ServerMapper;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.pojo.Aria2Server;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.List;

@Component
public class Aria2WebSocketClient implements CommandLineRunner {

    private final Aria2ServerMapper aria2ServerMapper;

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    private final Aria2WebSocketPool aria2WebSocketPool;

    public Aria2WebSocketClient(Aria2ServerMapper aria2ServerMapper,
                                Aria2DownloadTasksMapper aria2DownloadTasksMapper,
                                Aria2WebSocketPool aria2WebSocketPool) {
        this.aria2ServerMapper = aria2ServerMapper;
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
        this.aria2WebSocketPool = aria2WebSocketPool;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Aria2Server> aria2Servers = aria2ServerMapper.selectList(null);
        if (!aria2Servers.isEmpty()) {
            aria2Servers.forEach(aria2Server -> {
                // ws://127.0.0.1:6800/jsonrpc
                String wsUri = "ws://" + aria2Server.getIp() + ":" + aria2Server.getPort() + "/jsonrpc";
                StandardWebSocketClient client = new StandardWebSocketClient();
                WebSocketConnectionManager manager = new WebSocketConnectionManager(
                        client,
                        new Aria2Handler(aria2DownloadTasksMapper, aria2Server.getId(), aria2WebSocketPool),
                        "ws://" + aria2Server.getIp() + ":" + aria2Server.getPort() + "/jsonrpc"
                );
                manager.start();
                aria2WebSocketPool.put(aria2Server.getId(), manager);
            });
        }
    }
}
