package com.roukaixin.cronvideos.listener;


import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.listener.event.Aria2Task;
import com.roukaixin.cronvideos.listener.event.DownloadTaskStatus;
import com.roukaixin.cronvideos.mapper.DownloadTaskMapper;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import com.roukaixin.cronvideos.utils.Aria2Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DownloaderListener {

    private final DownloaderMapper downloaderMapper;

    private final DownloadTaskMapper downloadTaskMapper;

    public DownloaderListener(DownloaderMapper downloaderMapper,
                              DownloadTaskMapper downloadTaskMapper) {
        this.downloaderMapper = downloaderMapper;
        this.downloadTaskMapper = downloadTaskMapper;
    }

    @EventListener
    @Async
    public void changeDownloadTaskStatus(DownloadTaskStatus event) {
        if (log.isDebugEnabled()) {
            log.debug("监听到改变下载任务 -> {}", event);
        }
        downloadTaskMapper.update(Wrappers.<DownloadTask>lambdaUpdate()
                .set(DownloadTask::getStatus, event.getStatus())
                .eq(DownloadTask::getGid, event.getGid())
                .eq(DownloadTask::getDownloaderId, event.getId()));
    }


    @EventListener
    @Async
    public void removeAria2Task(Aria2Task event) {
        if (log.isDebugEnabled()) {
            log.debug("监听到删除 aria2 任务 -> {}", event);
        }
        Downloader downloader = downloaderMapper.selectById(event.getId());
        if (downloader != null) {
            String removeDownloadResult = Aria2Utils.removeDownloadResult(
                    downloader.getHost(),
                    downloader.getPort(),
                    JSONArray.of(
                            "token:" + downloader.getSecret(),
                            event.getGid()
                    ).toJSONString()
            );
            if (log.isDebugEnabled()) {
                log.debug("aria2 删除错误任务 -> {}", removeDownloadResult);
            }
        }
    }

}
