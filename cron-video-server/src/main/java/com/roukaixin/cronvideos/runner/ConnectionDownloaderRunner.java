package com.roukaixin.cronvideos.runner;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.client.Aria2DownloaderClient;
import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.enums.DownloaderProtocolEnum;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ConnectionDownloaderRunner implements CommandLineRunner {

    private final DownloaderMapper downloaderMapper;

    private final Aria2WebSocketPool aria2WebSocketPool;


    public ConnectionDownloaderRunner(DownloaderMapper downloaderMapper,
                                 Aria2WebSocketPool aria2WebSocketPool) {
        this.downloaderMapper = downloaderMapper;
        this.aria2WebSocketPool = aria2WebSocketPool;
    }

    @Override
    public void run(String... args) {
        List<Downloader> downloaderList = downloaderMapper.selectList(
                Wrappers.<Downloader>lambdaQuery().eq(Downloader::getIsOnline, 1));
        if (!downloaderList.isEmpty()) {
            // 配置下载器。
            downloaderList.forEach(d -> {
                switch (d.getType()) {
                    case aria2 -> {
                        if (d.getProtocol().equals(DownloaderProtocolEnum.ws) || d.getProtocol().equals(DownloaderProtocolEnum.wss)) {
                            CompletableFuture.runAsync(() -> {
                                Aria2DownloaderClient aria2Client = new Aria2DownloaderClient(
                                        URI.create(d.getProtocol() + "://" + d.getHost() + ":" + d.getPort() + "/jsonrpc"),
                                        d.getId(),
                                        d.getWeight()
                                );

                                try {
                                    if (aria2Client.start(2, TimeUnit.SECONDS)) {
                                        aria2WebSocketPool.put(d.getId(), aria2Client);
                                        SmoothWeightedRoundRobin.getInstance().put(d.getId(), d.getWeight());
                                    }
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }
                    case qbittorrent -> {

                    }
                }

            });
        }
    }
}
