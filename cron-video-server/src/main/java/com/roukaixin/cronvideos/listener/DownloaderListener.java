package com.roukaixin.cronvideos.listener;


import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.downloader.aria2.http.Aria2HttpClient;
import com.roukaixin.cronvideos.downloader.aria2.ws.Aria2WebSocketClient;
import com.roukaixin.cronvideos.enums.DownloaderProtocolEnum;
import com.roukaixin.cronvideos.enums.DownloaderTypeEnum;
import com.roukaixin.cronvideos.listener.event.DownloaderEvent;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import com.roukaixin.cronvideos.pooled.PooledClient;
import com.roukaixin.cronvideos.pooled.PooledDownloader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 下载器监听器
 *
 * @author roukaixin
 * @date 2026/1/13 19:09
 */
@Component
@Slf4j
public class DownloaderListener {

    private final PooledDownloader downloader;

    private final DownloaderMapper downloaderMapper;

    public DownloaderListener(PooledDownloader downloader,
                              DownloaderMapper downloaderMapper) {
        this.downloader = downloader;
        this.downloaderMapper = downloaderMapper;
    }

    @Async
    @EventListener(classes = DownloaderEvent.class)
    public void changePoole(DownloaderEvent event) {
        Downloader downloaderInfo = event.getDownloader();
        switch (event.getOperation()) {
            case QUERY, SAVE, UPDATE -> {
                PooledClient client = this.client(downloaderInfo);
                if (Objects.nonNull(client) && client.isValid() != (downloaderInfo.getIsOnline() == 1)) {
                    downloaderMapper.updateIsOnlineById(downloaderInfo.getId(), client.isValid() ? 1 : 0);
                }
                downloader.pushClient(client);
            }
            case DELETE -> {
            }
        }
    }

    private PooledClient client(Downloader downloaderInfo) {
        DownloaderTypeEnum type = downloaderInfo.getType();
        PooledClient client = null;
        switch (type) {
            case aria2 -> {
                DownloaderProtocolEnum protocol = downloaderInfo.getProtocol();
                if (protocol.equals(DownloaderProtocolEnum.ws) || protocol.equals(DownloaderProtocolEnum.wss)) {
                    Aria2WebSocketClient aria2WebSocketClient = new Aria2WebSocketClient(downloaderInfo);
                    client = new PooledClient(aria2WebSocketClient, downloader);
                } else if (protocol.equals(DownloaderProtocolEnum.http) || protocol.equals(DownloaderProtocolEnum.https)) {
                    Aria2HttpClient aria2HttpClient = new Aria2HttpClient(downloaderInfo);
                    client = new PooledClient(aria2HttpClient, downloader);
                }
            }
            case qbittorrent -> {

            }
        }
        return client;
    }

}
