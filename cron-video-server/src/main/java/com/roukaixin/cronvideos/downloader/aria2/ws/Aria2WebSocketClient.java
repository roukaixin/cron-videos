package com.roukaixin.cronvideos.downloader.aria2.ws;

import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.downloader.Client;
import com.roukaixin.cronvideos.downloader.Executor;
import com.roukaixin.cronvideos.downloader.aria2.MethodEnum;
import com.roukaixin.cronvideos.listener.event.Aria2TaskEvent;
import com.roukaixin.cronvideos.listener.event.DownloadTaskEvent;
import com.roukaixin.cronvideos.utils.EventUtils;
import com.roukaixin.cronvideos.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Aria2WebSocketClient extends WebSocketClient implements Client {

    private final Aria2WebSocketExecutor executor = new Aria2WebSocketExecutor(this);

    private final Long identifier;

    private final String password;

    private final int weight;

    private final CountDownLatch lock = new CountDownLatch(1);

    private boolean isStart = false;

    private final Map<String, MethodEnum> request = new ConcurrentHashMap<>(16);


    public Aria2WebSocketClient(Downloader downloader) {
        this.identifier = downloader.getId();
        this.password = downloader.getSecret();
        this.weight = downloader.getWeight();
        super(URI.create(downloader.getProtocol().name().toLowerCase(Locale.ROOT) + "://" + downloader.getHost() + ":" + downloader.getPort() + "/jsonrpc"));
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public Long identifier() {
        return this.identifier;
    }

    @Override
    public int weight() {
        return this.weight;
    }

    @Override
    public boolean start() {
        try {
            this.setConnectionLostTimeout(0);
            boolean connected = this.connectBlocking(2, TimeUnit.SECONDS);
            if (!connected) {
                return false;
            }
            if (lock.await(3, TimeUnit.SECONDS)) {
                return isStart;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Aria2WebSocketClient 下载器[{}]启动失败：", this.identifier, e);
            return false;
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean ping() {
        return false;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("Aria2WebSocketClient 下载器[{}]打开连接", identifier);
        this.send(buildVersionParams());
    }

    private String buildVersionParams() {
        synchronized (request) {
            Map<String, Object> map = new HashMap<>();
            String id = identifier + UUID.randomUUID().toString();
            map.put("id", id);
            map.put("method", MethodEnum.aria2_getVersion.getMethod());
            map.put("params", StringUtils.hasText(password) ? List.of("token:" + this.password) : new ArrayList<>());
            request.put(id, MethodEnum.aria2_getVersion);
            return JsonUtils.toJSONString(map);
        }
    }

    @Override
    public void onMessage(String message) {
        if (log.isDebugEnabled()) {
            log.debug("Aria2WebSocketClient 下载器[{}] 返回消息[{}]", this.identifier, message);
        }
        synchronized (request) {
            JsonNode repose = JsonUtils.readTree(message);
            String id = repose.get("id").asString();
            MethodEnum methodEnum = request.get(id);
            switch (methodEnum) {
                case aria2_getVersion -> {
                    String version = repose.path("result").path("version").asString("");
                    String errorMessage = repose.path("error").path("message").asString("");
                    if (StringUtils.hasText(version)) {
                        this.isStart = true;
                        lock.countDown();
                    }
                    if (StringUtils.hasText(errorMessage)) {
                        this.isStart = false;
                    }
                    request.remove(id);
                }
                case null -> {
                    log.info("Aria2WebSocketClient 下载器[{}] 不是本程序请求的结果", this.identifier);
                }
                default -> {
                    String method = repose.get("method").asString();
                    if (method != null) {
                        ArrayNode params = repose.withArrayProperty("params");
                        String gid = params.get(0).get("gid").asString();
                        if (method.equals(MethodEnum.aria2_onDownloadStart.getMethod())) {
                            // 开始下载，发送监听
                            EventUtils.publishEvent(new DownloadTaskEvent(identifier, 1, gid, 0));
                        }
                        if (method.equals(MethodEnum.aria2_onDownloadComplete.getMethod())) {
                            // 任务下载完成
                            EventUtils.publishEvent(new DownloadTaskEvent(identifier, 2, gid, 1));
                            // 删除内存任务
                            EventUtils.publishEvent(new Aria2TaskEvent(identifier, gid));
                        }
                        if (method.equals(MethodEnum.aria2_onDownloadError.getMethod())) {
                            // 重新处理下载失败的任务
                            EventUtils.publishEvent(new DownloadTaskEvent(identifier, 3, gid, 1));
                            // 删除失败任务
                            EventUtils.publishEvent(new Aria2TaskEvent(identifier, gid));
                        }
                    }
                }
            }

        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Aria2WebSocketClient 下载器[{}]关闭连接 code[{}] reason[{}] remote[{}]", identifier, code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        log.error("Aria2WebSocketClient 下载器[{}]发生错误", this.identifier, ex);
    }
}
