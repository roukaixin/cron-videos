package com.roukaixin.cronvideos.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.mapper.DownloadTaskMapper;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import com.roukaixin.cronvideos.utils.Aria2Utils;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.*;

@Slf4j
public class Aria2Handler extends TextWebSocketHandler {

    @Getter
    private final CompletableFuture<Boolean> connectionFuture = new CompletableFuture<>();

    private final ApplicationContext applicationContext;

    private ScheduledExecutorService scheduler;

    private final Long id;

    private final Integer weight;

    private final DownloadTaskMapper downloadTaskMapper;

    private final Aria2WebSocketPool aria2WebSocketPool;

    private final DownloaderMapper downloaderMapper;

    public Aria2Handler(ApplicationContext applicationContext,
                        Long id,
                        Integer weight,
                        DownloadTaskMapper downloadTaskMapper,
                        Aria2WebSocketPool aria2WebSocketPool,
                        DownloaderMapper downloaderMapper) {
        this.applicationContext = applicationContext;
        this.id = id;
        this.weight = weight;
        this.downloadTaskMapper = downloadTaskMapper;
        this.aria2WebSocketPool = aria2WebSocketPool;
        this.downloaderMapper = downloaderMapper;
    }

    /**
     * 建立连接后
     *
     * @param session session
     * @throws Exception ex
     */
    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws Exception {
        log.info("连接成功 {}", session.getId());
        aria2WebSocketPool.putSession(session, id);
        SmoothWeightedRoundRobin.getInstance().put(id, weight);
        connectionFuture.complete(true);
        cancelTimeout();
        super.afterConnectionEstablished(session);
    }

    /**
     * 处理文本信息
     *
     * @param session session
     * @param message 返回消息
     * @throws Exception 异常
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("{} -> aria2 返回消息: {}", message, message.getPayload());
        }
        JSONObject aria2Repose = JSON.parseObject(message.getPayload());
        String method = aria2Repose.getString("method");
        if (method != null) {
            JSONArray params = aria2Repose.getJSONArray("params");
            String gid = params.getJSONObject(0).getString("gid");
            if (method.equals("aria2.onDownloadStart")) {
                TimeUnit.SECONDS.sleep(1);
                // 任务开始下载,修改状态
                updateAria2TaskStatus(id, 1, gid);
            }
            if (method.equals("aria2.onDownloadComplete")) {
                TimeUnit.SECONDS.sleep(1);
                // 任务下载完成,修改状态
                DownloadTask downloadTask = downloadTaskMapper.selectOne(
                        Wrappers.<DownloadTask>lambdaQuery()
                                .eq(DownloadTask::getGid, gid)
                                .eq(DownloadTask::getDownloaderId, id));
                if (!ObjectUtils.isEmpty(downloadTask)) {
                    downloadTask.setStatus(2);
                    applicationContext.publishEvent(downloadTask);
                }
                removeDownloadResult(session, gid);
            }
            if (method.equals("aria2.onDownloadError")) {
                TimeUnit.SECONDS.sleep(1);
                // 重新处理下载失败的任务
                updateAria2TaskStatus(id, 3, gid);
                removeDownloadResult(session, gid);
            }
        }
        super.handleTextMessage(session, message);
    }

    @Override
    public void handleTransportError(@Nonnull WebSocketSession session, Throwable exception) throws Exception {
        log.info("连接失败 {}", exception.getMessage());
        super.handleTransportError(session, exception);
    }


    /**
     * 连接后关闭
     *
     * @param session session
     * @param status  status
     * @throws Exception ex
     */
    @Override
    public void afterConnectionClosed(@Nonnull WebSocketSession session, @Nonnull CloseStatus status) throws Exception {
        log.info("关闭连接 {} {} {}", session.getId(), status, id);
        // 手动停止 CloseStatus[code=1000, reason=null]
        if (status.getCode() == 1006) {
            // 强制关闭 CloseStatus[code=1006, reason=null]
            Downloader downloader = new Downloader();
            downloader.setId(id);
            downloader.setIsOnline(0);
            downloaderMapper.updateById(downloader);
        }
        aria2WebSocketPool.removeSession(session);
        aria2WebSocketPool.remove(id);
        // 重置id
        SmoothWeightedRoundRobin.getInstance().remove(id);
        super.afterConnectionClosed(session, status);
    }

    private void updateAria2TaskStatus(Long aria2ServiceId, Integer status, String gid) {
        downloadTaskMapper.update(Wrappers.<DownloadTask>lambdaUpdate()
                .set(DownloadTask::getStatus, status).eq(DownloadTask::getGid, gid).eq(DownloadTask::getDownloaderId, aria2ServiceId));
    }

    public void setTimeout(long timeout, TimeUnit unit) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            if (!connectionFuture.isDone()) {
                connectionFuture.completeExceptionally(new TimeoutException("Connection timed out"));
            }
        }, timeout, unit);
    }

    private void cancelTimeout() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    private void removeDownloadResult(@NonNull WebSocketSession session, String gid) {
        // 删除 aria2 任务
        Long aria2ServiceId = aria2WebSocketPool.getAria2ServiceId(session);
        Downloader downloader = downloaderMapper.selectById(aria2ServiceId);
        String removeDownloadResult = Aria2Utils.removeDownloadResult(
                downloader.getHost(),
                downloader.getPort(),
                JSONArray.of(
                        "token:" + downloader.getSecret(),
                        gid
                ).toJSONString()
        );
        if (log.isDebugEnabled()) {
            log.debug("aria2 删除错误任务 -> {}", removeDownloadResult);
        }
    }

}
