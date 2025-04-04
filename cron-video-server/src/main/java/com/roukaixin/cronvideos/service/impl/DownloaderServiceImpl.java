package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.client.Aria2DownloaderClient;
import com.roukaixin.cronvideos.client.DownloaderClient;
import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.DownloaderDTO;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import com.roukaixin.cronvideos.service.DownloaderService;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author pankx
 * @description 针对表【aria2_connection(aria2 连接信息)】的数据库操作Service实现
 */
@Service
@Slf4j
public class DownloaderServiceImpl extends ServiceImpl<DownloaderMapper, Downloader>
        implements DownloaderService {

    private final Aria2WebSocketPool aria2WebSocketPool;

    public DownloaderServiceImpl(Aria2WebSocketPool aria2WebSocketPool) {
        this.aria2WebSocketPool = aria2WebSocketPool;
    }

    @Override
    public R<String> add(DownloaderDTO downloaderDto) {
        Downloader downloader = new Downloader();
        BeanUtils.copyProperties(downloaderDto, downloader);
        this.save(downloader);
        CompletableFuture.runAsync(() -> addWebSocketClient(downloader));
        return R.<String>builder().code(200).message("添加成功").build();
    }


    @Override
    public R<String> delete(Long id) {
        this.removeById(id);
        DownloaderClient aria2Client =  aria2WebSocketPool.get(id);
        if (aria2Client != null) {
            aria2Client.stop();
        }
        return R.<String>builder().code(200).message("删除成功").build();
    }

    @Override
    public R<String> update(Long id, DownloaderDTO downloaderDto) {
        Downloader downloader = new Downloader();
        BeanUtils.copyProperties(downloaderDto, downloader);
        downloader.setId(id);
        DownloaderClient downloaderClient =  aria2WebSocketPool.get(id);
        if (downloaderClient != null) {
            downloaderClient.stop();
        }
        aria2WebSocketPool.remove(id);
        addWebSocketClient(downloader);
        this.updateById(downloader);
        return R.<String>builder().code(200).message("修改成功").build();
    }

    private void addWebSocketClient(Downloader downloader) {
        WebSocketClient aria2Client = new Aria2DownloaderClient(URI.create(downloader.getProtocol() + "://" + downloader.getHost() + ":" + downloader.getPort() + "/jsonrpc"), downloader.getId(), downloader.getWeight());
        aria2Client.setConnectionLostTimeout(0);
        try {
            if (aria2Client.connectBlocking(2, TimeUnit.SECONDS)) {
                SmoothWeightedRoundRobin.getInstance().put(downloader.getId(), downloader.getWeight());
                aria2WebSocketPool.put(downloader.getId(), (DownloaderClient) aria2Client);
                downloader.setIsOnline(1);
                updateById(downloader);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}




