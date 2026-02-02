package com.roukaixin.cronvideos.runner;


import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.listener.event.DownloaderEvent;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 连接下载器
 *
 * @author roukaixin
 * @date 2026/1/12 08:07
 */
@Slf4j
@Component
public class ConnectionDownloaderRunner implements CommandLineRunner {

    private final DownloaderMapper downloaderMapper;

    private final ApplicationEventPublisher applicationEventPublisher;


    public ConnectionDownloaderRunner(DownloaderMapper downloaderMapper,
                                      ApplicationEventPublisher applicationEventPublisher) {
        this.downloaderMapper = downloaderMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void run(String @NonNull ... args) {
        downloaderMapper.selectAllStream(result -> {
            Downloader downloader = result.getResultObject();
            applicationEventPublisher.publishEvent(new DownloaderEvent(DownloaderEvent.Operation.QUERY, downloader));
        });
    }
}
