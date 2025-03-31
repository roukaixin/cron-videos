package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.handler.Aria2Handler;
import com.roukaixin.cronvideos.mapper.DownloadTaskMapper;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.DownloaderDTO;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import com.roukaixin.cronvideos.service.DownloaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

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

    private final DownloadTaskMapper downloadTaskMapper;

    private final Aria2WebSocketPool aria2WebSocketPool;

    private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;

    private final ApplicationContext applicationContext;

    public DownloaderServiceImpl(DownloadTaskMapper downloadTaskMapper,
                                 Aria2WebSocketPool aria2WebSocketPool,
                                 SmoothWeightedRoundRobin smoothWeightedRoundRobin,
                                 ApplicationContext applicationContext) {
        this.downloadTaskMapper = downloadTaskMapper;
        this.aria2WebSocketPool = aria2WebSocketPool;
        this.smoothWeightedRoundRobin = smoothWeightedRoundRobin;
        this.applicationContext = applicationContext;
    }

    @Override
    public R<String> add(DownloaderDTO downloaderDto) {
        Downloader downloader = new Downloader();
        BeanUtils.copyProperties(downloaderDto, downloader);
        addWebSocketClient(downloader);
        this.save(downloader);
        return R.<String>builder().code(200).message("添加成功").build();
    }


    @Override
    public R<String> delete(Long id) {
        this.removeById(id);
        WebSocketConnectionManager manager = aria2WebSocketPool.getOrDefault(id);
        if (manager != null) {
            manager.stop();
        }
        return R.<String>builder().code(200).message("删除成功").build();
    }

    @Override
    public R<String> update(Long id, DownloaderDTO downloaderDto) {
        Downloader downloader = new Downloader();
        BeanUtils.copyProperties(downloaderDto, downloader);
        downloader.setId(id);
        WebSocketConnectionManager oldManager = aria2WebSocketPool.getOrDefault(id);
        if (oldManager != null) {
            oldManager.stop();
        }
        addWebSocketClient(downloader);
        this.updateById(downloader);
        return R.<String>builder().code(200).message("修改成功").build();
    }


    private void addWebSocketClient(Downloader downloader) {
        WebSocketClient client = new StandardWebSocketClient();
        Aria2Handler handler = new Aria2Handler(
                applicationContext,
                downloader.getId(),
                downloader.getWeight(),
                downloadTaskMapper,
                aria2WebSocketPool,
                this.baseMapper,
                smoothWeightedRoundRobin
        );
        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                client,
                handler,
                "ws://" + downloader.getHost() + ":" + downloader.getPort() + "/jsonrpc"
        );
        handler.setTimeout(1, TimeUnit.SECONDS);
        manager.start();
        CompletableFuture<Boolean> future = handler.getConnectionFuture().exceptionally(throwable -> {
            log.error("连接aria2服务器失败: {}", throwable.getMessage());
            return false;
        });
        if (future.join()) {
            aria2WebSocketPool.put(downloader.getId(), manager);
            downloader.setIsOnline(1);
        } else {
            downloader.setIsOnline(0);
        }
    }
}




