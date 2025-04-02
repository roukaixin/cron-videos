package com.roukaixin.cronvideos.runner;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.handler.Aria2Handler;
import com.roukaixin.cronvideos.mapper.DownloadTaskMapper;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ConnectionAria2Runner implements CommandLineRunner {

    private final DownloaderMapper downloaderMapper;

    private final DownloadTaskMapper downloadTaskMapper;

    private final Aria2WebSocketPool aria2WebSocketPool;

    private final ApplicationContext applicationContext;

    public ConnectionAria2Runner(DownloaderMapper downloaderMapper,
                                 DownloadTaskMapper downloadTaskMapper,
                                 Aria2WebSocketPool aria2WebSocketPool,
                                 ApplicationContext applicationContext) {
        this.downloaderMapper = downloaderMapper;
        this.downloadTaskMapper = downloadTaskMapper;
        this.aria2WebSocketPool = aria2WebSocketPool;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        List<Downloader> downloaderList = downloaderMapper.selectList(
                Wrappers.<Downloader>lambdaQuery().eq(Downloader::getIsOnline, 1));
        if (!downloaderList.isEmpty()) {
            // 配置下载器。
            List<CompletableFuture<Boolean>> futures = downloaderList.stream().map(aria2Server -> {
                String wsUri = "ws://" + aria2Server.getHost() + ":" + aria2Server.getPort() + "/jsonrpc";
                StandardWebSocketClient client = new StandardWebSocketClient();
                Aria2Handler handler = new Aria2Handler(
                        applicationContext,
                        aria2Server.getId(),
                        aria2Server.getWeight(),
                        downloadTaskMapper,
                        aria2WebSocketPool,
                        downloaderMapper
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
                    downloaderMapper.updateById(aria2Server);
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
