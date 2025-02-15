package com.roukaixin.cronvideos.websocker;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.handler.Aria2Handler;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.Aria2ServerMapper;
import com.roukaixin.cronvideos.pojo.Aria2Server;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Aria2WebSocketClient implements CommandLineRunner {

    private final Aria2ServerMapper aria2ServerMapper;

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    private final Aria2WebSocketPool aria2WebSocketPool;

    private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;

    public Aria2WebSocketClient(Aria2ServerMapper aria2ServerMapper,
                                Aria2DownloadTasksMapper aria2DownloadTasksMapper,
                                Aria2WebSocketPool aria2WebSocketPool,
                                SmoothWeightedRoundRobin smoothWeightedRoundRobin) {
        this.aria2ServerMapper = aria2ServerMapper;
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
        this.aria2WebSocketPool = aria2WebSocketPool;
        this.smoothWeightedRoundRobin = smoothWeightedRoundRobin;
    }

    @Override
    public void run(String... args) {
        List<Aria2Server> aria2Servers = aria2ServerMapper.selectList(
                Wrappers.<Aria2Server>lambdaQuery().eq(Aria2Server::getIsOnline, 1));
        if (!aria2Servers.isEmpty()) {
            List<CompletableFuture<Boolean>> futures = aria2Servers.stream().map(aria2Server -> {
                String wsUri = "ws://" + aria2Server.getIp() + ":" + aria2Server.getPort() + "/jsonrpc";
                StandardWebSocketClient client = new StandardWebSocketClient();
                Aria2Handler handler = new Aria2Handler(
                        aria2Server.getId(),
                        aria2Server.getWeight(),
                        aria2DownloadTasksMapper,
                        aria2WebSocketPool,
                        aria2ServerMapper,
                        smoothWeightedRoundRobin
                );
                WebSocketConnectionManager manager = new WebSocketConnectionManager(
                        client,
                        handler,
                        wsUri
                );
                handler.setTimeout(10, TimeUnit.SECONDS);
                manager.start();
                return handler.getConnectionFuture().exceptionally(throwable -> {
                    aria2Server.setIsOnline(0);
                    aria2ServerMapper.updateById(aria2Server);
                    log.error("连接aria2服务器失败: {}", throwable.getMessage());
                    return false;
                });
            }).toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
                long successCount = futures.stream().filter(CompletableFuture::join).count();
                log.info("成功连接 {} 个aria2服务器", successCount);
            });
        }
    }
}
