package com.roukaixin.cronvideos.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.listener.event.Aria2Task;
import com.roukaixin.cronvideos.listener.event.DownloadTaskStatus;
import com.roukaixin.cronvideos.utils.EventUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Aria2DownloaderClient extends WebSocketClient implements DownloaderClient {

    // downloader id
    private final Long id;

    private final int weight;

    public Aria2DownloaderClient(URI serverUri, @NonNull Long id, int weight) {
        super(serverUri);
        this.id = id;
        this.weight = weight;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("打开连接 {}", id);
        // 添加到负载均衡算法中
        SmoothWeightedRoundRobin.getInstance().put(id, weight);
    }

    @Override
    public void onMessage(String s) {
        if (log.isDebugEnabled()) {
            log.debug("aria2 返回消息: {}", s);
        }
        JSONObject aria2Repose = JSON.parseObject(s);
        String method = aria2Repose.getString("method");
        if (method != null) {
            JSONArray params = aria2Repose.getJSONArray("params");
            String gid = params.getJSONObject(0).getString("gid");
            if (method.equals("aria2.onDownloadStart")) {
                // 开始下载，发送监听
                EventUtils.publishEvent(new DownloadTaskStatus(id, 1, gid, 0));
            }
            if (method.equals("aria2.onDownloadComplete")) {
                // 任务下载完成
                EventUtils.publishEvent(new DownloadTaskStatus(id, 2, gid, 1));
                // 删除内存任务
                EventUtils.publishEvent(new Aria2Task(id, gid));
            }
            if (method.equals("aria2.onDownloadError")) {
                // 重新处理下载失败的任务
                EventUtils.publishEvent(new DownloadTaskStatus(id, 3, gid, 1));
                // 删除失败任务
                EventUtils.publishEvent(new Aria2Task(id, gid));
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("关闭连接 -> {} -> code {} -> reason {} -> remote {}", id, code, reason, remote);
        // 在负载均衡中删除掉线的下载器
        SmoothWeightedRoundRobin.getInstance().remove(id);
    }

    @Override
    public void onError(Exception e) {
        log.error("发送错误", e);
    }

    @Override
    public boolean start(long timeout, TimeUnit timeUnit) throws InterruptedException {
        this.setConnectionLostTimeout(0);
        return this.connectBlocking(2, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        this.close();
    }
}
