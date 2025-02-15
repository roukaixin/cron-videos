package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.handler.Aria2Handler;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.Aria2ServerMapper;
import com.roukaixin.cronvideos.pojo.Aria2Server;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.Aria2ServerDTO;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import com.roukaixin.cronvideos.service.Aria2ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
public class Aria2ServerServiceImpl extends ServiceImpl<Aria2ServerMapper, Aria2Server>
        implements Aria2ServerService {

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    private final Aria2WebSocketPool aria2WebSocketPool;

    private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;

    public Aria2ServerServiceImpl(Aria2DownloadTasksMapper aria2DownloadTasksMapper,
                                  Aria2WebSocketPool aria2WebSocketPool,
                                  SmoothWeightedRoundRobin smoothWeightedRoundRobin) {
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
        this.aria2WebSocketPool = aria2WebSocketPool;
        this.smoothWeightedRoundRobin = smoothWeightedRoundRobin;
    }

    @Override
    public R<String> add(Aria2ServerDTO aria2ServerDto) {
        Aria2Server aria2Server = new Aria2Server();
        BeanUtils.copyProperties(aria2ServerDto, aria2Server);
        addWebSocketClient(aria2Server);
        this.save(aria2Server);
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
    public R<String> update(Long id, Aria2ServerDTO aria2ServerDto) {
        Aria2Server aria2Server = new Aria2Server();
        BeanUtils.copyProperties(aria2ServerDto, aria2Server);
        aria2Server.setId(id);
        WebSocketConnectionManager oldManager = aria2WebSocketPool.getOrDefault(id);
        if (oldManager != null) {
            oldManager.stop();
        }
        addWebSocketClient(aria2Server);
        this.updateById(aria2Server);
        return R.<String>builder().code(200).message("修改成功").build();
    }


    private void addWebSocketClient(Aria2Server aria2Server) {
        WebSocketClient client = new StandardWebSocketClient();
        Aria2Handler handler = new Aria2Handler(
                aria2Server.getId(),
                aria2Server.getWeight(),
                aria2DownloadTasksMapper,
                aria2WebSocketPool,
                this.baseMapper,
                smoothWeightedRoundRobin
        );
        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                client,
                handler,
                "ws://" + aria2Server.getIp() + ":" + aria2Server.getPort() + "/jsonrpc"
        );
        handler.setTimeout(1, TimeUnit.SECONDS);
        manager.start();
        CompletableFuture<Boolean> future = handler.getConnectionFuture().exceptionally(throwable -> {
            log.error("连接aria2服务器失败: {}", throwable.getMessage());
            return false;
        });
        if (future.join()) {
            aria2WebSocketPool.put(aria2Server.getId(), manager);
            aria2Server.setIsOnline(1);
        } else {
            aria2Server.setIsOnline(0);
        }
    }
}




