package com.roukaixin.cronvideos.listener;


import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.listener.event.Aria2TaskEvent;
import com.roukaixin.cronvideos.listener.event.DownloadTaskEvent;
import com.roukaixin.cronvideos.mapper.DownloadTaskMapper;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import com.roukaixin.cronvideos.utils.Aria2Utils;
import com.roukaixin.cronvideos.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tools.jackson.databind.node.ArrayNode;

/**
 * 下载任务监听器
 *
 * @author roukaixin
 * @date 2026/1/13 19:09
 */
@Slf4j
@Component
public class DownloaderTaskListener {

    private final DownloaderMapper downloaderMapper;

    private final DownloadTaskMapper downloadTaskMapper;

    public DownloaderTaskListener(DownloaderMapper downloaderMapper,
                                  DownloadTaskMapper downloadTaskMapper) {
        this.downloaderMapper = downloaderMapper;
        this.downloadTaskMapper = downloadTaskMapper;
    }

    @Async
    @EventListener(classes = DownloadTaskEvent.class)
    public void changeDownloadTaskStatus(DownloadTaskEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("监听到改变下载任务 -> {}", event);
        }
        downloadTaskMapper.updateStatus(event.getStatus(), event.getGid(), event.getOriginalStatus(), event.getDownloaderId());
    }


    @Async
    @EventListener(classes = Aria2TaskEvent.class)
    public void removeAria2Task(Aria2TaskEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("监听到删除 aria2 任务 -> {}", event);
        }
        Downloader downloader = downloaderMapper.selectOneById(event.getId());
        if (downloader != null) {
            ArrayNode params = JsonUtils.createArrayNode();
            params.add("token:" + downloader.getSecret());
            params.add(event.getGid());
            String removeDownloadResult = Aria2Utils.removeDownloadResult(
                    downloader.getHost(),
                    downloader.getPort(),
                    params.toString()
            );
            if (log.isDebugEnabled()) {
                log.debug("aria2 删除错误任务 -> {}", removeDownloadResult);
            }
        }
    }

}
